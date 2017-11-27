package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.SignatureUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;


public class Demo {

    private static final Logger LOG = Logger.getLogger(Demo.class);
    private Blockchain blockchain = new Blockchain();

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



    public Block createBlock() {
        Block previousBlock = blockchain.getLastBlock();
        List<Transaction> transactions = new ArrayList<>();

        Block block = new Block(previousBlock,  transactions, 0);
        return block;
    }

    public static boolean isValidBlock(Block newBlock , Block previousBlock) {
        if (newBlock.getIndex() != previousBlock.getIndex() + 1) {
            LOG.info("invalid index" + newBlock.getIndex());
            return false;
        } else if (!newBlock.getPreviousBlockHash().equals(previousBlock.getHash())) {
            LOG.info("invalid previous txId");
            return false;
        }
        return true;
    }




















    public void demoTransactions() throws Exception {
        byte[] privateKeyBytes = Files.readAllBytes(Paths.get("key.priv"));
        byte[] publicKeyBytes = Files.readAllBytes(Paths.get("key.pub"));
        KeyPair keypair = SignatureUtils.getKeyPair(publicKeyBytes, privateKeyBytes);

        Address target = Address.generateAddress(keypair.getPublic());

        Transaction coinbaseTx = new CoinbaseTransaction(target);

        TxInput[] inputs = new TxInput[1];
        TxInput input = new TxInput(coinbaseTx.getTxId(), 0);
        input.sign(keypair);
        inputs[0] = input;
        Transaction tx1 = new Transaction(inputs, new TxOutput[0],".");


        LOG.info(coinbaseTx);
        LOG.info(tx1);

        //verify tx1   //get this somehow from the UTXOPool by txId
        LOG.info(coinbaseTx.outputs[0].getRecipientAddress().equals(Address.generateAddress(publicKeyBytes)));
        LOG.info(SignatureUtils.verify(tx1.inputs[0].getSignature(), tx1.inputs[0].getPublicKey(), tx1.inputs[0].getUnsignedHash()));
    }


}
