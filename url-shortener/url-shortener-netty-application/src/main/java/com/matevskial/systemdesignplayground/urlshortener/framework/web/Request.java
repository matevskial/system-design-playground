package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class Request {

    @Getter
    private String path;

    @Getter
    @Setter
    private HttpMethod httpMethod;

    @Getter
    protected QueryParameters queryParameters;

    @Getter
    protected PathVariables pathVariables;

    @Getter
    protected Headers headers;

    @Getter
    protected Body body;

    public void setPath(String path) {
        if (path != null) {
            int firstSharpIndex = -1;
            int firstQuestionMarkIndex = -1;
            for (int i = 0; i < path.length(); i++) {
                if (path.charAt(i) == '?' && firstQuestionMarkIndex == -1) {
                    firstQuestionMarkIndex = i;
                }
                if (path.charAt(i) == '#' && firstSharpIndex == -1) {
                    firstSharpIndex = i;
                }
            }
            int substringEndIndex = firstSharpIndex != -1 && firstSharpIndex <= firstQuestionMarkIndex
                    ? firstSharpIndex
                    : firstQuestionMarkIndex;

            if (substringEndIndex == -1) {
                this.path = path;
            } else {
                this.path = path.substring(0, substringEndIndex);
            }
        }
    }

    public void setQueryParameters(String path) {
        this.queryParameters = new QueryParameters(path);
    }

    public void setPathVariables(String path, String registeredRequestHandlerPath) {
        this.pathVariables = new PathVariables(path, registeredRequestHandlerPath);
    }

    public String getPathVariable(String path) {
        return this.pathVariables.getPathVariable(path);
    }

    public boolean isFormData() {
        return Objects.requireNonNullElse(headers.getValue(Headers.CONTENT_TYPE), "").startsWith("multipart/form-data");
    }
}
