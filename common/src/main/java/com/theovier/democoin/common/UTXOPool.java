package com.theovier.democoin.common;

import java.util.ArrayList;
import java.util.List;

public class UTXOPool {

    private static List<TxOutput> unspentOutputs = new ArrayList<>();

    public boolean loadFromFile() {
        //Unspent Transaction Output -> save as utxo.dat
        return false;
    }

    public boolean saveToFile() {
        return false;
    }

    public static void add(Transaction transaction) {
        transaction.getOutputs().forEach(out -> unspentOutputs.add(out));
    }

    public static TxOutput getUTXO(TxOutputPointer reference) throws MissingUTXOException {
        int index = getUTXOIndex(reference);
        if (index != -1) {
            return unspentOutputs.remove(index);
        }
        throw new MissingUTXOException();
    }

    private static int getUTXOIndex(TxOutputPointer reference) {
        for (int i=0; i < unspentOutputs.size(); i++) {
            TxOutput utxo = unspentOutputs.get(i);
            Transaction containingTx = utxo.getParentTransaction();
            if (containingTx.getTxId().equals(reference.getTransactionHash())) {
                if (utxo.getIndex() == reference.getOutputIndex()) {
                    return i;
                }
            }
        }
        return -1;
    }
}
