package com.matevskial.systemdesignplayground.urlshortener.framework.web.netty;

import com.matevskial.systemdesignplayground.urlshortener.framework.web.*;
import com.matevskial.systemdesignplayground.urlshortener.framework.web.HttpMethod;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Promise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.*;

@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
public class HttpNettyHandler extends ChannelInboundHandlerAdapter {

    private final ExecutorService executorService = new ThreadPoolExecutor(25, 200, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private final RequestHandlers requestHandlers;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest request) {
            Promise<Response> promise = null;
            try {
                promise = ctx.executor().newPromise();
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

                Request simpleRequest = mapFromNettyRequestToSimpleRequest(request);
                Optional<RegisteredRequestHandler> requestHandlerOptional = requestHandlers.query(simpleRequest);
                if (requestHandlerOptional.isPresent()) {
                    final Promise<Response> finalPromise = promise;
                    executorService.submit(() -> {
                        try {
                            Request frameworkRequest = mapFromNettyRequest(request, requestHandlerOptional.get());
                            Response response = new NettyResponse();
                            requestHandlerOptional.get().requestHandlerFunction().apply(frameworkRequest, response);
                            finalPromise.setSuccess(response);
                        } catch (Exception e) {
                            finalPromise.setFailure(e);
                        }
                    });
                } else {
                    throw new RuntimeException("No request handler found");
                }
            } catch (Exception e) {
                if (promise != null) {
                    promise.setFailure(e);
                } else {
                    throw e;
                }
            }
        } else {
            throw new RuntimeException("Did not receive full http request");
        }
    }

    private Request mapFromNettyRequestToSimpleRequest(FullHttpRequest request) {
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.setPath(request.uri());
        nettyRequest.setHttpMethod(mapFromNettyHttpMethod(request.method()));
        return nettyRequest;
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
        log.error("Exception during request handling", cause);
        writeAndFlushInternalServerErrorResponse(ctx, cause).addListener(ChannelFutureListener.CLOSE);
    }

    private ChannelFuture writeAndFlushInternalServerErrorResponse(ChannelHandlerContext ctx, Throwable cause) {
        DefaultFullHttpResponse defaultResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        return ctx.writeAndFlush(defaultResponse);
    }
}
