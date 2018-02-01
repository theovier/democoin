package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.io.Wallet;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.common.transaction.TxInput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;


public class Demo {

    private static final Logger LOG = Logger.getLogger(Demo.class);
    private Blockchain blockchain = Blockchain.loadFromDisc();

    public void demoMining() {

        Wallet wallet = new Wallet();
        KeyPair keypair = null;
        try {
            File file = new File("1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB.key");
            keypair = wallet.loadKeyPair(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        Address target = Address.generateAddress(keypair.getPublic());

        Transaction tx1 = new Transaction("broadcast tx");
        TxInput input1 = new TxInput(new Sha256Hash("986afc4e482ff3fec2527cfa1140e2991bc500aa4022b30a4e8719f5e1eebb71"), 0);
        tx1.addInput(input1);
        tx1.addOutput(target, 130);
        input1.sign(keypair);
        //tx1.signInput(0, keypair);
        tx1.build();

        LOG.info(blockchain.addToMemPool(tx1));
    }
}
