package com.theovier.democoin.common;

import com.theovier.democoin.common.transaction.*;
import org.apache.log4j.Logger;

import java.util.stream.Collectors;

public class BlockValidator implements Validator<Block> {

    private static final Logger LOG = Logger.getLogger(BlockValidator.class);

    private final Blockchain blockchain;
    private final Validator<Transaction> txValidator;

    public BlockValidator(final Blockchain blockchain, final Validator<Transaction> txValidator) {
        this.blockchain = blockchain;
        this.txValidator = txValidator;
    }

    @Override
    public boolean isValid(final Block candidate) {
        Block prevBlock = blockchain.getLastBlock();
        if (!hasValidProofOfWork(candidate, blockchain)) {
            LOG.debug("pow missing " + candidate);
            return false;
        }
        if (!hasValidIndex(candidate, prevBlock)) {
            LOG.debug("invalid block index " + candidate);
            return false;
        }
        if (!hasValidHashChain(candidate, prevBlock)) {
            LOG.debug("not referring to the previous block " + candidate);
            return false;
        }
        if (!hasValidMerkleRoot(candidate)) {
            LOG.debug("invalid merkle root " + candidate);
            return false;
        }
        if (!hasValidBlockHash(candidate)) {
            LOG.debug("invalid blockhash " + candidate);
            return false;
        }
        if (!hasValidTransactionCount(candidate)) {
            LOG.debug("there are too many transactions in this block " + candidate);
           return false;
        }
        if (!hasOnlyValidTransactions(candidate)) {
            LOG.debug("invalid transaction(s) " + candidate);
            return false;
        }
        if (!hasCoinbaseTx(candidate)) {
            LOG.debug("there is not exactly 1 coinbase transaction " + candidate);
            return false;
        }
        //has to be called after regular transaction validations, because these set the correct output reference.
        if (!hasValidCoinbaseTx(candidate)) {
            LOG.debug("coinbase output value is not calculated honestly. " + candidate);
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
        return candidate.getTransactions().size() <= ConsensusParams.MAX_TRANSACTIONS_PER_BLOCK;
    }

    public boolean hasOnlyValidTransactions(Block candidate) {
        return candidate.getTransactions()
                .stream()
                .filter(tx -> !(tx instanceof CoinbaseTransaction))
                .allMatch(txValidator::isValid);
    }
    
    public static boolean hasCoinbaseTx(Block candidate) {
        return candidate.getTransactions()
                .stream()
                .filter(tx -> tx instanceof CoinbaseTransaction)
                .count() == 1;
    }

    /**
     * attention: side-effect! sets the coinbase reward.
     * We can't do this earlier, because we have to validate each TX first in order to avoid
     * possible nullpointers (invalid referenced UTXOs when accessing the Fees).
     */
    public boolean hasValidCoinbaseTx(Block candidate) {
        long txFee = candidate.getTransactions().stream().mapToLong(Transaction::getTransactionFee).sum();
        return TransactionValidator.isValid(candidate.getCoinbaseTx(), txFee);
    }
}
