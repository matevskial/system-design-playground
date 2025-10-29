package com.matevskial.systemdesignplayground.urlshortener.framework.application.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ApplicationConfigBuilder {

    private ApplicationConfig applicationConfig;

    public ApplicationConfigBuilder fromProperties() {
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig();
        }
        try {
            InputStream applicationPropertiesInputStream = ApplicationConfigBuilder.class.getClassLoader().getResourceAsStream("application.properties");
            Properties properties = new Properties();
            properties.load(applicationPropertiesInputStream);
            for (String key : properties.stringPropertyNames()) {
                applicationConfig.setConfigProperty(key, properties.getProperty(key));
            }
        } catch (Exception e) {
            throw new ApplicationConfigException(e.getMessage());
        }
        return this;
    }

    public ApplicationConfigBuilder fromYaml() {
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig();
        }
        try {
            InputStream applicationYmlInputStream = ApplicationConfigBuilder.class.getClassLoader().getResourceAsStream("application.yml");
            Yaml yaml = new Yaml();
            Iterable<Object> config = yaml.loadAll(applicationYmlInputStream);
            for (Object o : config) {
                Map<String, Object> map = (Map<String, Object>) o;
                addConfig(map);
            }
            return this;
        } catch (Exception e) {
            throw new ApplicationConfigException(e.getMessage());
        }
    }

    private void addConfig(Map<String, Object> map) {
        List<String> keyParts = new ArrayList<>(50);
        int recursionLevel = 0;
        addConfigRec(map, keyParts, recursionLevel);
    }

    private void addConfigRec(Object obj, List<String> configKeyParts, int recursionLevel) {
        if (recursionLevel >= 50) {
            throw new ApplicationConfigException("Recursion level " + recursionLevel + " exceeded");
        }

        if (obj instanceof Map map) {
            for (Object key : map.keySet()) {
                if (key instanceof String keyPart) {
                    configKeyParts.add(keyPart);
                    addConfigRec(map.get(key), configKeyParts, recursionLevel + 1);
                    configKeyParts.removeLast();
                }
            }
        } else {
            this.applicationConfig.setConfigProperty(String.join(".", configKeyParts), obj);
        }
    }

    public ApplicationConfig build() {
        return this.applicationConfig;
    }
}
