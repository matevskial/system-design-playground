package com.matevskial.systemdesignplayground.urlshortener.spring;

import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContextManager;
import com.matevskial.systemdesignplayground.urlshortener.framework.transaction.Transaction;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionWithSpringApplicationContextManager implements ApplicationContextManager {

    @Override
    public void manage(ApplicationContext context) {
        SpringTransactionContext transactionContext = new SpringTransactionContext(context.getBean(PlatformTransactionManager.class));
        Transaction.initialize(transactionContext);
    }
}
