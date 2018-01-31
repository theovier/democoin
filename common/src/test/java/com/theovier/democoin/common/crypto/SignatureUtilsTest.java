package com.theovier.democoin.common.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;

import static com.theovier.democoin.common.crypto.SignatureUtils.generateKeyPair;
import static org.junit.jupiter.api.Assertions.*;

class SignatureUtilsTest {

    @BeforeEach
    void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void getPublicKeyFromPrivateKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        PublicKey copy = SignatureUtils.getPublicKeyFromPrivateKey((ECPrivateKey)pair.getPrivate());
        assertEquals(publicKey, copy);
    }

    @Test
    void getKeyPairFromPrivateKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        KeyPair copy = SignatureUtils.getKeyPairFromPrivateKey((ECPrivateKey)pair.getPrivate());
        assertEquals(pair.getPublic(), copy.getPublic());
        assertEquals(pair.getPrivate(), copy.getPrivate());
    }
}