package com.matevskial.systemdesignplayground.urlshortener.persistence.springjdbc;

import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLTemplates;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Optional;

@RequiredArgsConstructor
public class UrlPersistenceSpringJdbcQuerydslImpl implements UrlPersistence {

    private final JdbcTemplate jdbcTemplate;
    private final JdbcClient jdbcClient;
    private final TSID.Factory tsidFactory;

    @Override
    public Optional<String> findShortened(String url) {
        try {
            SQLTemplates dialect = new PostgreSQLTemplates();
            SQLQuery<?> query = new SQLQuery<Void>(jdbcTemplate.getDataSource().getConnection(), dialect);
            QUrlJdbcEntity urlEntity = QUrlJdbcEntity.urlJdbcEntity;
            return Optional.ofNullable(query
                    .select(urlEntity.shortened)
                    .from(urlEntity)
                    .where(urlEntity.originalUrl.eq(url))
                    .fetchFirst());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveShortened(String url, String shortened) {
        jdbcClient
                .sql("INSERT INTO urls(id, original_url, shortened) VALUES (:id, :original_url, :shortened)")
                .param("id", tsidFactory.generate().toLong())
                .param("original_url", url)
                .param("shortened", shortened)
                .update();
    }

    @Override
    public Optional<String> findOriginalUrl(String shortened) {
        return Optional.empty();
    }
}
