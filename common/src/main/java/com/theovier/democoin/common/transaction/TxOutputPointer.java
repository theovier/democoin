package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.crypto.Sha256Hash;

import java.io.Serializable;

public class TxOutputPointer implements Serializable {

    private static final long serialVersionUID = 5743551940949924366L;
    private Sha256Hash transactionHash;
    private int outputIndex;

    public TxOutputPointer(final Sha256Hash transactionHash, final int outputIndex) {
        this.transactionHash = transactionHash;
        this.outputIndex = outputIndex;
    }

    public TxOutputPointer(final TxOutput output) {
        this.transactionHash = output.getParentTransaction().getTxId();
        this.outputIndex = output.getIndex();
    }

    public Sha256Hash getTransactionHash() {
        return transactionHash;
    }

    public int getOutputIndex() {
        return outputIndex;
    }

    @Override
    public String toString() {
        return "TxOutputPointer{" +
                "transactionHash=" + transactionHash +
                ", outputIndex=" + outputIndex +
                '}';
    }
}
