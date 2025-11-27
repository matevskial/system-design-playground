package com.matevskial.systemdesignplayground.urlshortener;

import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationException;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.config.ApplicationConfig;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.config.ApplicationConfigReader;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.config.ApplicationConfigKeys;
import com.matevskial.systemdesignplayground.urlshortener.framework.logging.logback.LogbackApplicationContextManager;
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
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class UrlShortenerNettyApplication {

    public static void main(String[] args) {
        ApplicationConfig applicationConfig;
        ApplicationContext applicationContext;
        try {
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

            applicationConfig = new ApplicationConfigReader()
                    .profiles(profiles)
                    .fromProperties()
                    .fromYaml()
                    .read();

            applicationContext = new ApplicationContext(applicationConfig);
            LogbackApplicationContextManager logbackApplicationContextManager = new LogbackApplicationContextManager();
            logbackApplicationContextManager.manage(applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return; // to satisfy compiler that complains that variables above this try-catch might not be initialized
        }

        int exitCode = 0;
        NioEventLoopGroup httpServerParentEventLoopGroup = null;
        NioEventLoopGroup httpServerChildEventLoopGroup = null;

        try {
            log.info("Starting url-shortener netty application...");
            if (applicationConfig.getProfiles().isEmpty()) {
                log.info("No profile activated. Using default profile.");
            } else {
                log.info("Profiles activated: {}", applicationConfig.getProfiles());
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
            log.info("Started url-shortener netty application on port {}", httpPort);
            channelFuture.channel().closeFuture().syncUninterruptibly();
        } catch (Exception e) {
            exitCode = 1;
            if (e instanceof ApplicationException) {
                log.error("Failed initializing application", e);
            } else {
                log.error("Exception", e);
            }
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
                exitCode = 1;
                log.error("Exception", e);
            }
        }
        System.exit(exitCode);
    }
}
