package com.matevskial.systemdesignplayground.urlshortener.framework.application.transaction;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction {

    private static TransactionContext transactionContext;

    /**
     * Not thread safe, do not call this method in 2 or more threads that might execute
     * in an interleaving manner. This is designed to be called in the initialization part of the
     * application
     */
    public static void initialize(TransactionContext transactionContext) {
        Transaction.transactionContext = transactionContext;
    }

    public static <R> R withTransaction(Callable<R> callable) {
        try {
            return transactionContext.doIn(callable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
