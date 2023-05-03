package edu.mipt.accounts.dblock;

import edu.mipt.accounts.transaction.Transaction;

public class DynamicLock implements Lock {
    private static final Object freeLock = new Object();

    @Override
    public void exec(Transaction command, Object firstObj, Object secondObj) {
        int firstHash = firstObj.hashCode();
        int secondHash = secondObj.hashCode();

        if (firstHash < secondHash) {
            lock(firstObj, secondObj, command);
            return;
        }

        if (secondHash < firstHash) {
            lock(secondObj, firstObj, command);
            return;
        }

        synchronized (freeLock) {
            lock(firstObj, secondObj, command);
        }
    }

    private void lock(Object externalLock, Object internalLock, Transaction command) {
        synchronized (externalLock) {
            synchronized (internalLock) {
                command.apply().commit();
            }
        }
    }
}
