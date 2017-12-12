package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Block;
import com.theovier.democoin.common.Blockchain;
import org.apache.log4j.Logger;

import java.util.*;

//todo don't make this static?
public class UTXOPool {

    private static final Logger LOG = Logger.getLogger(UTXOPool.class);
    private static Set<TxOutput> unspentOutputs = new HashSet<>();

    public static void compute(Blockchain blockchain) {
        //todo just calculate it from scratch when starting -> won't have problems because we are so small.
        //todo make this better and faster.

        HashMap<TxOutputPointer, TxOutput> outputs = new HashMap<>();
        ArrayList<Block> blocks = blockchain.getBlocks();
        for (Block block : blocks) {
            for (Transaction transaction : block.getTransactions()) {
                for (TxOutput output : transaction.getOutputs()) {
                    TxOutputPointer pointer = new TxOutputPointer(output.getParentTransaction().getTxId(), output.getIndex()); //todo move this to TxOutput
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

    public static void add(Transaction transaction) {
        transaction.getOutputs().forEach(unspentOutputs::add);
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
