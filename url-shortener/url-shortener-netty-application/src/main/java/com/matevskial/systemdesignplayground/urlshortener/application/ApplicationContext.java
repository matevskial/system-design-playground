package com.matevskial.systemdesignplayground.urlshortener.application;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationContext {

    // TODO: replace with map or something for better perfomance if there are many beans?
    private static final List<Object> beans = new ArrayList<>();

    public static <T> void registerBean(Object bean, Class<T> clazz) {
        beans.add(bean);
    }

    public static <T> T getBean(Class<T> clazz) {
        for (Object bean : beans) {
            if (clazz.isInstance(bean)) {
                return (T) bean;
            }
        }
        return null;
    }
}
