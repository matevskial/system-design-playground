package com.matevskial.systemdesignplayground.urlshortener.persistence.springjdbc;

import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertClause;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

@RequiredArgsConstructor
public class UrlPersistenceSpringJdbcQuerydslImpl implements UrlPersistence {

    private final JdbcTemplate jdbcTemplate;
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
        try {
            SQLTemplates dialect = new PostgreSQLTemplates();
            QUrlJdbcEntity urlEntity = QUrlJdbcEntity.urlJdbcEntity;
            SQLInsertClause insertClause = new SQLInsertClause(jdbcTemplate.getDataSource().getConnection(), dialect, urlEntity);
            insertClause
                    .set(urlEntity.id, tsidFactory.generate().toLong())
                    .set(urlEntity.originalUrl, url)
                    .set(urlEntity.shortened, shortened)
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<String> findOriginalUrl(String shortened) {
        try {
            SQLTemplates dialect = new PostgreSQLTemplates();
            SQLQuery<?> query = new SQLQuery<Void>(jdbcTemplate.getDataSource().getConnection(), dialect);
            QUrlJdbcEntity urlEntity = QUrlJdbcEntity.urlJdbcEntity;
            return Optional.ofNullable(
                    query.select(urlEntity.originalUrl)
                            .from(urlEntity)
                            .where(urlEntity.shortened.eq(shortened))
                            .fetchFirst()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
