package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    public static final String CONTENT_TYPE = "content-type";

    private final Map<String, List<String>> headers;

    public Headers(HttpHeaders headers) {
        this.headers = new HashMap<>();
        headers.iteratorAsString().forEachRemaining(entry -> {
            this.headers.computeIfAbsent(entry.getKey().toLowerCase(), k -> new ArrayList<>()).add(entry.getValue());
        });
    }

    public String getValue(String headerName) {
        List<String> values = headers.get(headerName);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.getFirst();
    }
}
