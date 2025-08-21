package com.matevskial.systemdesignplayground.urlshortener.persistence.common;

public interface Entity<ID> {

    ID getId();
    void setId(ID id);
}
