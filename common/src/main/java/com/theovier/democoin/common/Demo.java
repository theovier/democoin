package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.transaction.*;
import org.apache.log4j.Logger;

import java.security.KeyPair;


public class Demo {

    private static final Logger LOG = Logger.getLogger(Demo.class);
    private Blockchain blockchain = new Blockchain();

    public void demoMining() {
        blockchain.load();
        UTXOPool.compute(blockchain);

        Wallet wallet = new Wallet();
        KeyPair keypair = wallet.getKeyPair();
        Address target = Address.generateAddress(keypair.getPublic());

        /*
        //simulate transaction pool
        Transaction tx1 = new Transaction("my message");
        TxInput input1 = new TxInput(new Sha256Hash("79fa92b596eced05d2baf130b4e9c5dc1b3c06dc7e233cd16f78697853887892"), 0); //50
        TxInput input2 = new TxInput(new Sha256Hash("ad0053ac6d208e60a3f8de957b4d68a9a838a2d6b743b612db427e83e6640976"), 0); //130
        tx1.addInput(input1);
        tx1.addInput(input2);
        tx1.addOutput(target, 130);
        tx1.addOutput(target, 20);
        tx1.signInput(0, keypair);
        tx1.signInput(1, keypair);
        tx1.build();

        LOG.info(TransactionPool.add(tx1));
        */

        Miner miner = new Miner(blockchain, target, "mined by theo's computer");
        miner.start();
    }
}
