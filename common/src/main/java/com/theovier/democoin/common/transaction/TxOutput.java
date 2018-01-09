package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Address;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class TxOutput implements Serializable {

    /** An output of a transaction. It contains the public key that the next input
     * must be able to sign with to claim it.
     */

    private static final long serialVersionUID = -4403978077387051602L;

    private Address recipientAddress; //was recipientPublicKey; //org: scriptPubKey
    private long value;
    private transient Transaction parentTransaction;

    public TxOutput(final Address recipientAddress, final long value) {
        this.recipientAddress = recipientAddress;
        this.value = value;
    }

    public TxOutput(Transaction parentTransaction, final Address recipientAddress, final long value) {
        this.parentTransaction = parentTransaction;
        this.recipientAddress = recipientAddress;
        this.value = value;
    }

    public void setParentTransaction(Transaction parentTransaction) {
        this.parentTransaction = parentTransaction;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public Address getRecipientAddress() {
        return recipientAddress;
    }

    public Transaction getParentTransaction() {
        return parentTransaction;
    }

    /**
     * Gets the index of this output in the parentTransaction transaction, or throws if this output is free standing. Iterates
     * over the parents list to discover this.
     */
    public int getIndex() {
        List<TxOutput> outputs = getParentTransaction().getOutputs();
        for (int i = 0; i < outputs.size(); i++) {
            if (outputs.get(i) == this)
                return i;
        }
        throw new IllegalStateException("Output linked to wrong parentTransaction transaction?");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxOutput txOutput = (TxOutput) o;
        return value == txOutput.value &&
                Objects.equals(recipientAddress, txOutput.recipientAddress) &&
                Objects.equals(parentTransaction.getTxId(), txOutput.parentTransaction.getTxId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, recipientAddress, parentTransaction.getTxId());
    }

    @Override
    public String toString() {
        return "out{" +
                "value=" + value +
                ", recipientPublicKey='" + recipientAddress + '\'' +
                '}';
    }
}
