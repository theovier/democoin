package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.templates.FillableTemplate;
import com.theovier.democoin.common.templates.TxOutputTemplate;

import java.io.Serializable;
import java.util.List;

public class TxOutput implements Serializable {

    /** An output of a transaction. It contains the public key that the next input
     * must be able to sign with to claim it.
     */

    private static final long serialVersionUID = -4403978077387051602L;

    private long value;
    private Address recipientAddress; //was recipientPublicKey; //org: scriptPubKey
    private Transaction parentTransaction;

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

    public String toXML() {
        return new TxOutputTemplate(this).getFilledTemplate();
    }

    @Override
    public String toString() {
        return "out{" +
                "value=" + value +
                ", recipientPublicKey='" + recipientAddress + '\'' +
                '}';
    }
}
