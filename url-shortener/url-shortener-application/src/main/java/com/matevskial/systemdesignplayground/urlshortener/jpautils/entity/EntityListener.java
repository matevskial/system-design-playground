package com.matevskial.systemdesignplayground.urlshortener.jpautils.entity;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class EntityListener {

    private TSID.Factory tsidFactory;

    @Autowired
    public void setTsidFactory(TSID.Factory tsidFactory) {
        this.tsidFactory = tsidFactory;
    }

    @PrePersist
    public void setId(Object object) {
        if (object instanceof Entity entity) {
            if (entity.getId() == null) {
                entity.setId(tsidFactory.generate().toLong());
            }
        }
    }
}
