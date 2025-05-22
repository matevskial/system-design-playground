package com.matevskial.systemdesignplayground.nettyplayground;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;

public class NettyPlaygroundApplication {

    public static void main(String[] args) {
        var parentEventLoopGroup = new NioEventLoopGroup(1);
        var childEventLoopGroup = new NioEventLoopGroup();

        try {
            var serverBootstrap = new ServerBootstrap()
                    .group(parentEventLoopGroup, childEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        // basically for every request an instance of these handlers(decoders, encoders, etc) is created
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("netty http decoder(inbound)", new HttpRequestDecoder())
                                    .addLast("netty http encoder(outbound)", new HttpResponseEncoder())
                                    .addLast("my http framework duplex(inbound and outbound)", new MyHttpFrameworkHandler())
                                    .addLast("application business logic handler(inbound)", new ApplicationHandler());
                        }
                    });

            var channelFuture = serverBootstrap.bind(8080).syncUninterruptibly();
            channelFuture.channel().closeFuture().syncUninterruptibly();
        } finally {
            try {
                Future<?> parentShutdown = parentEventLoopGroup.shutdownGracefully();
                Future<?> childShutdown = childEventLoopGroup.shutdownGracefully();
                parentShutdown.get();
                childShutdown.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
