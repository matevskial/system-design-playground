package com.matevskial.systemdesignplayground.urlshortener;

import com.matevskial.systemdesignplayground.urlshortener.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.application.ApplicationException;
import com.matevskial.systemdesignplayground.urlshortener.core.TsIdGenerator;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortener;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortenerProperties;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShorteners;
import com.matevskial.systemdesignplayground.urlshortener.hibernate.HibernateSessionManager;
import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import com.matevskial.systemdesignplayground.urlshortener.persistence.hibernate.UrlPersistenceImpl;
import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import com.matevskial.systemdesignplayground.urlshortener.tsid.TsIdManager;
import io.hypersistence.tsid.TSID;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;

public class UrlShortenerNettyApplication {

    public static void main(String[] args) {
        NioEventLoopGroup httpServerParentEventLoopGroup = null;
        NioEventLoopGroup httpServerChildEventLoopGroup = null;

        try {
            System.out.println("Initializing url-shortener netty application...");

            HibernateSessionManager.initializeSessionManager();

            ApplicationContext.registerBean(TsIdManager.tsIdFactory(), TSID.Factory.class);
            ApplicationContext.registerBean(new UrlPersistenceImpl(), UrlPersistenceImpl.class);
            UrlShortener urlShortener = UrlShorteners.base62Variant(new TsIdGenerator(ApplicationContext.getBean(TSID.Factory.class)));
            ApplicationContext.registerBean(urlShortener, UrlShortener.class);
            UrlShortenerProperties urlShortenerProperties = new UrlShortenerProperties("http://localhost:8080");
            ApplicationContext.registerBean(urlShortenerProperties, UrlShortenerProperties.class);
            UrlShortenerService urlShortenerService = new UrlShortenerService(
                    ApplicationContext.getBean(UrlPersistence.class),
                    ApplicationContext.getBean(UrlShortener.class),
                    ApplicationContext.getBean(UrlShortenerProperties.class)
            );
            ApplicationContext.registerBean(urlShortenerService, UrlShortenerService.class);

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
                                    .addLast("netty http encoder(outbound)", new HttpResponseEncoder());
                        }
                    });
            var channelFuture = httpServerBootstrap.bind(8080).syncUninterruptibly();
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
