package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.transaction.*;
import org.apache.log4j.Logger;

import java.security.KeyPair;


public class Demo {

    private static final Logger LOG = Logger.getLogger(Demo.class);
    private Blockchain blockchain = new Blockchain();

    public void demoBlockchain() {
        UTXOPool.compute(blockchain);

        Wallet wallet = new Wallet();
        KeyPair keypair = wallet.getKeyPair();
        Address target = Address.generateAddress(keypair.getPublic());

        Transaction tx1 = new Transaction(".");

        //reference the genesisBlock coinbase transaction.
        TxInput input1 = new TxInput(new Sha256Hash("172e5b6c0339375a7ea60e9293effc2f861a9a046a682d45169a9dab890f5f80"), 0);
        tx1.addInput(input1);
        tx1.addOutput(target, 20);
        tx1.addOutput(target, 20);

        tx1.signInput(0, keypair);
        tx1.build();

        Block block = new Block(blockchain.getLastBlock(),  0, target, tx1);
        blockchain.append(block);
        LOG.info(blockchain);
        blockchain.save();
    }
}
