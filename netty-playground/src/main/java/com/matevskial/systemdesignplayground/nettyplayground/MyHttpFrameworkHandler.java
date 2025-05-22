package com.matevskial.systemdesignplayground.nettyplayground;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.util.Objects;

import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;

public class MyHttpFrameworkHandler extends ChannelDuplexHandler {

    private HttpRequest request;
    private ByteBuf body;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channel read " + msg);
        if (msg instanceof HttpRequest httpRequest) {
            if (!httpRequest.decoderResult().isSuccess()) {
                throw new IllegalStateException("request decoder failure");
            }

            request = httpRequest;

            if (HttpUtil.is100ContinueExpected(httpRequest)) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE,
                        Unpooled.EMPTY_BUFFER);
                ctx.write(response);
            }

            int contentLength = Objects.requireNonNullElse(request.headers().getInt(HttpHeaderNames.CONTENT_LENGTH), 0);
            if (body != null) {
                body.release();
            }
            body = Unpooled.buffer(contentLength, contentLength);
        } else if (msg instanceof HttpContent httpContent) {
            if (!httpContent.decoderResult().isSuccess()) {
                throw new IllegalStateException("request decoder failure");
            }

            if (body.writableBytes() >= httpContent.content().readableBytes()) {
                body.writeBytes(httpContent.content());
            }
            if (msg instanceof LastHttpContent) {
                byte[] bytes = new byte[body.readableBytes()];
                body.readBytes(bytes);
                ctx.fireChannelRead(MyHttpRequest.builder().data(bytes).contentType(request.headers().get(HttpHeaderNames.CONTENT_TYPE)).build());
            }
        } else {
            throw new IllegalStateException("Unexpected msg type: " + msg.getClass());
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("handler removed");
        if (body != null) {
            body.release();
        }
    }

    /**
     * Flushes ctx when read is completed so that ctx is available for the next incoming messages
     * This video explains that this has performance benefits https://youtu.be/DKJ0w30M0vg?list=PLk-4xlJFG77ZU1MqdPy_K2Ma1wPljR-fU&t=2486
     * Note: Does this overriden method really need to exist at all to flush? The lower-level inbound handler should handle this, like HttpDecoder for example
     *   Answer: yes!!, HttpDecoderHandler does not flush, it's up to us to do that
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        writeAndFlushInternalServerErrorResponse(ctx, cause);
    }

    private void writeAndFlushInternalServerErrorResponse(ChannelHandlerContext ctx, Throwable cause) {
        DefaultFullHttpResponse defaultResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        ctx.writeAndFlush(defaultResponse).addListener(ChannelFutureListener.CLOSE);
    }

    // TODO: investigate why the request hangs in postman when code inside this function throws exception that is not handled, is there an exceptionCaught for write operations?
    @Override
    public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) {
        DefaultFullHttpResponse response = null;
        if (obj instanceof MyHttpResponse myHttpResponse) {
            try {
                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(myHttpResponse.getStatusCode()),
                        Unpooled.copiedBuffer(myHttpResponse.getBody())
                );
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, Objects.requireNonNullElse(myHttpResponse.getContentType(), TEXT_PLAIN));
            } catch (Exception e) {
                response = null;
            }
        }

        if (response == null) {
            writeAndFlushInternalServerErrorResponse(ctx, null);
        } else {
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (keepAlive) {
                System.out.println("Should keep alive");
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            ctx.writeAndFlush(response);

            if (!keepAlive) {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
