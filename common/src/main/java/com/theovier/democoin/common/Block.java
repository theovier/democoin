package com.theovier.democoin.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Block implements Serializable {

    private static final long serialVersionUID = 1113799434508676095L;

    private long index;
    private long timestamp;
    private String previousBlockHash;
    private String hash;

    private long nonce;

    private List<Transaction> transactions;
    private String merkleRoot;

    public Block(Block predecessor, List<Transaction> transactions, long nonce) {
        this.index = predecessor.getIndex() + 1;
        this.timestamp = Instant.now().getEpochSecond();
        this.previousBlockHash = predecessor.getHash();
        this.nonce = nonce;
        this.transactions = transactions;
        this.transactions.add(new CoinbaseTransaction("todo"));
        this.hash = computeHash();
        this.merkleRoot = computeMerkleRoot();
    }

    //GenesisBlock
    private Block() {
        this.index = 0;
        this.timestamp = Instant.now().getEpochSecond();
        this.previousBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
        this.nonce = 9;
        this.transactions = new ArrayList<>();
        this.transactions.add(new CoinbaseTransaction("todo"));
        this.hash = computeHash();
        this.merkleRoot = computeMerkleRoot();
    }

    private String computeHash() {
        StringBuilder blockContent = new StringBuilder();
        blockContent.append(String.valueOf(index));
        blockContent.append(String.valueOf(timestamp));
        blockContent.append(String.valueOf(nonce));
        blockContent.append(previousBlockHash);
        blockContent.append(merkleRoot);
        return DigestUtils.sha256Hex(blockContent.toString());
    }

    private String computeMerkleRoot() {
        Queue<String> hashQueue = new LinkedList<>(transactions.stream().map(Transaction::getHash).collect(Collectors.toList()));
        while (hashQueue.size() > 1) {
            String hashableData = hashQueue.poll() + hashQueue.poll();
            hashQueue.add(DigestUtils.sha256Hex(hashableData));
        }
        return hashQueue.poll();
    }

    public static Block generateGenesisBlock() {
        return new Block();
    }

    public long getIndex() {
        return index;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", previousBlockHash='" + previousBlockHash + '\'' +
                ", hash='" + hash + '\'' +
                ", nonce='" + nonce + '\'' +
                ", merkleRoot='" + merkleRoot + '\'' +
                ", TX=" + StringUtils.join(transactions)  +
                '}';
    }
}
