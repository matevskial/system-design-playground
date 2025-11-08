package com.matevskial.systemdesignplayground.urlshortener.framework.application.config;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ApplicationConfig {

    private final Properties properties;

    @Getter
    private List<String> profiles = List.of();

    public ApplicationConfig() {
        this.properties = new Properties();
    }

    public void setConfigProperty(String configKey, Object value) {
        this.properties.put(configKey, value);
    }

    public Object getConfigProperty(String configKey) {
        return this.properties.getOrDefault(configKey, null);
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = new ArrayList<>(profiles);
    }
}
