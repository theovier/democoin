package com.theovier.democoin.common;

public class TransactionValidator {

    //todo implement more checks.

    /**
     * Adapt to https://en.bitcoin.it/wiki/Protocol_rules#.22tx.22_messages
     */

    public static boolean validate(Transaction tx) {
        if (tx.isCoinbaseTx()) {
            return validateCoinbaseTx(tx);
        } else {
            return validateRegularTx(tx);
        }
    }

    private static boolean validateCoinbaseTx(Transaction tx) {
        if (tx.inputs.length > 0) {
            return false;
        } else if (tx.outputs.length > 1) {
            return false;
        } else if (tx.outputs[0].value != Config.COINBASE_REWARD) {
            return false;
        }
        return true;
    }

    private static boolean validateRegularTx(Transaction tx) {
        return true;
    }
}
