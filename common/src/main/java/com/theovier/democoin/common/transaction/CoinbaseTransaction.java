package com.theovier.democoin.common.transaction;


import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.ConsensusParams;

public class CoinbaseTransaction extends Transaction {

    /**
     * TX with:
     *  -no inputs.
     *  -only one output.
    */

    private static final long serialVersionUID = -1720855678162093827L;

    public CoinbaseTransaction(final Address recipientAddress, final long reward, final String msg) {
        super(msg);
        addOutput(new TxOutput(recipientAddress, reward));
        build();
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
