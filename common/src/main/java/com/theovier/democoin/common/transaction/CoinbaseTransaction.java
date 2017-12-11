package com.theovier.democoin.common.transaction;


import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.Config;

public class CoinbaseTransaction extends Transaction {

    /**
     * TX with:
     *  -no inputs.
     *  -only one output.
    */

    public static final String COINBASE_MSG = "It's a gift from the Gods!";

    public CoinbaseTransaction(Address recipientAddress) {
        super(COINBASE_MSG);
        addOutput(new TxOutput(recipientAddress, Config.COINBASE_REWARD));
        build();
    }
}
