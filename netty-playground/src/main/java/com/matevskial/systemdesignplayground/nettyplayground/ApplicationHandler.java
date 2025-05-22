package com.matevskial.systemdesignplayground.nettyplayground;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;

public class ApplicationHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * Should not block handler methods.
     * Heavy computation or blocking stuff should be offloaded to a separate thread pool
     * Even though the channel will always use the chosen event loop, the event loop might handle multiple channels
     * so we should not do blocking stuff here.
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MyHttpRequest myHttpRequest) {
            var promise = ctx.executor().newPromise();
            CompletableFuture.runAsync(() -> {
                try {
                    System.out.println("Simulating doing some work for the request");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    promise.setFailure(e);
                }
                System.out.println("Done simulating some work for the request");
                promise.setSuccess(MyHttpResponse.builder().statusCode(200).body(myHttpRequest.getData()).build());
            });

            promise.addListener(future -> {
                if (future.isSuccess()) {
                    try {
                        // if we use ctx.write curl and postman hang, but if we use writeAndFlush,
                        // then curl and postman don't hang and work normally, WHY?
                        // answer: I wrongly used ctx.write in the lowerlevel outbound handler(MyHttpFrameworkHandler)
                        //   because I mistakenly thought writeAndFlush alone closes the connection
                        ctx.write(future.getNow());
//                        ctx.writeAndFlush(future.getNow());
                    } catch (Exception e) {
                        ctx.fireExceptionCaught(e);
                    }
                } else {
                    ctx.fireExceptionCaught(future.cause());
                }
            });
            System.out.println("Done setting up offload work");

//            try {
//                System.out.println("Simulating doing some work for the request");
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                throw e;
//            }
//            // if we have only this in ApplicationHandler(no promise and async) then curl and postman do not hang.
//            // why is it acceptable to use write here but not in promise listener?
//            ctx.write(MyHttpResponse.builder().statusCode(200).body(myHttpRequest.getData()).build());
        } else {
            throw new IllegalStateException("Unexpected msg type: " + msg.getClass());
        }
    }

    // TODO: Check if this exception can be propagated back to the nearest outbound handler
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        MyHttpResponse response = MyHttpResponse.builder().statusCode(500).build();
        ctx.write(response);
    }
}
