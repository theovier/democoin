package com.theovier.democoin.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class CommonMain {

    public static void main (String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Demo miner = new Demo();
        //miner.initBlockchain();
        //miner.demo();
        miner.demoTransactions();

    }
}
