package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Validator;

import java.util.*;
import java.util.stream.Collectors;

public class TransactionPool {

    private final Set<Transaction> pendingTransactions = new HashSet<>();
    private final Validator<Transaction> validator;

    public TransactionPool(final Validator<Transaction> validator) {
        this.validator = validator;
    }

    /** check if all containing transactions are indeed valid.
        returns the amount of invalid transactions that were removed.
     */
    public synchronized int purge() {
        Set<Transaction> invalidPendingTransactions = pendingTransactions.stream()
                .filter(tx -> !validator.isValid(tx))
                .collect(Collectors.toSet());
        pendingTransactions.removeAll(invalidPendingTransactions);
        return invalidPendingTransactions.size();
    }

    public synchronized boolean add(final Transaction transaction) {
        if (validator.isValid(transaction) && !pendingTransactions.contains(transaction)) {
            pendingTransactions.add(transaction);
            return true;
        }
        return false;
    }

    public synchronized void remove(final Transaction transaction) {
        pendingTransactions.remove(transaction);
    }

    public synchronized boolean containsAll(final Collection<Transaction> transactions) {
        return pendingTransactions.containsAll(transactions);
    }

    public synchronized Set<Transaction> getPendingTransactions() {
        return pendingTransactions;
    }
}