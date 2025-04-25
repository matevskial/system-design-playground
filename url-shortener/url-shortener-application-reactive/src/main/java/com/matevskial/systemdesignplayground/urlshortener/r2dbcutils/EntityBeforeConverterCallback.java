package com.matevskial.systemdesignplayground.urlshortener.r2dbcutils;

import com.matevskial.systemdesignplayground.urlshortener.jpautils.entity.Entity;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EntityBeforeConverterCallback implements BeforeConvertCallback<Entity> {

    private final TSID.Factory tsidFactory;

    @Override
    public Publisher<Entity> onBeforeConvert(Entity entity, SqlIdentifier table) {
        if (entity.getId() == null) {
            entity.setId(tsidFactory.generate().toLong());
        }
        return Mono.just(entity);
    }
}
