package com.theovier.democoin.common.io;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;

import java.security.Security;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {


    @BeforeEach
    void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }


}