package edu.mipt.accounts.transaction;

public interface Transaction {
    Transaction apply();
    void commit();
}
