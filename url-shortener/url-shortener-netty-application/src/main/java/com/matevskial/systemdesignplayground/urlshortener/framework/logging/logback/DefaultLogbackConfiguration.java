package com.matevskial.systemdesignplayground.urlshortener.framework.logging.logback;

import java.io.Console;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.OptionHelper;

class DefaultLogbackConfiguration {

    private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

    private static final String DEFAULT_CONSOLE_LOG_PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    static final String CONSOLE_LOG_PATTERN = "${CONSOLE_LOG_PATTERN:-" + DEFAULT_CONSOLE_LOG_PATTERN + "}";

    public DefaultLogbackConfiguration() {

    }

    void apply(LogbackConfigurator config) {
        config.getConfigurationLock().lock();
        try {
            defaults(config);
            Appender<ILoggingEvent> consoleAppender = consoleAppender(config);
            config.root(Level.INFO, consoleAppender);
        }
        finally {
            config.getConfigurationLock().unlock();
        }
    }

    private void defaults(LogbackConfigurator config) {
        putProperty(config, "CONSOLE_LOG_PATTERN", CONSOLE_LOG_PATTERN);
        putProperty(config, "CONSOLE_LOG_CHARSET", "${CONSOLE_LOG_CHARSET:-" + getConsoleCharset() + "}");
        putProperty(config, "CONSOLE_LOG_THRESHOLD", "${CONSOLE_LOG_THRESHOLD:-TRACE}");
        putProperty(config, "CONSOLE_LOG_STRUCTURED_FORMAT", "${CONSOLE_LOG_STRUCTURED_FORMAT:-}");
        config.logger("io.netty", Level.ERROR);
        config.logger("io.micronaut", Level.OFF);
    }

    private String getConsoleCharset() {
        Console console = getConsole();
        return (console != null) ? console.charset().name() : DEFAULT_CHARSET;
    }

    Console getConsole() {
        return System.console();
    }

    void putProperty(LogbackConfigurator config, String name, String val) {
        config.getContext().putProperty(name, resolve(config, val));
    }

    private Appender<ILoggingEvent> consoleAppender(LogbackConfigurator config) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        createAppender(config, appender, "CONSOLE");
        config.appender("CONSOLE", appender);
        return appender;
    }

    private void createAppender(LogbackConfigurator config, OutputStreamAppender<ILoggingEvent> appender, String type) {
        appender.addFilter(createThresholdFilter(config, type));
        Encoder<ILoggingEvent> encoder = createEncoder(config, type);
        appender.setEncoder(encoder);
        config.start(encoder);
    }

    private ThresholdFilter createThresholdFilter(LogbackConfigurator config, String type) {
        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(resolve(config, "${" + type + "_LOG_THRESHOLD}"));
        filter.start();
        return filter;
    }

    private Encoder<ILoggingEvent> createEncoder(LogbackConfigurator config, String type) {
        Charset charset = resolveCharset(config, "${" + type + "_LOG_CHARSET}");
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setCharset(charset);
        encoder.setPattern(resolve(config, "${" + type + "_LOG_PATTERN}"));
        return encoder;
    }

    private Charset resolveCharset(LogbackConfigurator config, String val) {
        return Charset.forName(resolve(config, val));
    }

    private String resolve(LogbackConfigurator config, String val) {
        try {
            return OptionHelper.substVars(val, config.getContext());
        } catch (ScanException ex) {
            throw new RuntimeException(ex);
        }
    }
}
