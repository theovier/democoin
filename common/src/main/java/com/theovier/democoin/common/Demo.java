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
        TxInput input1 = new TxInput(new Sha256Hash("f99630a720faedb0ac16115fb84d83614488e15919e030e18ec095a226ecdba0"), 0);
        tx1.addInput(input1);
        tx1.addOutput(target, 130);
        //tx1.addOutput(target, 20);

        tx1.signInput(0, keypair);
        tx1.build();

        Block block = new Block(blockchain.getLastBlock(),  0, target, tx1);
        LOG.info(blockchain.append(block));
        blockchain.save();
    }

    public void demoMining() {
        Wallet wallet = new Wallet();
        KeyPair keypair = wallet.getKeyPair();
        Address target = Address.generateAddress(keypair.getPublic());


        Block block = null;
        long nonce = 0;
        while (true) {
            block = new Block(blockchain.getLastBlock(),  nonce, target);
            if (hasProofOfWork(block)) {
                break;
            }
            LOG.info("retry " + block.getHash());
            nonce++;
        }
        blockchain.append(block);
        LOG.info(blockchain.toXML());
    }

    public static boolean hasProofOfWork(Block block) {
        long leadingZeroesCount = block.getLeadingZerosCount();

        if (leadingZeroesCount >= Config.DIFFICULTY) {
            return true;
        }

        return false;
    }
}
