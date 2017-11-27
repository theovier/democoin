package com.theovier.democoin.common;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UTXOPool {

    //Unspent Transaction Output -> save as utxo.dat

    //make sure the transaction is removed if all output has been spent.
    private Map<String, Transaction> utxo = new HashMap<>();

    public void loadFromFile(Path path) {

    }

    public TxOutput getUTXO(String transasctionHash, int TxOutputIndex) {
        return null;
    }

}
