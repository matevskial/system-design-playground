package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RequestHandlers {

    private final List<RegisteredRequestHandler> requestHandlers = new ArrayList<>();

    public RequestHandlersRegisterBegin register() {
        return new RequestHandlersRegisterBegin(this);
    }

    public RequestHandlersQueryBegin query() {
        return new RequestHandlersQueryBegin(this);
    }

    @RequiredArgsConstructor
    public static final class RequestHandlersRegisterBegin {
        private final RequestHandlers requestHandlers;
        private String path;

        public RequestHandlersRegister path(String path) {
            if (path == null) {
                throw new IllegalArgumentException("path cannot be null");
            }
            this.path = path;
            if (!path.startsWith("/")) {
                this.path = "/" + this.path;
            }
            return new RequestHandlersRegister(this);
        }
    }

    @RequiredArgsConstructor
    public static final class RequestHandlersRegister {
        private final RequestHandlersRegisterBegin requestHandlersRegisterBegin;
        private HttpMethod httpMethod;
        private RequestHandlerFunction requestHandlerFunction;

        public RequestHandlersRegister method(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public void handler(RequestHandlerFunction requestHandlerFunction) {
            this.requestHandlerFunction = requestHandlerFunction;
            register();
        }

        public void register() {
            requestHandlersRegisterBegin.requestHandlers.requestHandlers.add(new RegisteredRequestHandler(
                    requestHandlersRegisterBegin.path,
                    httpMethod,
                    requestHandlerFunction
            ));
        }
    }

    @RequiredArgsConstructor
    public static final class RequestHandlersQueryBegin {
        private final RequestHandlers requestHandlers;
        private String path;

        public RequestHandlersQuery path(String path) {
            this.path = path;
            return new RequestHandlersQuery(this);
        }

        public Optional<RegisteredRequestHandler> request(Request request) {
            return path(request.getPath()).method(request.getHttpMethod());
        }
    }

    @RequiredArgsConstructor
    public static final class RequestHandlersQuery {
        private final RequestHandlersQueryBegin requestHandlersQueryBegin;
        private HttpMethod httpMethod;

        public Optional<RegisteredRequestHandler> method(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return query();
        }

        public Optional<RegisteredRequestHandler> query() {
            for (RegisteredRequestHandler registeredHandler : requestHandlersQueryBegin.requestHandlers.requestHandlers) {
                if (pathMatches(registeredHandler)) {
                    if (Objects.equals(registeredHandler.httpMethod(), httpMethod)) {
                        return Optional.of(registeredHandler);
                    }
                }
            }
            return Optional.empty();
        }

        private boolean pathMatches(RegisteredRequestHandler registeredHandler) {
            String[] pathSegments = this.requestHandlersQueryBegin.path.split("/");
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
