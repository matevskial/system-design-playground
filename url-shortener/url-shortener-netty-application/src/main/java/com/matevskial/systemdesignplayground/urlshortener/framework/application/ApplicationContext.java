package com.matevskial.systemdesignplayground.urlshortener.framework.application;

import com.matevskial.systemdesignplayground.urlshortener.framework.application.config.ApplicationConfig;

import java.util.ArrayList;
import java.util.List;

public class ApplicationContext {

    private final ApplicationConfig config;

    public ApplicationContext(ApplicationConfig config) {
        this.config = config;
    }

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

    public <T> T getConfigProperty(String configKey, Class<T> clazz, Object... defaultValue) {
        Object value = config.getConfigProperty(configKey);
        if (value == null) {
            if (defaultValue != null && defaultValue.length > 0) {
                return (T) defaultValue[0];
            } else {
                return null;
            }
        }
        return (T) value;
    }
}
