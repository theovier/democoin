package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.templates.BlockTemplate;
import com.theovier.democoin.common.transaction.CoinbaseTransaction;
import com.theovier.democoin.common.transaction.Transaction;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Block implements Serializable {

    private static final long serialVersionUID = 1113799434508676095L;

    private long index;
    private long timestamp;
    private Sha256Hash previousBlockHash;
    private Sha256Hash hash;
    private long nonce;
    private List<Transaction> transactions = new ArrayList<>(); //todo make Collection = new HashSet
    private CoinbaseTransaction coinbaseTx;
    private Sha256Hash merkleRoot;
    private String powTarget;

    public Block(final Block predecessor, final long nonce, final String powTarget, final Address coinbaseRecipient, final String coinbaseMsg, final List<Transaction> transactions) {
        this.index = predecessor.getIndex() + 1;
        this.timestamp = Instant.now().getEpochSecond();
        this.previousBlockHash = predecessor.getHash();
        this.nonce = nonce;
        this.powTarget = powTarget;
        this.coinbaseTx = new CoinbaseTransaction(coinbaseRecipient, coinbaseMsg);
        this.transactions.add(coinbaseTx);
        this.transactions.addAll(transactions);
        this.merkleRoot = computeMerkleRoot();
        this.hash = computeHash();
    }

    public Block(final Block predecessor, final long nonce, final String powTarget, final Address coinbaseRecipient, final List<Transaction> transactions) {
        this.index = predecessor.getIndex() + 1;
        this.timestamp = Instant.now().getEpochSecond();
        this.previousBlockHash = predecessor.getHash();
        this.nonce = nonce;
        this.powTarget = powTarget;
        this.coinbaseTx = new CoinbaseTransaction(coinbaseRecipient);
        this.transactions.add(coinbaseTx);
        this.transactions.addAll(transactions);
        this.merkleRoot = computeMerkleRoot();
        this.hash = computeHash();
    }

    public Block(final Block predecessor, final long nonce, final String powTarget, final Address coinbaseRecipient, final Transaction... transactions) {
        this(predecessor, nonce, powTarget, coinbaseRecipient, Arrays.asList(transactions));
    }

    public Block(final Block predecessor, final long nonce, final String powTarget, final Address coinbaseRecipient, final String optionalCoinbaseMsg, final Transaction... transactions) {
        this(predecessor, nonce, powTarget, coinbaseRecipient, optionalCoinbaseMsg, Arrays.asList(transactions));
    }

    //GenesisBlock, always the same.
    private Block() {
        this.index = 0;
        this.timestamp = Instant.now().getEpochSecond();
        this.previousBlockHash = Sha256Hash.ZERO_HASH;
        this.nonce =  684848142333899113L;
        this.powTarget = ConsensusParams.MIN_DIFFICULTY;
        this.coinbaseTx = new CoinbaseTransaction(ConsensusParams.GENESIS_ADDRESS, "GENESIS");
        this.coinbaseTx.setTimestamp(1513428657);
        this.transactions.add(coinbaseTx);
        this.merkleRoot = computeMerkleRoot();
        this.hash = computeHash();
    }

    public Sha256Hash computeHash() {
        StringBuilder blockContent = new StringBuilder();
        blockContent.append(index);
        blockContent.append(timestamp);
        blockContent.append(nonce);
        blockContent.append(powTarget);
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
        if (hash == null) {
            hash = computeHash();
        }
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

    public CoinbaseTransaction getCoinbaseTx() {
        return coinbaseTx;
    }

    public String getPowTarget() {
        return powTarget;
    }

    //used by the velocity template
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
