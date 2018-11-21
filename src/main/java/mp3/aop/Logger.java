package mp3.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Aspect
public class Logger {

    @Pointcut("execution(* mp3.dao.implementations.SQLiteDAO.*.insert*(..))")
    public void insertMethods() {

    }

    @Before("insertMethods()")
    public void printActiveTransactions() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
    }

}
