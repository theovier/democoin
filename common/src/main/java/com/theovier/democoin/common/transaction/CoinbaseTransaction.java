package com.theovier.democoin.common.transaction;


import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.ConsensusParams;

public class CoinbaseTransaction extends Transaction {

    /**
     * TX with:
     *  -no inputs.
     *  -only one output.
    */

    public static final String COINBASE_MSG = "It's a gift from the Gods!";

    public CoinbaseTransaction(final Address recipientAddress) {
        super(COINBASE_MSG);
        addOutput(new TxOutput(recipientAddress, ConsensusParams.COINBASE_REWARD));
        build();
    }

    public CoinbaseTransaction(final Address recipientAddress, final String msg) {
        super(msg);
        addOutput(new TxOutput(recipientAddress, ConsensusParams.COINBASE_REWARD));
        build();
    }

    public void addTransactionFees(final long fees) {
        getFirstOutput().setValue(ConsensusParams.COINBASE_REWARD + fees);
    }
}
