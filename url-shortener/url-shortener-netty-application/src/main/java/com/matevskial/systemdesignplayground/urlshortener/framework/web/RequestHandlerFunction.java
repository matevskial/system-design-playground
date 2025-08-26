package com.matevskial.systemdesignplayground.urlshortener.framework.web;

@FunctionalInterface
public interface RequestHandlerFunction {
    void apply(Request request, Response response);
}
