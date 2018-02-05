package com.theovier.democoin.client;

import com.theovier.democoin.common.crypto.KeyGenerator;
import com.theovier.democoin.common.io.Wallet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;

public class AddressKeyGenerator {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AddressKeyGenerator.class);

    public static void main (String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        try {
            generateAddressKeyFile();
        } catch (IOException | GeneralSecurityException e) {
            LOG.fatal("could not generate {address, key} pair", e);
        }
    }

    private static void generateAddressKeyFile() throws GeneralSecurityException, IOException {
        KeyPair keyPair = KeyGenerator.generateKeyPair();
        Wallet.saveKeyPair(keyPair);
    }
}
