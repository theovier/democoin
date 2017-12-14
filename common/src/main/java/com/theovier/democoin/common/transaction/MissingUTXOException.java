package com.theovier.democoin.common.transaction;

public class MissingUTXOException extends Exception {

    public MissingUTXOException(TxOutputPointer invalidReference) {
        super(invalidReference.toString());
    }
}
