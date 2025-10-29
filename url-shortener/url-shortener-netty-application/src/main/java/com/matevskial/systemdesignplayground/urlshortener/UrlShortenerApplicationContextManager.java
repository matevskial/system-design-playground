package com.matevskial.systemdesignplayground.urlshortener;

import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContextManager;
import com.matevskial.systemdesignplayground.urlshortener.core.TsIdGenerator;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortener;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortenerProperties;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShorteners;
import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import com.matevskial.systemdesignplayground.urlshortener.persistence.springjdbc.UrlPersistenceSpringJdbcQuerydslImpl;
import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import io.hypersistence.tsid.TSID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

public class UrlShortenerApplicationContextManager implements ApplicationContextManager {

    @Override
    public void manage(ApplicationContext context) {
        UrlShortener urlShortener = UrlShorteners.base62Variant(new TsIdGenerator(context.getBean(TSID.Factory.class)));
        context.registerBean(urlShortener, UrlShortener.class);

        UrlShortenerProperties urlShortenerProperties = new UrlShortenerProperties(context.getConfigProperty("url-shortener.base-url", String.class));
        context.registerBean(urlShortenerProperties, UrlShortenerProperties.class);

        context.registerBean(new UrlPersistenceSpringJdbcQuerydslImpl(
                        context.getBean(JdbcTemplate.class),
                        context.getBean(JdbcClient.class),
                        context.getBean(TSID.Factory.class)),
                UrlPersistence.class);

        UrlShortenerService urlShortenerService = new UrlShortenerService(
                context.getBean(UrlPersistence.class),
                context.getBean(UrlShortener.class),
                context.getBean(UrlShortenerProperties.class)
        );
        context.registerBean(urlShortenerService, UrlShortenerService.class);
    }
}
