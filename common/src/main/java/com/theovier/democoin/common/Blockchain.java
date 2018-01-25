package com.theovier.democoin.common;

import com.theovier.democoin.common.io.Config;
import com.theovier.democoin.common.io.XMLSerializer;
import com.theovier.democoin.common.transaction.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class Blockchain implements Serializable {

    private static final Logger LOG = Logger.getLogger(Blockchain.class);
    private static final long serialVersionUID = 5811480394608466057L;
    private List<Block> blockchain = new LinkedList<>();
    private transient final UTXOPool UTXOPool = new UTXOPool(this);
    private transient final Validator<Transaction> txValidator = new TransactionValidator(UTXOPool);
    private transient final Validator<Block> blockValidator = new BlockValidator(this, txValidator);
    private transient final Validator<Blockchain> blockchainValidator = new BlockchainValidator();
    private transient final TransactionPool memPool = new TransactionPool(txValidator);
    private static transient final XMLSerializer printer = new XMLSerializer();

    public Blockchain() {
        appendGensisBlock();
    }

    //copy constructor for deserialization
    private Blockchain(final Blockchain copy) {
        this.blockchain = copy.getBlocks();
        this.UTXOPool.compute();
    }

    //used for deserialization
    private Object readResolve() {
        return new Blockchain(this);
    }

    public synchronized boolean saveToDisc() {
        try {
            if (isValid()) {
                printer.saveAsXML(this, Config.BLOCKCHAIN_FILE);
                LOG.info(String.format("saved blockchain, height: %d", getHeight()));
            } else {
                throw new IllegalStateException("blockchain is not valid and therefore can't be saved");
            }
        } catch (IOException e) {
            LOG.error("could not save blockchain", e);
            return false;
        }
       return true;
    }

    public static Blockchain loadFromDisc() {
        try {
            File blockchainXML = new File(Config.BLOCKCHAIN_FILE);
            Blockchain loadedBlockchain = new Blockchain((Blockchain) printer.loadFromXMLFile(blockchainXML));
            if (loadedBlockchain.isValid()) {
                LOG.debug(String.format("blockchain with height %d successfully loaded.", loadedBlockchain.getHeight()));
                return loadedBlockchain;
            }
        } catch (IOException e) {
            LOG.debug(e);
            LOG.warn("could not load blockchain -> generating GenesisBlock");
            return new Blockchain();
        }
        LOG.warn("blockchain file has been corrupted -> generating GenesisBlock");
        return new Blockchain();
    }

    public static Blockchain loadFromFile(final File file) throws IOException {
        return new Blockchain((Blockchain) printer.loadFromXMLFile(file));
    }

    private void appendGensisBlock() {
        Block genesis = Block.generateGenesisBlock();
        blockchain.add(genesis);
        genesis.getTransactions().forEach(UTXOPool::add); //add transactions outputs to the UTXO.
    }

    public synchronized boolean append(Block block) {
        if (blockValidator.isValid(block)) {
            blockchain.add(block);
            block.getTransactions().forEach(memPool::remove);//remove included transactions from (pending) transaction pool
            block.getTransactions().forEach(UTXOPool::add); //add transactions outputs to the UTXO.

            //todo make this pretty.
            block.getTransactions().forEach(
                    tx -> tx.getInputs().forEach(in -> {
                        try {
                            UTXOPool.removeUTXO(in);
                        } catch (MissingUTXOException e) {
                            throw new IllegalStateException("validation must be broken");
                        }
                    })); //remove transaction inputs from the UTXO.
            return true;
        }
        return false;
    }

    /** called when there is a valid, longer blockchain.
     *  we change our status to represent that blockchain
     */
    private synchronized void copy(Blockchain other) {
        this.blockchain = other.getBlocks();
        this.UTXOPool.compute();
    }

    public boolean substitute(Blockchain other) {
        if (getHeight() < other.getHeight() && other.isValid()) {
            copy(other);
            return true;
        }
        return false;
    }

    public synchronized Block getLastBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }
        return blockchain.get(blockchain.size() - 1);
    }

    public List<Block> getBlocks() {
        return blockchain;
    }

    public synchronized Block get(int index) {
        if (index >= blockchain.size()) {
            return null;
        }
        return blockchain.get(index);
    }

    public synchronized Block getGenesisBlock() {
        return get(0);
    }

    public synchronized int getHeight() {
        return blockchain.size();
    }

    public TransactionPool getMemPool() {
        return memPool;
    }

    public boolean isValid() {
        return blockchainValidator.isValid(this);
    }

    public boolean addToMemPool(Transaction tx) {
        return memPool.add(tx);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blockchain that = (Blockchain) o;
        return Objects.equals(blockchain, that.blockchain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockchain);
    }

    @Override
    public String toString() {
        return "Blockchain{" +
                "blockchain=" + blockchain +
                '}';
    }
}
