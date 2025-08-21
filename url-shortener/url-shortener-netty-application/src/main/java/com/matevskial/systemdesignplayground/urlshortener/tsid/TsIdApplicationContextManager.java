package com.matevskial.systemdesignplayground.urlshortener.tsid;

import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContextManager;
import io.hypersistence.tsid.TSID;
import java.time.Instant;

public class TsIdApplicationContextManager implements ApplicationContextManager {

    /**
     * DO NOT CHANGE THIS, value(in UTC) is Wed Apr 23 2025 13:00:00.000
     */
    private static final Instant TSID_CUSTOM_EPOCH = Instant.ofEpochMilli(1745413200000L);

    private static TSID.Factory tsIdFactory() {
        return TSID.Factory.builder()
                .withNodeBits(6)
                .withNode(42)
                .withCustomEpoch(TSID_CUSTOM_EPOCH)
                .build();
    }

    @Override
    public void manage(ApplicationContext context) {
        context.registerBean(tsIdFactory(), TSID.Factory.class);
    }
}
