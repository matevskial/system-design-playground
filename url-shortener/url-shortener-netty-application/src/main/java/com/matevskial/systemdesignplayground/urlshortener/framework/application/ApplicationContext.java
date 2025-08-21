package com.matevskial.systemdesignplayground.urlshortener.framework.application;

import java.util.ArrayList;
import java.util.List;

public class ApplicationContext {

    public static final ApplicationContext INSTANCE = new ApplicationContext();

    // TODO: replace with map or something for better perfomance if there are many beans?
    private final List<Object> beans = new ArrayList<>();

    public <T> void registerBean(Object bean, Class<T> clazz) {
        beans.add(bean);
    }

    public <T> T getBean(Class<T> clazz) {
        for (Object bean : beans) {
            if (clazz.isInstance(bean)) {
                return (T) bean;
            }
        }
        throw new ApplicationContextException("Bean not found");
    }
}
