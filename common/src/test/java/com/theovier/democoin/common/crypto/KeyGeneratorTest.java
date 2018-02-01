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
    void getPublicKeyFromPrivateKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        PublicKey copy = KeyGenerator.getPublicKeyFromPrivateKey((ECPrivateKey)pair.getPrivate());
        assertEquals(publicKey, copy);
    }

    @Test
    void getKeyPairFromPrivateKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        KeyPair copy = KeyGenerator.getKeyPairFromPrivateKey((ECPrivateKey)pair.getPrivate());
        assertEquals(pair.getPublic(), copy.getPublic());
        assertEquals(pair.getPrivate(), copy.getPrivate());
    }

    @Test
    void getKeyPairFromPrivateKeyHex() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        String privateKeyHex = Hex.toHexString(pair.getPrivate().getEncoded());
        KeyPair copy = KeyGenerator.getKeyPairFromPrivateKey(privateKeyHex);
        assertEquals(pair.getPublic(), copy.getPublic());
        assertEquals(pair.getPrivate(), copy.getPrivate());
    }

    @Test
    void getPublicKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        byte[] x509key = publicKey.getEncoded();
        PublicKey copy = KeyGenerator.getPublicKey(x509key);
        assertEquals(publicKey, copy);
    }

    @Test
    void getPrivateKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        byte[] pkcs8key = privateKey.getEncoded();
        PrivateKey copy = KeyGenerator.getPrivateKey(pkcs8key);
        assertEquals(privateKey, copy);
    }

    @Test
    void getPublicKeyFromHex() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        String publicKeyHex = Hex.toHexString(publicKey.getEncoded());
        PublicKey copy = KeyGenerator.getPublicKey(publicKeyHex);
        assertEquals(publicKey, copy);
    }

    @Test
    void getPrivateKeyFromHex() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        String privateKeyHex = Hex.toHexString(privateKey.getEncoded());
        PrivateKey copy = KeyGenerator.getPrivateKey(privateKeyHex);
        assertEquals(privateKey, copy);
    }
}