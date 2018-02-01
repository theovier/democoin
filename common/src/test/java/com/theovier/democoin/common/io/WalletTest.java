package com.theovier.democoin.common.io;

import com.theovier.democoin.common.crypto.KeyGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @BeforeEach
    void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void saveAndLoad() throws IOException, GeneralSecurityException {
        //todo
    }

    @Test
    void saveKeyPair() throws IOException {
        //todo
    }

    @Test
    void loadKeyPair() throws IOException, GeneralSecurityException {
        String privateKeyHex = "26059b6cb5979c48453acffe68760b0972eb7d175baf977e140cab5ee6e5b716";
        KeyPair pair = KeyGenerator.getKeyPairFromPrivateKeyHex(privateKeyHex);
        File file = new File(getClass().getResource("/keys/1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB.key").getFile());
        KeyPair copy = Wallet.loadKeyPair(file);
        assertEquals(pair.getPrivate(), copy.getPrivate());
        assertEquals(pair.getPublic(), copy.getPublic());
    }
}