package com.matevskial.systemdesignplayground.nettyplayground;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;

public class NettyPlaygroundServerApplication {

    public static void main(String[] args) {
        var httpServerParentEventLoopGroup = new NioEventLoopGroup(1);
        var httpServerChildEventLoopGroup = new NioEventLoopGroup();

        var echoTcpServerParentEventLoopGroup = new NioEventLoopGroup(1);
        var echoTcpServerChildEventLoopGroup = new NioEventLoopGroup();

        var echoTcpServerHandler = new EchoTcpServerHandler();

        try {
            var httpServerBootstrap = new ServerBootstrap()
                    .group(httpServerParentEventLoopGroup, httpServerChildEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        // basically for every request an instance of these handlers(decoders, encoders, etc) is created
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline()
                                    .addLast("netty http decoder(inbound)", new HttpRequestDecoder())
                                    .addLast("netty http encoder(outbound)", new HttpResponseEncoder())
                                    .addLast("my http framework duplex(inbound and outbound)", new MyHttpFrameworkHandler())
                                    .addLast("application business logic handler(inbound)", new ApplicationHandler());
                        }
                    });

            var echoTcpServerBootstrap = new ServerBootstrap()
                    .group(echoTcpServerParentEventLoopGroup, echoTcpServerChildEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline()
                                    .addLast("netty echo handler", echoTcpServerHandler);
                        }
                    });

            Thread httpServerThread = new Thread(() -> {
                System.out.println("Starting netty http server");
                var channelFuture = httpServerBootstrap.bind(8080).syncUninterruptibly();
                channelFuture.channel().closeFuture().syncUninterruptibly();
            });

            Thread echoTcpServerThread = new Thread(() -> {
                System.out.println("Starting netty echo tcp server");
                var channelFuture = echoTcpServerBootstrap.bind(8081).syncUninterruptibly();
                channelFuture.channel().closeFuture().syncUninterruptibly();
            });

            httpServerThread.start();
            echoTcpServerThread.start();
            httpServerThread.join();
            echoTcpServerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                Future<?> parentHttpServerEventLoopGroupShutdown = httpServerParentEventLoopGroup.shutdownGracefully();
                Future<?> childHttpServerEventLoopGroupShutdown = httpServerChildEventLoopGroup.shutdownGracefully();

                Future<?> parentEchoTcpServerEventLoopGroupShutdown = echoTcpServerParentEventLoopGroup.shutdownGracefully();
                Future<?> childEchoTcpServerEventLoopGroupShutdown = echoTcpServerChildEventLoopGroup.shutdownGracefully();

                parentHttpServerEventLoopGroupShutdown.get();
                childHttpServerEventLoopGroupShutdown.get();
                parentEchoTcpServerEventLoopGroupShutdown.get();
                childEchoTcpServerEventLoopGroupShutdown.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
