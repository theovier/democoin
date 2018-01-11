package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.ConsensusParams;
import com.theovier.democoin.common.Validator;
import org.apache.log4j.Logger;

import java.util.List;

public class TransactionValidator implements Validator<Transaction> {

    /**
     * Adapt to https://en.bitcoin.it/wiki/Protocol_rules#.22tx.22_messages
     */

    private static final Logger LOG = Logger.getLogger(TransactionValidator.class);
    private final UTXOPool UTXOPool;

    public TransactionValidator(final UTXOPool UTXOPool) {
        this.UTXOPool = UTXOPool;
    }

    public static boolean isValid(CoinbaseTransaction tx, long totalBlockFee) {
        if (tx.getInputs().size() != 0) {
            return false;
        }
        if (tx.getOutputs().size() != 1) {
            return false;
        }
        if (tx.getReward() < 0 || tx.getReward() > ConsensusParams.MAX_COINS) {
            return false;
        }
        if (tx.getReward() != (ConsensusParams.COINBASE_REWARD + totalBlockFee)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isValid(Transaction tx) {
        if (!hasOnlyValidTxInputs(tx)) {
            LOG.debug("invalid input(s) @" + tx);
            return false;
        }
        if (!hasOnlyValidTxOutputs(tx)) {
            LOG.debug("invalid output(s) @" + tx);
            return false;
        }

        long sumInputs = tx.calculateSumInputs();
        long sumOutputs = tx.calculateSumOutputs();
        if (sumInputs < sumOutputs) {
            LOG.debug("invalid: inputs < outputs" + tx);
            return false;
        }
        tx.calculateTransactionFee();
        //todo "Reject if we already have matching tx in the pool, or in a block in the main branch" //use merkleHash for this.
        return true;
    }

    private boolean hasOnlyValidTxInputs(Transaction tx) {
        List<TxInput> inputs = tx.getInputs();
        if (inputs.size() <= 0) {
            return false;
        }
        if (!inputs.stream().allMatch(this::isValidTxInput)) {
            return false;
        }
        //if we are here, we can be sure that every input has a referenced output.
        if (!areAllInputsInLegalMoneyRange(inputs)) {
            return false;
        }
        if (!isValueSumInLegalMoneyRange(inputs)) {
            return false;
        }
        //todo check if referenced output is coinbase -> check maturity.
        return true;
    }

    /**
     * attention: side-effect! sets the referenced output.
     */
    private boolean isValidTxInput(TxInput in) {
        try {
            TxOutputPointer pointer = in.getPrevOutputInfo();
            TxOutput out = UTXOPool.getUTXO(pointer);
            in.setReferencedOutput(out);
            return in.verify(out);
        } catch (MissingUTXOException e) {
            return false;
        }
    }

    private static boolean areAllInputsInLegalMoneyRange(List<TxInput> inputs) {
        return inputs.stream().allMatch(TransactionValidator::isValueInLegalMoneyRange);
    }

    private static boolean isValueInLegalMoneyRange(TxInput in) {
        return isInLegalMoneyRange(in.getClaimedValue());
    }

    private static boolean isInLegalMoneyRange(long value) {
        return 0 < value && value <= ConsensusParams.MAX_COINS;
    }

    private static boolean isValueSumInLegalMoneyRange(List<TxInput> inputs) {
        long sumInValues = inputs.stream().mapToLong(TxInput::getClaimedValue).sum();
        if (isInLegalMoneyRange(sumInValues)) {
            return true;
        }
        return false;
    }

    private static boolean hasOnlyValidTxOutputs(Transaction tx) {
        if (tx.getOutputs().size() <= 0) {
            return false;
        }
        if (!tx.getOutputs().stream().allMatch(TransactionValidator::isValidTxOutput)) {
            return false;
        }
        if (tx.getOutputs().stream().mapToLong(TxOutput::getValue).sum() > ConsensusParams.MAX_COINS) {
            return false;
        }
        return true;
    }

    private static boolean isValidTxOutput(TxOutput out) {
        if (out.getValue() > ConsensusParams.MAX_COINS) {
            return false;
        }
        return true;
    }
}
