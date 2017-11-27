package com.theovier.democoin.common;

import java.io.Serializable;

public class TxOutput implements Serializable {

    /** An output of a transaction. It contains the public key that the next input
     * must be able to sign with to claim it.
     */

    private static final long serialVersionUID = -4403978077387051602L;
    long value;
    String recipientPublicKey; //org: scriptPubKey; here: address

    public TxOutput(final long value, String recipientPublicKey) {
        this.value = value;
        this.recipientPublicKey = recipientPublicKey;
    }

    @Override
    public String toString() {
        return "out{" +
                "value=" + value +
                ", recipientPublicKey='" + recipientPublicKey + '\'' +
                '}';
    }
}
