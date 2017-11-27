package com.theovier.democoin.common;

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
        } else if (tx.getOutputs().get(0).getValue() != Config.COINBASE_REWARD) {
            return false;
        }
        return true;
    }

    private static boolean validateRegularTx(Transaction tx) {

        return true;
    }

    private static boolean validateRegulatTxInput(TxInput txIn) {
        return true;
    }
}
