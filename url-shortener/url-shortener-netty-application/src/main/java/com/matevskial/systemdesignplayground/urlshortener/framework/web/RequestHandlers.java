package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RequestHandlers {

    private final List<RequestHandlerRegisterEnd> requestHandlers = new ArrayList<>();

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
            this.path = path;
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
            requestHandlersRegisterBegin.requestHandlers.requestHandlers.add(new RequestHandlerRegisterEnd(this));
        }
    }

    @RequiredArgsConstructor
    public static final class RequestHandlerRegisterEnd {
        private final RequestHandlersRegister requestHandlersRegister;
    }

    @FunctionalInterface
    public interface RequestHandlerFunction {
        void apply(Request request, Response response);
    }

    @RequiredArgsConstructor
    public static final class RequestHandlersQueryBegin {
        private final RequestHandlers requestHandlers;
        private String path;

        public RequestHandlersQuery path(String path) {
            this.path = path;
            if (path != null) {
                int end = path.indexOf('?');
                if (end == -1) {
                    end = path.length();
                }
                this.path = path.substring(0, end);
            }
            return new RequestHandlersQuery(this);
        }
    }

    @RequiredArgsConstructor
    public static final class RequestHandlersQuery {
        private final RequestHandlersQueryBegin requestHandlersQueryBegin;
        private HttpMethod httpMethod;

        public Optional<RequestHandlerFunction> method(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return query();
        }

        public Optional<RequestHandlerFunction> query() {
            for (RequestHandlerRegisterEnd registeredHandler : requestHandlersQueryBegin.requestHandlers.requestHandlers) {
                if (Objects.equals(registeredHandler.requestHandlersRegister.requestHandlersRegisterBegin.path, requestHandlersQueryBegin.path)) {
                    if (Objects.equals(registeredHandler.requestHandlersRegister.httpMethod, httpMethod)) {
                        return Optional.ofNullable(registeredHandler.requestHandlersRegister.requestHandlerFunction);
                    }
                }
            }
            return Optional.empty();
        }
    }
}
