package com.theovier.democoin.common;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class DemoMiner {

    private static final Logger LOG = Logger.getLogger(DemoMiner.class);
    private Blockchain blockchain = new Blockchain();
    private Queue<Transaction> transactionPool = new LinkedList<>();
    private List<Transaction> orphanTransactions = new ArrayList<>(); //transactions who wait for 1 or more inputs to be confirmed.
    private List<TxOutput> UTXO = new ArrayList<>(); //Unspent Transaction Output -> save as utxo.dat

    public void initBlockchain() {
        if (!loadBlockchain()) {
            Block genesisBlock = Block.generateGenesisBlock();
            blockchain.add(genesisBlock);
        }
    }

    public boolean loadBlockchain() {
        try {
            FileInputStream fin = new FileInputStream("blockchain.dat");
            ObjectInputStream ois = new ObjectInputStream(fin);
            blockchain = (Blockchain)ois.readObject();
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    public void saveBlockchain() {
        try {
            FileOutputStream fout = new FileOutputStream("blockchain.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(blockchain);
            LOG.info("saved blockchain.");
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public void demo() {
        Block block = createBlock();
        if (isValidBlock(block, blockchain.getLastBlock())) {
            blockchain.add(block);
        }
        LOG.info(blockchain);
        saveBlockchain();
    }

    private void fillTransactionPool() {
        //simulate filled transaction pool
        TxInput[] inputs = new TxInput[1];
        TxInput input = new TxInput("prev_tx_hash", 0, "signature");
        inputs[0] = input;
        Transaction genericTx = new Transaction(inputs, new TxOutput[0],".");
        transactionPool.add(genericTx);
    }

    public void demoTransactions() {
        fillTransactionPool();
        List<Transaction> txInBlock = new ArrayList<>();
        Transaction coinbaseTx = new CoinbaseTransaction("todo");
        txInBlock.add(coinbaseTx);
        txInBlock.add(transactionPool.poll());
        txInBlock.forEach((tx) -> LOG.info(tx));
    }

    public Block createBlock() {
        fillTransactionPool();
        Block previousBlock = blockchain.getLastBlock();
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transactionPool.poll());

        Block block = new Block(previousBlock,  transactions, 0);
        return block;
    }

    public static boolean isValidBlock(Block newBlock , Block previousBlock) {
        if (newBlock.getIndex() != previousBlock.getIndex() + 1) {
            LOG.info("invalid index" + newBlock.getIndex());
            return false;
        } else if (!newBlock.getPreviousBlockHash().equals(previousBlock.getHash())) {
            LOG.info("invalid previous hash");
            return false;
        }
        return true;
    }
}
