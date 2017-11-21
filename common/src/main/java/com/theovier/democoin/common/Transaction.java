package com.theovier.democoin.common;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;

public class Transaction implements Serializable {

    private static final long serialVersionUID = -3564602822987321657L;

    protected String hash; //identifier txid
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
        this.hash = computeHash();
    }

    protected Transaction(String msg) {
        this.msg = msg;
        this.timestamp = Instant.now().getEpochSecond();
        this.isCoinbaseTx = true;
    }

    public String computeHash() {
        String content = String.valueOf(timestamp);
        return DigestUtils.sha256Hex(content);
    }

    public boolean isCoinbaseTx() {
        return isCoinbaseTx;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "TX{" +
                "msg='" + msg + '\'' +
                ", inputs=" + Arrays.toString(inputs) +
                ", outputs=" + Arrays.toString(outputs) +
                ", hash='" + hash + '\'' +
                '}';
    }
}
