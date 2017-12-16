package com.theovier.democoin.common;

import com.theovier.democoin.common.transaction.*;
import org.apache.log4j.Logger;

import java.util.stream.Collectors;

public class BlockValidator {

    private static final Logger LOG = Logger.getLogger(BlockValidator.class);

    public static boolean isValid(final Block candidate, final Blockchain blockchain) {
        Block prevBlock = blockchain.getLastBlock();
        if (!hasValidProofOfWork(candidate, blockchain)) {
            LOG.warn("pow missing");
            return false;
        }
        if (!hasValidIndex(candidate, prevBlock)) {
            LOG.warn("invalid block index");
            return false;
        }
        if (!hasValidHashChain(candidate, prevBlock)) {
            LOG.warn("not referring to the previous block");
            return false;
        }
        if (!hasValidMerkleRoot(candidate)) {
            LOG.warn("invalid merkle root");
            return false;
        }
        if (!hasValidBlockHash(candidate)) {
            LOG.warn("invalid blockhash");
            return false;
        }
        if (!hasValidTransactionCount(candidate)) {
            LOG.warn("there are too many transactions in this block");
           return false;
        }
        if (!hasOnlyValidTransactions(candidate)) {
            LOG.warn("invalid transaction(s)");
            return false;
        }
        if (!hasOnlyBroadcastedTransactions(candidate)) {
            return false;
        }
        if (!hasCoinbaseTx(candidate)) {
            LOG.warn("there is not exactly 1 coinbase transaction");
            return false;
        }
        //has to be called after regular transaction validation. because this sets the correct output reference.
        if (!hasValidCoinbaseTx(candidate)) {
            LOG.warn("coinbase output value is not calculated honestly.");
            return false;
        }
        return true;
    }

    public static boolean hasValidProofOfWork(Block candidate, Blockchain blockchain) {
        if (!Pow.checkProofOfWork(candidate.getHash(), candidate.getPowTarget())) {
            return false;
        }
        if (!candidate.getPowTarget().equals(Pow.getNextWorkRequired(blockchain))) {
            return false;
        }
        return true;
        //return candidate.getLeadingZerosCount() >= Config.DIFFICULTY;
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
        return TransactionPool.containsAll(
                candidate.getTransactions()
                        .stream()
                        .filter(tx -> !tx.isCoinBase())
                        .collect(Collectors.toList()));
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
