package edu.mipt.accounts.dblock;

import edu.mipt.accounts.Accounts;
import edu.mipt.accounts.transaction.TransferTransaction;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DbSynchronizedAccounts implements Accounts {
    private final AccountRepository accountRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Retryable(maxAttempts = 28, retryFor = {   ObjectOptimisticLockingFailureException.class,
                                                CannotAcquireLockException.class,
                                                HibernateException.class})
    public void transfer(long fromAccountId, long toAccountId, long amount) {
        if(fromAccountId == toAccountId) return;

        var fromAccount = accountRepository.findById(fromAccountId);
        var toAccount = accountRepository.findById(toAccountId);

        System.out.println("Version: " + fromAccount.getVersion());
        System.out.println("Balance: " + fromAccount.getBalance());

        var transferTransaction = new TransferTransaction(accountRepository, fromAccount, toAccount, amount);
        var lock = new DynamicLock();

        lock.exec(transferTransaction, fromAccount, toAccount);
    }
}