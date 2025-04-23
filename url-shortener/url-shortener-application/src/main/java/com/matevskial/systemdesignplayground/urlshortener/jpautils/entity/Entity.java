package com.matevskial.systemdesignplayground.urlshortener.jpautils.entity;

public interface Entity<ID> {

    ID getId();
    void setId(ID id);
}
