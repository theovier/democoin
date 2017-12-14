package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Config;

public class TransactionValidator {

    //todo implement more checks.

    /**
     * Adapt to https://en.bitcoin.it/wiki/Protocol_rules#.22tx.22_messages
     */

    public static boolean validate(Transaction tx) {
        if (tx.isCoinBase()) {
            return validateCoinbaseTx(tx);
        } else {
            return validateRegularTx(tx);
        }
    }

    private static boolean validateCoinbaseTx(Transaction tx) {
        if (tx.getInputs().size() > 0) {
            return false;
        } else if (tx.getOutputs().size() > 1) {
            return false;
        }
        return true;
    }

    private static boolean validateRegularTx(Transaction tx) {
        for (TxInput in : tx.getInputs()) {
            if (!validateRegularTxInput(in)) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateRegularTxInput(TxInput in) {
        try {
            TxOutputPointer pointer = in.getPrevOutputInfo();
            TxOutput out = UTXOPool.getUTXO(pointer);
            return in.verify(out);
        } catch (MissingUTXOException e) {
            return false;
        }
    }
}
