package com.theovier.democoin.common.transaction;


import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.common.Utils;
import com.theovier.democoin.common.crypto.Sha256Hash;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.theovier.democoin.common.ConsensusParams.DEFAULT_TX_MSG;

public class Transaction implements Serializable {
    private static final long serialVersionUID = -3564602822987321657L;

    private Sha256Hash txId;
    private long timestamp;
    private String msg;
    private ArrayList<TxInput> inputs = new ArrayList<>();
    private ArrayList<TxOutput> outputs = new ArrayList<>();
    private long transactionFee = 0;

    public Transaction(String msg) {
        this.msg = Utils.escapeText(msg);
        this.timestamp = Instant.now().getEpochSecond();
    }

    public Transaction() {
        this.msg = Utils.escapeText(DEFAULT_TX_MSG);
        this.timestamp = Instant.now().getEpochSecond();
    }

    //used for deserialization
    private Object readResolve() {
        //we don't save the TxOutput parent references in xml, so set them manually.
        this.inputs.forEach(in -> in.setParentTransaction(this));
        this.outputs.forEach(out -> out.setParentTransaction(this));
        return this;
    }

    public void build() {
        this.txId = computeHash();
    }

    public TxInput addInput(Sha256Hash spendTxHash, int outputIndex) {
        return addInput(new TxInput(this, spendTxHash, outputIndex));
    }

    public TxInput addInput(TxOutput from) {
        return addInput(new TxInput(from));
    }

    public TxInput addInput(TxInput input) {
        input.setParentTransaction(this);
        inputs.add(input);
        return input;
    }

    public TxOutput addOutput(Address recipientAddress, long value) {
        return addOutput(new TxOutput(this, recipientAddress, value));
    }

    public TxOutput addOutput(TxOutput out) {
        out.setParentTransaction(this);
        this.outputs.add(out);
        return out;
    }

    /** call only if all needed inputs and outputs are already added.*/
    public boolean signInput(int index, KeyPair keyPair) {
        return inputs.get(index).sign(keyPair);
    }

    public Sha256Hash getSignableHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp);
        sb.append(msg);
        inputs.forEach(input -> sb.append(input.unsigned()));
        outputs.forEach(output -> sb.append(output));
        return Sha256Hash.create(sb.toString());
    }

    public Sha256Hash computeHash() {
        StringBuilder txContent = new StringBuilder();
        txContent.append(timestamp);
        txContent.append(msg);
        txContent.append(inputs);
        txContent.append(outputs);
        return Sha256Hash.create(txContent.toString());
    }

    public Sha256Hash getTxId() {
        return txId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return msg;
    }

    public List<TxInput> getInputs() {
        return inputs;
    }

    public List<TxOutput> getOutputs() {
        return outputs;
    }

    public TxInput getFirstInput() {
        return getInputs().get(0);
    }

    public TxOutput getFirstOutput() {
        return getOutputs().get(0);
    }

    public long getTransactionFee() {
        return transactionFee;
    }

    /**
     * has to be called after the validation, to make sure each input references an actual output.
     */
    public void calculateTransactionFee() {
        long sumInputs = calculateSumInputs();
        long sumOutputs = calculateSumOutputs();
        transactionFee = sumInputs - sumOutputs;
    }

    /**
     * @return the sum of the input values.
     */
    public long calculateSumInputs() {
        return inputs.stream().mapToLong(TxInput::getClaimedValue).sum();
    }

    /**
     * @return the sum of the output values.
     */
    public long calculateSumOutputs() {
        return outputs.stream().mapToLong(TxOutput::getValue).sum();
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        build();
    }

    public void setMessage(final String msg) {
        this.msg = Utils.escapeText(msg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return timestamp == that.timestamp &&
                transactionFee == that.transactionFee &&
                Objects.equals(txId, that.txId) &&
                Objects.equals(msg, that.msg) &&
                Objects.equals(inputs, that.inputs) &&
                Objects.equals(outputs, that.outputs);
    }

    @Override
    public int hashCode() {

        return Objects.hash(txId, timestamp, msg, inputs, outputs, transactionFee);
    }

    @Override
    public String toString() {
        return "TX{" +
                "timestamp=" + timestamp +
                ", txId=" + txId +
                ", msg='" + msg + '\'' +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }
}
