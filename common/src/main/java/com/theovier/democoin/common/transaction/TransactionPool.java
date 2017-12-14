package com.theovier.democoin.common.transaction;

import java.util.*;

public class TransactionPool {

    private static Set<Transaction> pendingTransactions = new HashSet<>();

    public static synchronized boolean add(Transaction transaction) {
        if (TransactionValidator.isValid(transaction)) {
            pendingTransactions.add(transaction);
            return true;
        }
        return false;
    }

    public static synchronized void remove(Transaction transaction) {
        pendingTransactions.remove(transaction);
    }

    public static boolean containsAll(Collection<Transaction> transactions) {
        return pendingTransactions.containsAll(transactions);
    }
}
