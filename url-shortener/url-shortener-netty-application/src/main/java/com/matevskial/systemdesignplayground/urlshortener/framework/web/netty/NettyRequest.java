package com.matevskial.systemdesignplayground.urlshortener.framework.web.netty;

import com.matevskial.systemdesignplayground.urlshortener.framework.web.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.List;

class NettyRequest extends Request {

    public void setHeaders(FullHttpRequest request) {
        headers = new Headers(request.headers());
    }

    public void setBody(FullHttpRequest request) {
        if (headers == null) {
            throw new IllegalStateException("Should set headers before setting body");
        }
        if (isFormData()) {
            HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(request);
            List<InterfaceHttpData> formItems = decoder.getBodyHttpDatas();
            body = new FormDataBody(formItems);
        }
    }

    public static Request basicRequest(FullHttpRequest request) {
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.setPath(request.uri());
        nettyRequest.setHttpMethod(mapFromNettyHttpMethod(request.method()));
        return nettyRequest;
    }

    public static NettyRequest withoutBodyRequest(FullHttpRequest request, RegisteredRequestHandler requestHandler) {
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.setPath(request.uri());
        nettyRequest.setHttpMethod(mapFromNettyHttpMethod(request.method()));
        nettyRequest.setQueryParameters(request.uri());
        nettyRequest.setPathVariables(request.uri(), requestHandler.path());
        nettyRequest.setHeaders(request);
        return nettyRequest;
    }

    public static Request fullRequest(FullHttpRequest request, RegisteredRequestHandler requestHandler) {
        NettyRequest nettyRequest = withoutBodyRequest(request, requestHandler);
        nettyRequest.setBody(request);
        return nettyRequest;
    }

    private static HttpMethod mapFromNettyHttpMethod(io.netty.handler.codec.http.HttpMethod method) {
        for  (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.name().equals(method.name())) {
                return httpMethod;
            }
        }
        return null;
    }
}
