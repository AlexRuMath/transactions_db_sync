package edu.mipt.accounts.dblock;

import edu.mipt.accounts.transaction.Transaction;

public interface Lock {
    void exec(Transaction command, Object firstObj, Object secondObj);
}
