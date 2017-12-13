package com.theovier.democoin.common;

import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.common.transaction.TransactionPool;
import com.theovier.democoin.common.transaction.TransactionValidator;

public class BlockValidator {

    public static boolean isValid(Block candidate, Block prevBlock) {
        if (!hasValidIndex(candidate, prevBlock)) {
            return false;
        }
        if (!hasValidHashChain(candidate, prevBlock)) {
            return false;
        }
        if (!hasValidMerkleRoot(candidate)) {
            return false;
        }
        if (!hasValidBlockHash(candidate)) {
            return false;
        }
        if (!hasValidProofOfWork(candidate)) {
            //return false;
        }
        if (!hasValidTransactionCount(candidate)) {
           return false;
        }
        if (!hasOnlyValidTransactions(candidate)) {
            return false;
        }
        if (!hasOnlyBroadcastedTransactions(candidate)) {
            //return false;
        }
        if (!hasCoinbaseTX(candidate)) {
           // return false;
        }
        return true;
    }

    public static boolean hasValidIndex(Block candidate, Block prevBlock) {
        return (candidate.getIndex() == prevBlock.getIndex() + 1);
    }

    public static boolean hasValidHashChain(Block candidate, Block prevBlock) {
        return prevBlock.getHash().equals(candidate.getPreviousBlockHash());
    }

    public static boolean hasValidMerkleRoot(Block candidate) {
        return candidate.computeMerkleRoot().equals(candidate.getMerkleRoot());
    }

    public static boolean hasValidBlockHash(Block candidate) {
        return candidate.computeHash().equals(candidate.getHash());
    }

    public static boolean hasValidProofOfWork(Block candidate) {
        return false;
    }

    public static boolean hasValidTransactionCount(Block candidate) {
        return candidate.getTransactions().size() <= Config.MAX_TRANSACTIONS_PER_BLOCK;
    }

    public static boolean hasOnlyValidTransactions(Block candidate) {
        return candidate.getTransactions()
                .stream()
                .allMatch(TransactionValidator::validate);
    }

    public static boolean hasOnlyBroadcastedTransactions(Block candidate) {
        return TransactionPool.containsAll(candidate.getTransactions());
    }

    public static boolean hasCoinbaseTX(Block candidate) {
        return candidate.getTransactions()
                .stream()
                .filter(Transaction::isCoinBase)
                .count() == 1;
    }
}
