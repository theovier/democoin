package com.theovier.democoin.common;

import com.theovier.democoin.common.templates.BlockChainTemplate;
import com.theovier.democoin.common.templates.FillableTemplate;
import com.theovier.democoin.common.transaction.TransactionPool;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            blockchain.add(Block.generateGenesisBlock());
        }
        return false;
    }

    public synchronized boolean append(Block block) {
        if (BlockValidator.isValid(block, getLastBlock())) {
            blockchain.add(block);
            block.getTransactions().forEach(TransactionPool::remove);//remove included transactions from transaction pool
            return true;
        }
        return false;
    }

    public Block getLastBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }
        return blockchain.get(blockchain.size() - 1);
    }

    public ArrayList<Block> getBlocks() {
        return blockchain;
    }

    public String toXML() {
        return template.getFilledTemplate();
    }

    @Override
    public String toString() {
        return toXML();
    }
}
