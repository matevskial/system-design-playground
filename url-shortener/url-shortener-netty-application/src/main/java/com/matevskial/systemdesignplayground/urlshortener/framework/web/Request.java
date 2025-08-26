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

    @Getter
    protected PathVariables pathVariables;

    public void setQueryParameters(String path) {
        this.queryParameters = new QueryParameters(path);
    }

    public void setPathVariables(String path, String registeredRequestHandlerPath) {
        this.pathVariables = new PathVariables(path, registeredRequestHandlerPath);
    }

    public String getPathVariable(String path) {
        return this.pathVariables.getPathVariable(path);
    }
}
