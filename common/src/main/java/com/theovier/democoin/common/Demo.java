package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.transaction.CoinbaseTransaction;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.common.transaction.TransactionValidator;
import com.theovier.democoin.common.transaction.UTXOPool;
import org.apache.log4j.Logger;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;


public class Demo {

    private static final Logger LOG = Logger.getLogger(Demo.class);
    private Blockchain blockchain = new Blockchain();

    public void demoTransactions() {
        Wallet wallet = new Wallet();
        KeyPair keypair = wallet.getKeyPair();

        Address target = Address.generateAddress(keypair.getPublic());
        Transaction coinbaseTx = new CoinbaseTransaction(target);
        UTXOPool.add(coinbaseTx);

        Transaction tx1 = new Transaction(".");
        tx1.addInput(coinbaseTx.getFirstOutput());
        tx1.signInput(0, keypair);
        tx1.build();

        Transaction tx2 = new Transaction(".");
        tx2.addInput(coinbaseTx.getFirstOutput());
        tx2.signInput(0, keypair);
        tx2.build();

        LOG.info(coinbaseTx);
        LOG.info(tx1);
        LOG.info(tx2);
        LOG.info(TransactionValidator.validate(tx1));
        LOG.info(TransactionValidator.validate(tx2));
    }

    public void generateDemoBlockchainFile() {
        //remove blockchain.dat before calling
        // -> only 1 genesis block with 1 coinbase TX is present
        //because of the blockchain = new Blockchain();

//        blockchain.save();
        Block block = new Block(blockchain.getLastBlock(),  new ArrayList<>(), 0, new Address("..."));
        blockchain.append(block);
        LOG.info(blockchain);
        UTXOPool.compute(blockchain);
    }

    public void demoBlockchain() {
        UTXOPool.compute(blockchain);

        Wallet wallet = new Wallet();
        KeyPair keypair = wallet.getKeyPair();
        Address target = Address.generateAddress(keypair.getPublic());

        Transaction tx1 = new Transaction(".");

        //reference the genesisBlock coinbase transaction.
        tx1.addInput(new Sha256Hash("eba690d49ef7a59efd057f22984989ab7f6f597dc6d63442fb75139c4655efd4"), 0);
        tx1.addOutput(target, 20);
        tx1.signInput(0, keypair);
        tx1.build();

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(tx1);

        Block block = new Block(blockchain.getLastBlock(),  transactions, 0, target);
        blockchain.append(block);
        LOG.info(blockchain);
    }
}
