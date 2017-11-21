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

    protected TxInput[] inputs;
    protected TxOutput[] outputs;


    public Transaction(TxInput[] inputs, TxOutput[] outputs, String msg) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.msg = msg;
        this.timestamp = Instant.now().getEpochSecond();
        this.hash = computeHash();
    }

    protected Transaction(String msg) {
        this.msg = msg;
        this.timestamp = Instant.now().getEpochSecond();
    }

    public String computeHash() {
        String content = String.valueOf(timestamp);
        return DigestUtils.sha256Hex(content);
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
