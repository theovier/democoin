package com.theovier.democoin.client;

import com.theovier.democoin.common.Wallet;
import com.theovier.democoin.common.transaction.TxInput;

import java.security.KeyPair;

public class InputKeyHelper {

    private final TxInput input;
    private final KeyPair keyPair;

    public InputKeyHelper(final TxInput input, final String key) {
        this.input = input;
        this.keyPair = new Wallet().getKeyPair();
    }

    public TxInput getInput() {
        return input;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
