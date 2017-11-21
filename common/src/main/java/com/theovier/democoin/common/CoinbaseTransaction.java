package com.theovier.democoin.common;


public class CoinbaseTransaction extends Transaction {

    /**
     * TX with:
     *  -no inputs.
     *  -only one output.
    */

    public static final String COINBASE_MSG = "It's a gift from the Gods!";

    public CoinbaseTransaction(String recipientAddress) {
        super(COINBASE_MSG);
        inputs = new TxInput[0];
        outputs = new TxOutput[]{
                new TxOutput(Config.COINBASE_REWARD, recipientAddress)
        };
        this.hash = computeHash();
    }
}
