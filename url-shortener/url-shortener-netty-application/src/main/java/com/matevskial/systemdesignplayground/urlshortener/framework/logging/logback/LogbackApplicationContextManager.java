package com.matevskial.systemdesignplayground.urlshortener.framework.logging.logback;

import ch.qos.logback.classic.LoggerContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContextManager;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationException;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class LogbackApplicationContextManager implements ApplicationContextManager {

    @Override
    public void manage(ApplicationContext context) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext loggerContext) {
            LogbackConfigurator logbackConfigurator = new LogbackConfigurator(loggerContext);
            DefaultLogbackConfiguration defaultLogbackConfiguration = new DefaultLogbackConfiguration();
            defaultLogbackConfiguration.apply(logbackConfigurator);
        } else {
            throw new ApplicationException("Logback application context is not configured: logging with logback is not configured since loggerFactory is %s. Make sure logback dependencies are in classpath".formatted(loggerFactory.getClass()));
        }
    }
}
