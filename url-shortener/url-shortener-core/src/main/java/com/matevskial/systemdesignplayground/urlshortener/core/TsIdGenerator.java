package com.matevskial.systemdesignplayground.urlshortener.core;

import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TsIdGenerator implements IdGenerator {

    private final TSID.Factory tsidFactory;

    @Override
    public long generateId() {
        return tsidFactory.generate().toLong();
    }
}
