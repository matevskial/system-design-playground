package com.matevskial.systemdesignplayground.urlshortener.hibernate.entity;

public interface Entity<ID> {

    ID getId();
    void setId(ID id);
}
