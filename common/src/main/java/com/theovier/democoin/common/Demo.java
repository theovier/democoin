package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.common.transaction.TxInput;
import org.apache.log4j.Logger;

import java.security.KeyPair;


public class Demo {

    private static final Logger LOG = Logger.getLogger(Demo.class);
    private Blockchain blockchain = Blockchain.loadFromDisc();

    public void demoMining() {


        LOG.info(blockchain.isValid() + ", " + blockchain.getHeight());


        Wallet wallet = new Wallet();
        KeyPair keypair = wallet.getKeyPair();
        Address target = Address.generateAddress(keypair.getPublic());

        Transaction tx1 = new Transaction("my message");
        TxInput input1 = new TxInput(new Sha256Hash("9d359f05f53053aaa0618d4f0894abd364b94dcc4d18b0fd937244dc49f646bf"), 0);
        tx1.addInput(input1);
        tx1.addOutput(target, 70);
        tx1.signInput(0, keypair);
        tx1.build();

        LOG.info(blockchain.addToMemPool(tx1));

        Miner miner = new Miner(blockchain, target, "mined by theo's computer");
        miner.start();

    }
}
