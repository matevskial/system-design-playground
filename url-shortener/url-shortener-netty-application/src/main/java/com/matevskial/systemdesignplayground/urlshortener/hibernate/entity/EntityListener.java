package com.matevskial.systemdesignplayground.urlshortener.hibernate.entity;

import com.matevskial.systemdesignplayground.urlshortener.application.ApplicationContext;
import io.hypersistence.tsid.TSID;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityListener {

    @PrePersist
    public void setId(Object object) {
        if (object instanceof Entity entity) {
            if (entity.getId() == null) {
                entity.setId(ApplicationContext.getBean(TSID.Factory.class).generate().toLong());
            }
        }
    }
}
