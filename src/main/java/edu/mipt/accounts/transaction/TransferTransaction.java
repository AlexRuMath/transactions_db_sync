package edu.mipt.accounts.transaction;

import edu.mipt.accounts.dblock.Account;
import edu.mipt.accounts.dblock.AccountRepository;

import java.util.List;

public class TransferTransaction implements Transaction {
    private final Account from;
    private final Account to;
    private final AccountRepository repository;
    private final long value;

    public TransferTransaction(AccountRepository repository, Account from, Account to, long value) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.repository = repository;
    }

    @Override
    public Transaction apply() {
        from.withdraw(value);
        to.deposit(value);
        return this;
    }

    @Override
    public void commit() {
        repository.saveAllAndFlush(List.of(from, to));
    }
}
