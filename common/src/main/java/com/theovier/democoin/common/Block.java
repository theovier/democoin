package com.theovier.democoin.common;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;

public class Block {

    private long index;
    private Instant timestamp;
    private String previousBlockHash;
    private String message;
    private String hash;

    public Block(Block predecessor, String message) {
        this.index = predecessor.getIndex() + 1;
        this.timestamp = Instant.now();
        this.previousBlockHash = predecessor.getHash();
        this.message = message;
        this.hash = generateHash();
    }

    //GenesisBlock
    private Block(String message) {
        this.index = 0;
        this.timestamp = Instant.now();
        this.previousBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
        this.message = message;
        this.hash = generateHash();
    }

    private String generateHash() {
        String blockContent = String.valueOf(index) + String.valueOf(timestamp) + previousBlockHash + message;
        return DigestUtils.sha256Hex(blockContent);
    }

    public boolean isValid() {
        return false;
    }

    public static Block generateGenesisBlock(String message) {
        return new Block(message);
    }

    public long getIndex() {
        return index;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", previousBlockHash='" + previousBlockHash + '\'' +
                ", message='" + message + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
