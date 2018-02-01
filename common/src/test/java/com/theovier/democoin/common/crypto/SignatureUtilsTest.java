package com.theovier.democoin.common.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.security.*;
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

    @Test
    void getPublicKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        byte[] x509key = publicKey.getEncoded();
        PublicKey copy = SignatureUtils.getPublicKey(x509key);
        assertEquals(publicKey, copy);
    }

    @Test
    void getPrivateKey() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        byte[] pkcs8key = privateKey.getEncoded();
        PrivateKey copy = SignatureUtils.getPrivateKey(pkcs8key);
        assertEquals(privateKey, copy);
    }

    @Test
    void getPublicKeyFromHex() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        String publicKeyHex = Hex.toHexString(publicKey.getEncoded());
        PublicKey copy = SignatureUtils.getPublicKey(publicKeyHex);
        assertEquals(publicKey, copy);
    }

    @Test
    void getPrivateKeyFromHex() throws GeneralSecurityException {
        KeyPair pair = generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        String privateKeyHex = Hex.toHexString(privateKey.getEncoded());
        PrivateKey copy = SignatureUtils.getPrivateKey(privateKeyHex);
        assertEquals(privateKey, copy);
    }

    @Test
    void signAndVerify() throws GeneralSecurityException, UnsupportedEncodingException {
        KeyPair pair = generateKeyPair();
        byte[] publicKeyBytes = pair.getPublic().getEncoded();
        byte[] privateKeyBytes = pair.getPrivate().getEncoded();
        byte[] unsignedData = "SIGNME".getBytes("UTF-8");
        byte[] signedData = SignatureUtils.sign(unsignedData, privateKeyBytes);
        assertTrue(SignatureUtils.verify(signedData, publicKeyBytes, unsignedData));
    }
}