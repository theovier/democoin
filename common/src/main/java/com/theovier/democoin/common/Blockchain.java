package com.theovier.democoin.common;

import com.theovier.democoin.common.templates.BlockChainTemplate;
import com.theovier.democoin.common.templates.FillableTemplate;
import com.theovier.democoin.common.transaction.TransactionPool;
import com.theovier.democoin.common.transaction.UTXOPool;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class Blockchain {
    private static final Logger LOG = Logger.getLogger(Blockchain.class);
    private FillableTemplate template = new BlockChainTemplate(this);
    private ArrayList<Block> blockchain = new ArrayList<>();

    public Blockchain() {
        load();
    }

    public boolean save() {
        try {
            FileOutputStream fout = new FileOutputStream(Config.BLOCKCHAIN_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(blockchain);
            LOG.info("saved blockchain.");
            FileUtils.writeStringToFile(new File("blockchain.xml"), toXML(), "UTF-8");
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    public boolean load() {
        try {
            FileInputStream fin = new FileInputStream(Config.BLOCKCHAIN_FILE);
            ObjectInputStream ois = new ObjectInputStream(fin);
            this.blockchain = (ArrayList<Block>)ois.readObject();
            return true;
        } catch (Exception e) {
            LOG.warn("could not load blockchain - generating GenesisBlock");
            appendGensisBlock();
        }
        return false;
    }

    private void appendGensisBlock() {
        Block genesis = Block.generateGenesisBlock();
        blockchain.add(genesis);
        genesis.getTransactions().forEach(UTXOPool::add); //add transactions outputs to the UTXO.
    }

    public synchronized boolean append(Block block) {
        if (BlockValidator.isValid(block, getLastBlock())) {
            blockchain.add(block);
            block.getTransactions().forEach(TransactionPool::remove);//remove included transactions from (pending) transaction pool
            block.getTransactions().forEach(UTXOPool::add); //add transactions outputs to the UTXO.
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

    public ArrayList<Block> getBlocks() {
        return blockchain;
    }

    public synchronized String toXML() {
        return template.getFilledTemplate();
    }

    @Override
    public synchronized String toString() {
        return toXML();
    }
}
