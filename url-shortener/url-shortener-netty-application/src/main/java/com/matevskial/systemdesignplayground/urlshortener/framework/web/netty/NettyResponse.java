package com.matevskial.systemdesignplayground.urlshortener.framework.web.netty;

import com.matevskial.systemdesignplayground.urlshortener.framework.web.Response;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
class NettyResponse extends Response {

    private static final String TEXT_PLAIN_UTF8 = "text/plain;charset=UTF-8";

    public void setContent(FullHttpResponse nettyResponse) {
        if (textBody != null) {
            byte[] bytes = textBody.getBytes(StandardCharsets.UTF_8);
            nettyResponse.content().writeBytes(bytes);
            nettyResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
            nettyResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, TEXT_PLAIN_UTF8);
        }
    }

    public void setHeaders(FullHttpResponse nettyResponse) {
        if (status == 201 || status == 302) {
            nettyResponse.headers().set(HttpHeaderNames.LOCATION, locationHeaderValue);
        }
    }
}
