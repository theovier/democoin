package com.theovier.democoin.common;

import com.theovier.democoin.common.templates.BlockChainTemplate;
import com.theovier.democoin.common.templates.FillableTemplate;
import com.theovier.democoin.common.transaction.MissingUTXOException;
import com.theovier.democoin.common.transaction.TransactionPool;
import com.theovier.democoin.common.transaction.UTXOPool;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Blockchain implements Serializable {

    private transient static final Logger LOG = Logger.getLogger(Blockchain.class);
    private static final long serialVersionUID = 5811480394608466057L;
    private List<Block> blockchain = new LinkedList<>();

    public Blockchain() {
        appendGensisBlock();
    }

    private Blockchain(final List<Block> blocks) {
        this.blockchain = blocks;
        //todo calculate own UTXOPool here
    }

    public synchronized boolean save() {
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

    public static Blockchain load() {
        try {
            FileInputStream fin = new FileInputStream(Config.BLOCKCHAIN_FILE);
            ObjectInputStream ois = new ObjectInputStream(fin);
            List<Block> blocks = (LinkedList<Block>)ois.readObject();
            return new Blockchain(blocks);
        } catch (Exception e) {
            LOG.warn("could not load blockchain - generating GenesisBlock");
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
        if (BlockValidator.isValid(block, this)) {
            blockchain.add(block);
            block.getTransactions().forEach(TransactionPool::remove);//remove included transactions from (pending) transaction pool
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

    public synchronized void substitute(Blockchain other) {
        this.blockchain = other.getBlocks();
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

    private synchronized String toXML() {
        return new BlockChainTemplate(this).getFilledTemplate();
    }

    @Override
    public synchronized String toString() {
        return toXML();
    }
}
