package com.matevskial.systemdesignplayground.urlshortener.framework.web.netty;

import com.matevskial.systemdesignplayground.urlshortener.framework.web.QueryParameters;
import com.matevskial.systemdesignplayground.urlshortener.framework.web.Request;
import io.netty.handler.codec.http.FullHttpRequest;

class NettyRequest extends Request {

    public void setQueryParameters(FullHttpRequest request) {
        this.queryParameters = new QueryParameters(request.uri());
    }

    public void setHeaders(FullHttpRequest request) {

    }

    public void setBody(FullHttpRequest request) {

    }
}
