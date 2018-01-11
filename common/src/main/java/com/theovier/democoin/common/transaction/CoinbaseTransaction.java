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
    private static final long serialVersionUID = -1720855678162093827L;

    public CoinbaseTransaction(final Address recipientAddress) {
        super(COINBASE_MSG);
        addOutput(new TxOutput(recipientAddress, 100));
        build();
    }

    public CoinbaseTransaction(final Address recipientAddress, final String msg) {
        super(msg);
        addOutput(new TxOutput(recipientAddress, 100));
        build();
    }

    public void addTransactionFeeReward(final long fees) {
        getFirstOutput().setValue(ConsensusParams.COINBASE_REWARD + fees);
    }

    public void setReward(long reward) {
        //TxOutput value can be final again if we do this instead of the constructor call.
        //addOutput(new TxOutput(recipientAddress, reward));
    }

    public long getReward() {
        return getFirstOutput().getValue();
    }

    //used for deserialization
    private Object readResolve() {
        getInputs().forEach(in -> in.setParentTransaction(this));
        getOutputs().forEach(out -> out.setParentTransaction(this));
        return this;
    }

}
