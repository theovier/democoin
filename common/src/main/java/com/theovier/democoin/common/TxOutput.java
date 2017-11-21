package com.theovier.democoin.common;

public class TxOutput {


    /** An output of a transaction. It contains the public key that the next input
     * must be able to sign with to claim it.
     */

    long value;
    String recipientPublicKey; //org: scriptPubKey; here: address

    public TxOutput(final long value, String recipientPublicKey) {
        this.value = value;
        this.recipientPublicKey = recipientPublicKey;
    }

    @Override
    public String toString() {
        return "TxOutput{" +
                "value=" + value +
                ", recipientPublicKey='" + recipientPublicKey + '\'' +
                '}';
    }
}
