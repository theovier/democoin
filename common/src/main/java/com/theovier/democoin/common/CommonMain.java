package com.theovier.democoin.common;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.Security;

public class CommonMain {

    public static void main (String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        Demo demo = new Demo();
        demo.demoMining();
    }
}
