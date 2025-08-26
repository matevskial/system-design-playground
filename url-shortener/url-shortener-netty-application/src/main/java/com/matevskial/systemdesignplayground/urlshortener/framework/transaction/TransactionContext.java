package com.matevskial.systemdesignplayground.urlshortener.framework.transaction;

import java.util.concurrent.Callable;

public interface TransactionContext {

    /**
     * Do some work in a transaction context
     * @param function
     * @return
     * @param <R>
     */
    <R> R doIn(Callable<R> function) throws Exception;
}
