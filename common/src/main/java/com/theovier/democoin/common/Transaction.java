package com.theovier.democoin.common;


import com.theovier.democoin.common.crypto.Sha256Hash;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;

public class Transaction implements Serializable {

    private static final long serialVersionUID = -3564602822987321657L;

    protected Sha256Hash txId;
    private long timestamp;

    private String msg;
    private boolean isCoinbaseTx;
    protected TxInput[] inputs;
    protected TxOutput[] outputs;


    public Transaction(TxInput[] inputs, TxOutput[] outputs, String msg) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.msg = msg;
        this.timestamp = Instant.now().getEpochSecond();
        this.isCoinbaseTx = false;
        this.txId = computeHash();
    }

    protected Transaction(String msg) {
        this.msg = msg;
        this.timestamp = Instant.now().getEpochSecond();
        this.isCoinbaseTx = true;
    }

    public Sha256Hash computeHash() {
        StringBuilder txContent = new StringBuilder();
        txContent.append(String.valueOf(timestamp));
        txContent.append(msg);
        txContent.append(String.valueOf(isCoinbaseTx));
        txContent.append(String.valueOf(inputs));
        txContent.append(String.valueOf(outputs));
        return Sha256Hash.create(txContent.toString());
    }

    public boolean isCoinbaseTx() {
        return isCoinbaseTx;
    }

    public Sha256Hash getTxId() {
        return txId;
    }

    @Override
    public String toString() {
        return "TX{" +
                "msg='" + msg + '\'' +
                ", inputs=" + Arrays.toString(inputs) +
                ", outputs=" + Arrays.toString(outputs) +
                ", txId='" + txId + '\'' +
                '}';
    }
}
