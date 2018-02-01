package com.theovier.democoin.client;

import com.theovier.democoin.common.io.Wallet;
import com.theovier.democoin.common.transaction.TxInput;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

public class InputKeyHelper {

    private final TxInput input;
    private final KeyPair keyPair;

    InputKeyHelper(final TxInput input, final String key) throws IOException, GeneralSecurityException {
        this.input = input;
        this.keyPair = Wallet.loadKeyPair(key);
    }

    public TxInput getInput() {
        return input;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
