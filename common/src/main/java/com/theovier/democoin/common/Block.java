package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.templates.BlockTemplate;
import com.theovier.democoin.common.templates.FillableTemplate;
import com.theovier.democoin.common.transaction.CoinbaseTransaction;
import com.theovier.democoin.common.transaction.Transaction;
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
    private static final Address MINER_ADDRESS = new Address("1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB");

    private long index;
    private long timestamp;
    private Sha256Hash previousBlockHash;
    private Sha256Hash hash;

    private long nonce;

    private List<Transaction> transactions = new ArrayList<>();
    private Sha256Hash merkleRoot;

    public Block(Block predecessor, List<Transaction> transactions, long nonce, Address coinbaseRecipient) {
        this.index = predecessor.getIndex() + 1;
        this.timestamp = Instant.now().getEpochSecond();
        this.previousBlockHash = predecessor.getHash();
        this.nonce = nonce;
        this.transactions.add(new CoinbaseTransaction(coinbaseRecipient));
        this.transactions.addAll(transactions);
        this.merkleRoot = computeMerkleRoot();
        this.hash = computeHash();
    }

    //GenesisBlock
    private Block() {
        this.index = 0;
        this.timestamp = Instant.now().getEpochSecond();
        this.previousBlockHash = Sha256Hash.ZERO_HASH;
        this.nonce = -1;
        this.transactions.add(new CoinbaseTransaction(MINER_ADDRESS));
        this.merkleRoot = computeMerkleRoot();
        this.hash = computeHash();
    }

    public Sha256Hash computeHash() {
        StringBuilder blockContent = new StringBuilder();
        blockContent.append(String.valueOf(index));
        blockContent.append(String.valueOf(timestamp));
        blockContent.append(String.valueOf(nonce));
        blockContent.append(previousBlockHash);
        blockContent.append(merkleRoot);
        return Sha256Hash.create(blockContent.toString());
    }

    //todo if only 1 element hash with duplicate -> atm. genesis block merkle root = tx hash.
    public Sha256Hash computeMerkleRoot() {
        Queue<Sha256Hash> hashQueue = new LinkedList<>(transactions.stream().map(Transaction::getTxId).collect(Collectors.toList()));
        while (hashQueue.size() > 1) {
            String hashableData = hashQueue.poll().toString() + hashQueue.poll().toString();
            hashQueue.add(Sha256Hash.create(hashableData));
        }
        return hashQueue.poll();
    }

    public static Block generateGenesisBlock() {
        return new Block();
    }

    public long getIndex() {
        return index;
    }

    public Sha256Hash getHash() {
        return hash;
    }

    public Sha256Hash getPreviousBlockHash() {
        return previousBlockHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getNonce() {
        return nonce;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Sha256Hash getMerkleRoot() {
        return merkleRoot;
    }

    public String toXML() {
        return new BlockTemplate(this).getFilledTemplate();
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
                ", Transactions=" + System.lineSeparator() + '\t' +
                StringUtils.join(transactions, System.lineSeparator() + '\t')  + System.lineSeparator() +
                '}';
    }
}
