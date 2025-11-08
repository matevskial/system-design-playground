package com.matevskial.systemdesignplayground.urlshortener.framework.application.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ApplicationConfigReader {

    private ApplicationConfig applicationConfig;
    private List<String> profiles = List.of();
    private boolean fromProperties = false;
    private boolean fromYaml = false;

    public ApplicationConfigReader fromProperties() {
        this.fromProperties = true;
        return this;
    }

    public ApplicationConfigReader fromYaml() {
        this.fromYaml = true;
        return this;
    }

    public ApplicationConfig read() {
        this.applicationConfig = new ApplicationConfig();
        readConfig("application");
        Object profilesFromProperties = applicationConfig.getConfigProperty(ApplicationConfigKeys.PROFILES);
        if (profilesFromProperties instanceof String profilesFromPropertiesString) {
            this.profiles = new ArrayList<>(this.profiles);
            this.profiles.addAll(Arrays.asList(profilesFromPropertiesString.split(",")));
        } else if (profilesFromProperties instanceof List profilesFromPropertiesList) {
            this.profiles = new ArrayList<>(this.profiles);
            profilesFromPropertiesList.forEach(profile -> {
                this.profiles.add(profile.toString());
            });
        }
        this.applicationConfig.setProfiles(profiles);
        for (String profile : this.profiles) {
            readConfig("application-%s".formatted(profile));
        }
        return this.applicationConfig;
    }

    public ApplicationConfigReader profiles(List<String> profiles) {
        this.profiles = new ArrayList<>(profiles);
        return this;
    }

    private void readConfig(String configFilePrefix) {
        if (fromProperties) {
            String file = configFilePrefix + ".properties";
            readConfigFromProperties(file);
        }
        if (fromYaml) {
            String fileYaml = configFilePrefix + ".yaml";
            String fileYml = configFilePrefix + ".yml";
            readConfigFromYaml(fileYaml);
            readConfigFromYaml(fileYml);
        }
    }

    private void readConfigFromProperties(String configFileName) {
        try {
            URL filePathUrl = ApplicationConfigReader.class.getClassLoader().getResource(configFileName);
            if (filePathUrl != null && Files.exists(Path.of(filePathUrl.getFile()))) {
                InputStream applicationPropertiesInputStream = ApplicationConfigReader.class.getClassLoader().getResourceAsStream(configFileName);
                Properties properties = new Properties();
                properties.load(applicationPropertiesInputStream);
                for (String key : properties.stringPropertyNames()) {
                    applicationConfig.setConfigProperty(key, properties.getProperty(key));
                }
            }
        } catch (Exception e) {
            throw new ApplicationConfigException(e.getMessage());
        }
    }

    private void readConfigFromYaml(String configFileName) {
        try {
            URL filePathUrl = ApplicationConfigReader.class.getClassLoader().getResource(configFileName);
            if (filePathUrl != null && Files.exists(Path.of(filePathUrl.getFile()))) {
                InputStream applicationYmlInputStream = ApplicationConfigReader.class.getClassLoader().getResourceAsStream(configFileName);
                Yaml yaml = new Yaml();
                Iterable<Object> config = yaml.loadAll(applicationYmlInputStream);
                for (Object o : config) {
                    Map<String, Object> map = (Map<String, Object>) o;
                    addConfig(map);
                }
            }
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
}
