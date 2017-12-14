package com.theovier.democoin.common;

import com.theovier.democoin.common.transaction.*;
import org.apache.log4j.Logger;

public class BlockValidator {

    private static final Logger LOG = Logger.getLogger(BlockValidator.class);

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
            LOG.warn("invalid transactions");
            return false;
        }
        if (!hasOnlyBroadcastedTransactions(candidate)) {
            //return false;
        }
        if (!hasCoinbaseTx(candidate)) {
            LOG.warn("nicht genau 1 coinbase tx");
            return false;
        }
        //has to be called after regular transaction validation. because this sets the correct output reference.
        if (!hasValidCoinbaseTx(candidate)) {
            LOG.warn("coinbase reward passt nicht");
            return false;
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
                .filter(tx -> !tx.isCoinBase())
                .allMatch(TransactionValidator::isValid);
    }

    public static boolean hasOnlyBroadcastedTransactions(Block candidate) {
        return TransactionPool.containsAll(candidate.getTransactions());
    }

    public static boolean hasCoinbaseTx(Block candidate) {
        return candidate.getTransactions()
                .stream()
                .filter(Transaction::isCoinBase)
                .count() == 1;
    }

    public static boolean hasValidCoinbaseTx(Block candidate) {
        long txFee = candidate.getTransactions().stream().mapToLong(Transaction::getTransactionFee).sum();
        CoinbaseTransaction coinbaseTx = candidate.getCoinbaseTx();
        coinbaseTx.addTransactionFees(txFee);
        return TransactionValidator.isValid(coinbaseTx, txFee);
    }
}
