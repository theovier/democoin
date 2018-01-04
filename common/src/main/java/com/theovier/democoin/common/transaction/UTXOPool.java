package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Block;
import com.theovier.democoin.common.Blockchain;
import org.apache.log4j.Logger;

import java.util.*;

public class UTXOPool {

    private static final Logger LOG = Logger.getLogger(UTXOPool.class);
    private static Set<TxOutput> unspentOutputs = new HashSet<>();

    public static void compute(Blockchain blockchain) {
        //todo make this better and faster.

        HashMap<TxOutputPointer, TxOutput> outputs = new HashMap<>();
        List<Block> blocks = blockchain.getBlocks();
        for (Block block : blocks) {
            for (Transaction transaction : block.getTransactions()) {
                for (TxOutput output : transaction.getOutputs()) {
                    TxOutputPointer pointer = new TxOutputPointer(output);
                    outputs.put(pointer, output);
                }
                for (TxInput input : transaction.getInputs()) {
                    TxOutputPointer pointer = input.getPrevOutputInfo();
                    outputs.remove(pointer);
                }
            }
        }
        for (TxOutputPointer key : outputs.keySet()) {
            LOG.info(key);
        }
        unspentOutputs.addAll(outputs.values());
    }

    public static synchronized void add(Transaction transaction) {
        unspentOutputs.addAll(transaction.getOutputs());
    }

    /**
     * gets the referenced TxOutput. Throws an error if the output is not in the UTXO-set.
     */
    public static synchronized TxOutput getUTXO(TxOutputPointer reference) throws MissingUTXOException {
        Optional<TxOutput> result = unspentOutputs
                .stream()
                .filter(utxo -> utxo.getParentTransaction().getTxId().equals(reference.getTransactionHash()))
                .filter(utxo -> utxo.getIndex() == reference.getOutputIndex())
                .findAny();

        if (result.isPresent()) {
            return result.get();
        }
        throw new MissingUTXOException(reference);
    }

    public static synchronized TxOutput removeUTXO(TxOutputPointer reference) throws MissingUTXOException {
        TxOutput utxo = getUTXO(reference);
        unspentOutputs.remove(utxo);
        return utxo;
    }

    public static synchronized TxOutput removeUTXO(TxInput input) throws MissingUTXOException {
        return removeUTXO(input.getPrevOutputInfo());
    }
}
