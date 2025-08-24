package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import lombok.Getter;
import lombok.Setter;

public abstract class Response {

    @Getter
    protected int status = 200;
    @Getter
    @Setter
    protected String locationHeaderValue;

    protected String textBody;

    public void setStatus(int status) {
        this.status = status;
        if (status == 201 && locationHeaderValue == null) {
            throw new IllegalStateException("location header value is required");
        }
    }
    public void setText(String textBody) {
        this.textBody = textBody;
    }
}
