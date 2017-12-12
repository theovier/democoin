package com.theovier.democoin.common;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class CommonMain {

    private static final Logger LOG = Logger.getLogger(CommonMain.class);

    public static void main (String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Demo miner = new Demo();
        //miner.generateDemoBlockchainFile();
        miner.demoBlockchain();
    }
}
