package com.theovier.democoin.common.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.security.*;

import static org.junit.jupiter.api.Assertions.*;

class SignatureUtilsTest {

    @BeforeEach
    void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void signAndVerify() throws GeneralSecurityException, UnsupportedEncodingException {
        KeyPair pair = KeyGenerator.generateKeyPair();
        byte[] publicKeyBytes = pair.getPublic().getEncoded();
        byte[] privateKeyBytes = pair.getPrivate().getEncoded();
        byte[] unsignedData = "SIGNME".getBytes("UTF-8");
        byte[] signedData = SignatureUtils.sign(unsignedData, privateKeyBytes);
        assertTrue(SignatureUtils.verify(signedData, publicKeyBytes, unsignedData));
    }
}