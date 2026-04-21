package com.matevskial.systemdesignplayground.urlshortener.transaction.spring;

import com.matevskial.systemdesignplayground.urlshortener.framework.transaction.TransactionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SpringTransactionContext implements TransactionContext {

    private final TransactionTemplate transactionTemplate;

    public SpringTransactionContext(PlatformTransactionManager platformTransactionManager) {
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    @Override
    public <R> R doIn(Callable<R> function) {
        return (R) transactionTemplate.execute((transactionStatus) -> {
            try {
                return function.call();
            } catch (Exception e) {
                return new RuntimeException(e);
            }
        });

//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        TransactionStatus status = transactionManager.getTransaction(def);
//        try {
//            R result = function.call();
//            try {
//                transactionManager.commit(status);
//            } catch (Exception e) {
//                throw new CommitException(e);
//            }
//            return result;
//        } catch (CommitException e) {
//            throw e;
//        } catch (Exception e) {
//            transactionManager.rollback(status);
//            throw e;
//        }
    }

    private static class CommitException extends RuntimeException {
        public CommitException(Throwable e) {
            super(e);
        }
    }
}
