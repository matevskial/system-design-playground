package com.matevskial.systemdesignplayground.urlshortener.framework.application.config;

import java.util.Properties;

public class ApplicationConfig {

    private final Properties properties;

    public ApplicationConfig() {
        this.properties = new Properties();
    }

    public void setConfigProperty(String configKey, Object value) {
        this.properties.put(configKey, value);
    }

    public Object getConfigProperty(String configKey) {
        return this.properties.getOrDefault(configKey, null);
    }
}
