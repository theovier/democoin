package com.theovier.democoin.client;

import com.theovier.democoin.common.io.Wallet;
import com.theovier.democoin.common.transaction.TxInput;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

public class InputKeyHelper {

    private final TxInput input;
    private final KeyPair keyPair;

    InputKeyHelper(final TxInput input, final String key) throws IOException, GeneralSecurityException {
        this.input = input;
        File file = new File(key);
        this.keyPair = Wallet.loadKeyPair(file);
    }

    public TxInput getInput() {
        return input;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
