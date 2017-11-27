package com.theovier.democoin.common;


public class CoinbaseTransaction extends Transaction {

    /**
     * TX with:
     *  -no inputs.
     *  -only one output.
    */

    public static final String COINBASE_MSG = "It's a gift from the Gods!";

    public CoinbaseTransaction(Address recipientAddress) {
        super(COINBASE_MSG);
        inputs = new TxInput[0];
        outputs = new TxOutput[]{
                new TxOutput(recipientAddress, Config.COINBASE_REWARD)
        };
        this.txId = computeHash();
    }
}
