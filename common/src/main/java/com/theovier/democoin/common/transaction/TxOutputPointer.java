package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.crypto.Sha256Hash;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Objects;

public class TxOutputPointer implements Serializable {

    private static final Logger LOG = Logger.getLogger(TxOutputPointer.class);
    private static final long serialVersionUID = 5743551940949924366L;
    private Sha256Hash transactionHash;
    private int outputIndex;

    public TxOutputPointer(final Sha256Hash transactionHash, final int outputIndex) {
        this.transactionHash = transactionHash;
        this.outputIndex = outputIndex;
        LOG.info(this);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxOutputPointer that = (TxOutputPointer) o;
        return outputIndex == that.outputIndex &&
                Objects.equals(transactionHash, that.transactionHash);
    }

    @Override
    public int hashCode() {

        return Objects.hash(transactionHash, outputIndex);
    }

    @Override
    public String toString() {
        return "TxOutputPointer{" +
                "transactionHash=" + transactionHash +
                ", outputIndex=" + outputIndex +
                '}';
    }
}
