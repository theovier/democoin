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
        byte[] unsignedData = "SIGNME".getBytes("UTF-8");
        byte[] signedData = SignatureUtils.sign(unsignedData, pair.getPrivate());
        assertTrue(SignatureUtils.verify(signedData, pair.getPublic(), unsignedData));
    }
}