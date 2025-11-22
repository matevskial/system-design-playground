package com.matevskial.systemdesignplayground.urlshortener.framework.logging.logback;

import java.util.concurrent.locks.ReentrantLock;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

class LogbackConfigurator {

    private final LoggerContext context;

    LogbackConfigurator(LoggerContext context) {
        this.context = context;
        Logger logger = this.context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        // Remove the default appender, so we don't get duplicate loggings
        logger.detachAndStopAllAppenders();
    }

    LoggerContext getContext() {
        return this.context;
    }

    ReentrantLock getConfigurationLock() {
        return this.context.getConfigurationLock();
    }

    void appender(String name, Appender<?> appender) {
        appender.setName(name);
        start(appender);
    }

    void logger(String name, Level level) {
        logger(name, level, true);
    }

    void logger(String name, Level level, boolean additive) {
        logger(name, level, additive, null);
    }

    void logger(String name, Level level, boolean additive, Appender<ILoggingEvent> appender) {
        Logger logger = this.context.getLogger(name);
        if (level != null) {
            logger.setLevel(level);
        }
        logger.setAdditive(additive);
        if (appender != null) {
            logger.addAppender(appender);
        }
    }

    @SafeVarargs
    final void root(Level level, Appender<ILoggingEvent>... appenders) {
        Logger logger = this.context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (level != null) {
            logger.setLevel(level);
        }
        for (Appender<ILoggingEvent> appender : appenders) {
            logger.addAppender(appender);
        }
    }

    void start(LifeCycle lifeCycle) {
        if (lifeCycle instanceof ContextAware contextAware) {
            contextAware.setContext(this.context);
        }
        lifeCycle.start();
    }
}
