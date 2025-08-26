package com.matevskial.systemdesignplayground.urlshortener.spring;

import com.matevskial.systemdesignplayground.urlshortener.framework.transaction.TransactionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SpringTransactionContext implements TransactionContext {

    private final PlatformTransactionManager transactionManager;

    @Override
    public <R> R doIn(Callable<R> function) throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            R result = function.call();
            transactionManager.commit(status);
            return result;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
