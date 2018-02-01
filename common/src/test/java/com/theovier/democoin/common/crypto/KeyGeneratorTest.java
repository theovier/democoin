package com.theovier.democoin.common.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.security.interfaces.ECPrivateKey;

import static com.theovier.democoin.common.crypto.KeyGenerator.generateKeyPair;
import static org.junit.jupiter.api.Assertions.*;

class KeyGeneratorTest {

    @BeforeEach
    void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void getKeyPairFromPrivateKeyHex() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        ECPrivateKey privateKey = (ECPrivateKey) pair.getPrivate();
        String privateHex = Hex.toHexString(privateKey.getS().toByteArray());
        KeyPair copyPair = KeyGenerator.getKeyPairFromPrivateKeyHex(privateHex);
        assertEquals(pair.getPublic(), copyPair.getPublic());
        assertEquals(pair.getPrivate(), copyPair.getPrivate());
    }

    @Test
    void getKeyPairFromPrivateKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        KeyPair copy = KeyGenerator.getKeyPairFromPrivateKey((ECPrivateKey)pair.getPrivate());
        assertEquals(pair.getPublic(), copy.getPublic());
        assertEquals(pair.getPrivate(), copy.getPrivate());
    }

    @Test
    void getPublicKeyFromPrivateKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        PublicKey copy = KeyGenerator.getPublicKeyFromPrivateKey((ECPrivateKey)pair.getPrivate());
        assertEquals(publicKey, copy);
    }

    @Test
    void getPublicKeyFromHex() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        String publicHex = Hex.toHexString(pair.getPublic().getEncoded());
        PublicKey copy = KeyGenerator.getPublicKey(publicHex);
        assertEquals(pair.getPublic(), copy);
    }
}