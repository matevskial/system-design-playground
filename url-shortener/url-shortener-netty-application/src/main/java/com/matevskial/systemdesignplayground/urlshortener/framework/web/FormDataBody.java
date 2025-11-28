package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormDataBody extends Body {

    private final Map<String, String> formItems;

    public FormDataBody(List<InterfaceHttpData> formItems) {
        try {
            this.formItems = new HashMap<>();
            for (InterfaceHttpData formItem : formItems) {
                if (formItem.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    HttpData formItemData = (HttpData) formItem;
                    this.formItems.put(formItem.getName(), formItemData.getString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getFormItem(String name) {
        return formItems.get(name);
    }
}
