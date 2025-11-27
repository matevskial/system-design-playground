package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RequestHandlers {

    private final List<RegisteredRequestHandler> requestHandlers = new ArrayList<>();

    public RequestSpecBuilder path(String path) {
        return new RequestSpecBuilder(this).path(path);
    }

    public RequestSpecBuilder method(HttpMethod httpMethod) {
        return new RequestSpecBuilder(this).method(httpMethod);
    }

    public Optional<RegisteredRequestHandler> query(Request simpleRequest) {
        return new RequestSpecBuilder(this)
                .path(simpleRequest.getPath())
                .method(simpleRequest.getHttpMethod())
                .query();
    }

    @Getter
    public static class RequestSpecBuilder {
        private final RequestHandlers requestHandlers;

        private String path;
        private HttpMethod httpMethod;

        public RequestSpecBuilder(RequestHandlers requestHandlers) {
            this.requestHandlers = requestHandlers;
        }

        public RequestSpecBuilder path(String path) {
            this.path = path;
            return this;
        }

        public RequestSpecBuilder method(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public void register(RequestHandlerFunction requestHandlerFunction) {
            requestHandlers.requestHandlers.add(new RegisteredRequestHandler(
                    this.path,
                    this.httpMethod,
                    requestHandlerFunction
            ));
        }

        public Optional<RegisteredRequestHandler> query() {
            for (RegisteredRequestHandler registeredHandler : requestHandlers.requestHandlers) {
                if (pathMatches(registeredHandler)) {
                    if (Objects.equals(registeredHandler.httpMethod(), httpMethod)) {
                        return Optional.of(registeredHandler);
                    }
                }
            }
            return Optional.empty();
        }

        private boolean pathMatches(RegisteredRequestHandler registeredHandler) {
            String[] pathSegments = this.path.split("/");
            String[] registeredPathSegments = registeredHandler.path().split("/");
            if (pathSegments.length != registeredPathSegments.length) {
                return false;
            }
            for (int i = 0; i < pathSegments.length; i++) {
                if (!(registeredPathSegments[i].startsWith("{") && registeredPathSegments[i].endsWith("}"))) {
                    if (!pathSegments[i].equals(registeredPathSegments[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
