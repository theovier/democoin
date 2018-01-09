package com.theovier.democoin.common;

import com.theovier.democoin.common.templates.BlockChainTemplate;
import com.theovier.democoin.common.transaction.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public final class Blockchain implements Serializable {

    private static final Logger LOG = Logger.getLogger(Blockchain.class);
    private static final long serialVersionUID = 5811480394608466057L;
    private List<Block> blockchain = new LinkedList<>();
    private transient final UTXOPool UTXOPool = new UTXOPool(this);
    private transient final Validator<Transaction> txValidator = new TransactionValidator(UTXOPool);
    private transient final Validator<Block> blockValidator = new BlockValidator(this, txValidator);
    private transient final Validator<Blockchain> blockchainValidator = new BlockchainValidator();
    private transient final TransactionPool memPool = new TransactionPool(txValidator);

    public Blockchain() {
        appendGensisBlock();
    }

    private Blockchain(final List<Block> blocks) {
        this.blockchain = blocks;
        this.UTXOPool.compute();
    }

    //copy constructor for deserialization
    private Blockchain(final Blockchain copy) {
        this.blockchain = copy.getBlocks();
    }

    private Object readResolve() {
        return new Blockchain(this);
    }

    public synchronized boolean saveToDisc() {
        try {
            FileOutputStream fout = new FileOutputStream(Config.BLOCKCHAIN_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(blockchain);
            LOG.info(String.format("saved blockchain, height: %d", getHeight()));
            FileUtils.writeStringToFile(new File("blockchain.xml"), toXML(), "UTF-8");
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    public static Blockchain loadFromDisc() {
        try {
            FileInputStream fin = new FileInputStream(Config.BLOCKCHAIN_FILE);
            ObjectInputStream ois = new ObjectInputStream(fin);
            @SuppressWarnings("unchecked")
            List<Block> blocks = (LinkedList<Block>)ois.readObject();
            return new Blockchain(blocks);
        } catch (Exception e) {
            LOG.warn("could not loadFromDisc blockchain - generating GenesisBlock");
            LOG.debug(e);
            return new Blockchain();
        }
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
    public synchronized void substitute(Blockchain other) {
        this.blockchain = other.getBlocks();
        this.UTXOPool.compute();
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

    private synchronized String toXML() {
        return new BlockChainTemplate(this).getFilledTemplate();
    }

    @Override
    public synchronized String toString() {
        return toXML();
    }
}
