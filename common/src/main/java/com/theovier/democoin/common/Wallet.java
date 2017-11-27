package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.SignatureUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

public class Wallet {

    private static final Logger LOG = Logger.getLogger(Wallet.class);
    private final KeyPair keyPair;

    public Wallet() {
        this.keyPair = loadKeyPair();
    }

    private KeyPair loadKeyPair() {
        try {
            byte[] privateKeyBytes = Files.readAllBytes(Paths.get("key.priv"));
            byte[] publicKeyBytes = Files.readAllBytes(Paths.get("key.pub"));
            return SignatureUtils.getKeyPair(publicKeyBytes, privateKeyBytes);
        } catch (GeneralSecurityException | IOException e) {
            LOG.error("wallet could not retrieve keypair", e);
            throw new IllegalStateException();
        }
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

}
