package com.matevskial.systemdesignplayground.urlshortener.jpautils.entity;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityListener {

    private final TSID.Factory tsidFactory;

    @PrePersist
    public void setId(Object object) {
        if (object instanceof Entity entity) {
            if (entity.getId() == null) {
                entity.setId(tsidFactory.generate().toLong());
            }
        }
    }
}
