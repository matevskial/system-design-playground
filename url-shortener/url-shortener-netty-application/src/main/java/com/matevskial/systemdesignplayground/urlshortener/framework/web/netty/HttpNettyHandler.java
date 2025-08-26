package com.matevskial.systemdesignplayground.urlshortener.framework.web.netty;

import com.matevskial.systemdesignplayground.urlshortener.framework.web.*;
import com.matevskial.systemdesignplayground.urlshortener.framework.web.HttpMethod;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Promise;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ChannelHandler.Sharable
@RequiredArgsConstructor
public class HttpNettyHandler extends ChannelInboundHandlerAdapter {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final RequestHandlers requestHandlers;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest request) {
            Promise<Response> promise = ctx.executor().newPromise();
            Optional<RegisteredRequestHandler> requestHandlerOptional = requestHandlers
                    .query()
                    .path(request.uri())
                    .method(mapFromNettyHttpMethod(request.method()));
            if (requestHandlerOptional.isPresent()) {
                executorService.submit(() -> {
                    Request frameworkRequest = mapFromNettyRequest(request, requestHandlerOptional.get());
                    Response response = new NettyResponse();
                    requestHandlerOptional.get().requestHandlerFunction().apply(frameworkRequest, response);
                    promise.setSuccess(response);
                });
                promise.addListener(future -> {
                    if (future.isSuccess()) {
                        try {
                            DefaultFullHttpResponse defaultResponse = mapFromFrameworkResponse((Response) future.getNow());
                            ctx.writeAndFlush(defaultResponse).addListener(ChannelFutureListener.CLOSE);
                        } catch (Exception e) {
                            exceptionCaught(ctx, e);
                        }
                    } else{
                        exceptionCaught(ctx, future.cause());
                    }
                });
            } else {
                throw new RuntimeException("No request handler found");
            }
        }
    }

    private Request mapFromNettyRequest(FullHttpRequest request, RegisteredRequestHandler requestHandler) {
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.setPath(request.uri());
        nettyRequest.setHttpMethod(mapFromNettyHttpMethod(request.method()));
        nettyRequest.setQueryParameters(request.uri());
        nettyRequest.setPathVariables(request.uri(), requestHandler.path());
        nettyRequest.setHeaders(request);
        nettyRequest.setBody(request);
        return nettyRequest;
    }

    private DefaultFullHttpResponse mapFromFrameworkResponse(Response frameworkResponse) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(frameworkResponse.getStatus()));
        if (frameworkResponse instanceof NettyResponse frameworkNettyResponse) {
            frameworkNettyResponse.setContent(response);
            frameworkNettyResponse.setHeaders(response);
        }
        return response;
    }

    private HttpMethod mapFromNettyHttpMethod(io.netty.handler.codec.http.HttpMethod method) {
        for  (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.name().equals(method.name())) {
                return httpMethod;
            }
        }
        return null;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        writeAndFlushInternalServerErrorResponse(ctx, cause).addListener(ChannelFutureListener.CLOSE);
    }

    private ChannelFuture writeAndFlushInternalServerErrorResponse(ChannelHandlerContext ctx, Throwable cause) {
        DefaultFullHttpResponse defaultResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        return ctx.writeAndFlush(defaultResponse);
    }
}
