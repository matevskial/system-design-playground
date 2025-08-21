package com.matevskial.systemdesignplayground.urlshortener.framework.application.transaction;

import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContextManager;
import com.matevskial.systemdesignplayground.urlshortener.spring.SpringTransactionContext;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionApplicationContextManager implements ApplicationContextManager {

    @Override
    public void manage(ApplicationContext context) {
        SpringTransactionContext transactionContext = new SpringTransactionContext(context.getBean(PlatformTransactionManager.class));
        Transaction.initialize(transactionContext);
    }
}
