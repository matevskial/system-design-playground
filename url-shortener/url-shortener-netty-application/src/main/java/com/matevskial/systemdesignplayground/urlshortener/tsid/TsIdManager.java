package com.matevskial.systemdesignplayground.urlshortener.tsid;

import io.hypersistence.tsid.TSID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TsIdManager {

    /**
     * DO NOT CHANGE THIS, value(in UTC) is Wed Apr 23 2025 13:00:00.000
     */
    private static final Instant TSID_CUSTOM_EPOCH = Instant.ofEpochMilli(1745413200000L);

    public static TSID.Factory tsIdFactory() {
        return TSID.Factory.builder()
                .withNodeBits(6)
                .withNode(42)
                .withCustomEpoch(TSID_CUSTOM_EPOCH)
                .build();
    }
}
