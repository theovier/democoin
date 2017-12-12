package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Blockchain;

import java.util.*;

//todo don't make this static?
public class UTXOPool {

    private static Set<TxOutput> unspentOutputs = new HashSet<>();

    public void compute(Blockchain blockchain) {
        //todo just calculate it from scratch when starting -> won't have problems because we are so small.
    }

    public static void add(Transaction transaction) {
        transaction.getOutputs().forEach(out -> unspentOutputs.add(out));
    }

    /**
     * gets the referenced TxOutput. Throws an error if the output is not in the UTXO-set.
     */
    public static TxOutput getUTXO(TxOutputPointer reference) throws MissingUTXOException {
        Optional<TxOutput> result = unspentOutputs
                .stream()
                .filter(utxo -> utxo.getParentTransaction().getTxId().equals(reference.getTransactionHash()))
                .filter(utxo -> utxo.getIndex() == reference.getOutputIndex())
                .findAny();

        if (result.isPresent()) {
            TxOutput utxo = result.get();
            unspentOutputs.remove(utxo);
            return utxo;
        }
        throw new MissingUTXOException();
    }
}
