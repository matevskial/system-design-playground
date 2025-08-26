package com.matevskial.systemdesignplayground.urlshortener.framework.web;

public record RegisteredRequestHandler(
        String path,
        HttpMethod httpMethod,
        RequestHandlerFunction requestHandlerFunction
) {
}
