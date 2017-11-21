package com.theovier.democoin.common;

import java.io.Serializable;

public class TxInput implements Serializable {

    private static final long serialVersionUID = 478420474849537539L;
    private String previousTransactionHash; //transaction identifier (previous to be used transaction)
    private int previousTxOutputIndex;
    private String signature; //signature of (UTXO owner)


    public TxInput(String previousTransactionHash, int previousTxOutputIndex, String signature) {
        this.previousTransactionHash = previousTransactionHash;
        this.previousTxOutputIndex = previousTxOutputIndex;
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "TxInput{" +
                "previousTransactionHash='" + previousTransactionHash + '\'' +
                ", previousTxOutputIndex=" + previousTxOutputIndex +
                ", signature='" + signature + '\'' +
                '}';
    }
}
