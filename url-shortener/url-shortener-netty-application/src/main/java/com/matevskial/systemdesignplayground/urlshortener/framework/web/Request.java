package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class Request {

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private HttpMethod httpMethod;

    @Getter
    protected QueryParameters queryParameters;
}
