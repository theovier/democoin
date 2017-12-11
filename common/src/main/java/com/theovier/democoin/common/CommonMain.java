package com.theovier.democoin.common;

import com.theovier.democoin.common.templates.FillableTemplate;
import com.theovier.democoin.common.templates.TransactionTemplate;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class CommonMain {

    private static final Logger LOG = Logger.getLogger(CommonMain.class);

    public static void main (String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Demo miner = new Demo();
        //miner.demoTransactions();
        miner.demoBlockchain();
    }
}
