package com.matevskial.systemdesignplayground.urlshortener;

import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationException;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.config.ApplicationConfig;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.config.ApplicationConfigReader;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.config.ApplicationConfigKeys;
import com.matevskial.systemdesignplayground.urlshortener.spring.TransactionWithSpringApplicationContextManager;
import com.matevskial.systemdesignplayground.urlshortener.framework.web.RequestHandlers;
import com.matevskial.systemdesignplayground.urlshortener.framework.web.netty.HttpNettyHandler;
import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import com.matevskial.systemdesignplayground.urlshortener.spring.jdbc.SpringJdbcApplicationContextManager;
import com.matevskial.systemdesignplayground.urlshortener.tsid.TsIdApplicationContextManager;
import com.matevskial.systemdesignplayground.urlshortener.web.ShortenedToOriginalUrlRedirectionHandler;
import com.matevskial.systemdesignplayground.urlshortener.web.UrlShortenerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;

import java.util.Arrays;
import java.util.List;

public class UrlShortenerNettyApplication {

    public static void main(String[] args) {
        List<String> profiles = List.of();
        if (args != null) {
            for (String arg : args) {
                if (arg != null && arg.startsWith(ApplicationConfigKeys.PROFILES)) {
                    String[] argParts = arg.split("=");
                    if (argParts.length > 1) {
                        profiles = Arrays.asList(argParts[1].trim().split(","));
                    }
                }
            }
        }

        ApplicationConfig applicationConfig = new ApplicationConfigReader()
                .profiles(profiles)
                .fromProperties()
                .fromYaml()
                .read();

        ApplicationContext applicationContext = new ApplicationContext(applicationConfig);

        NioEventLoopGroup httpServerParentEventLoopGroup = null;
        NioEventLoopGroup httpServerChildEventLoopGroup = null;

        try {
            System.out.println("Initializing url-shortener netty application...");
            if (applicationConfig.getProfiles().isEmpty()) {
                System.out.println("No profile activated. Using default profile.");
            } else {
                System.out.println("Profiles activated: %s".formatted(applicationConfig.getProfiles()));
            }

            TsIdApplicationContextManager tsIdApplicationContextManager = new TsIdApplicationContextManager();
            tsIdApplicationContextManager.manage(applicationContext);

            SpringJdbcApplicationContextManager springJdbcApplicationContextManager = new SpringJdbcApplicationContextManager();
            springJdbcApplicationContextManager.manage(applicationContext);

            TransactionWithSpringApplicationContextManager transactionManager = new TransactionWithSpringApplicationContextManager();
            transactionManager.manage(applicationContext);

            UrlShortenerApplicationContextManager urlShortenerApplicationContextManager = new UrlShortenerApplicationContextManager();
            urlShortenerApplicationContextManager.manage(applicationContext);

            RequestHandlers requestHandlers = new RequestHandlers();
            HttpNettyHandler httpNettyHandler = new HttpNettyHandler(requestHandlers);

            UrlShortenerHandler urlShortenerHandler = new UrlShortenerHandler(applicationContext.getBean(UrlShortenerService.class));
            urlShortenerHandler.setupHandlers(requestHandlers);

            ShortenedToOriginalUrlRedirectionHandler shortenedToOriginalUrlRedirectionHandler = new ShortenedToOriginalUrlRedirectionHandler(applicationContext.getBean(UrlShortenerService.class));
            shortenedToOriginalUrlRedirectionHandler.setupHandlers(requestHandlers);

            int httpPort = applicationContext.getConfigProperty("server.port", Integer.class, 8080);
            System.out.println("Starting url-shortener netty application...");

            httpServerParentEventLoopGroup = new NioEventLoopGroup(1);
            httpServerChildEventLoopGroup = new NioEventLoopGroup();

            var httpServerBootstrap = new ServerBootstrap()
                    .group(httpServerParentEventLoopGroup, httpServerChildEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline()
                                    .addLast("netty http decoder(inbound)", new HttpRequestDecoder())
                                    .addLast("netty http encoder(outbound)", new HttpResponseEncoder())
                                    .addLast("netty http object aggregator(inbound)", new HttpObjectAggregator(1048576))
                                    .addLast("netty http framework(inbound)", httpNettyHandler);
                        }
                    });
            var channelFuture = httpServerBootstrap.bind(httpPort).syncUninterruptibly();
            System.out.println("Started url-shortener netty application on port %s".formatted(httpPort));
            channelFuture.channel().closeFuture().syncUninterruptibly();
        } catch (ApplicationException e) {
            System.out.println("Failed initializing application: %s".formatted(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Exception: %s".formatted(e.getMessage()));
        } finally {
            try {
                Future<?> parentHttpServerEventLoopGroupShutdown = null;
                Future<?> childHttpServerEventLoopGroupShutdown = null;

                if (httpServerParentEventLoopGroup != null) {
                    parentHttpServerEventLoopGroupShutdown = httpServerParentEventLoopGroup.shutdownGracefully();
                }
                if (httpServerChildEventLoopGroup != null) {
                    childHttpServerEventLoopGroupShutdown = httpServerChildEventLoopGroup.shutdownGracefully();
                }

                if (parentHttpServerEventLoopGroupShutdown != null) {
                    parentHttpServerEventLoopGroupShutdown.get();
                }
                if (childHttpServerEventLoopGroupShutdown != null) {
                    childHttpServerEventLoopGroupShutdown.get();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
