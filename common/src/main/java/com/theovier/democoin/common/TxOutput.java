package com.theovier.democoin.common;

import java.io.Serializable;

public class TxOutput implements Serializable {

    /** An output of a transaction. It contains the public key that the next input
     * must be able to sign with to claim it.
     */

    private static final long serialVersionUID = -4403978077387051602L;
    private long value;
    private Address recipientAddress; //was recipientPublicKey; //org: scriptPubKey

    public TxOutput(final Address recipientAddress, final long value) {
        this.recipientAddress = recipientAddress;
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public Address getRecipientAddress() {
        return recipientAddress;
    }

    @Override
    public String toString() {
        return "out{" +
                "value=" + value +
                ", recipientPublicKey='" + recipientAddress + '\'' +
                '}';
    }
}
