package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Block;
import com.theovier.democoin.common.Blockchain;
import org.apache.log4j.Logger;

import java.util.*;

public class UTXOPool {

    private static final Logger LOG = Logger.getLogger(UTXOPool.class);
    private Set<TxOutput> unspentOutputs = new HashSet<>();

    private final Blockchain blockchain;

    public UTXOPool(final Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public synchronized void compute() {
        unspentOutputs.clear();

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
            //LOG.info(key);
        }
        unspentOutputs.addAll(outputs.values());
    }

    public synchronized void add(Transaction transaction) {
        unspentOutputs.addAll(transaction.getOutputs());
    }

    /**
     * gets the referenced TxOutput. Throws an error if the output is not in the UTXO-set.
     */
    public synchronized TxOutput getUTXO(TxOutputPointer reference) throws MissingUTXOException {
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

    private synchronized TxOutput removeUTXO(TxOutputPointer reference) throws MissingUTXOException {
        TxOutput utxo = getUTXO(reference);
        unspentOutputs.remove(utxo);
        return utxo;
    }

    public synchronized TxOutput removeUTXO(TxInput input) throws MissingUTXOException {
        return removeUTXO(input.getPrevOutputInfo());
    }
}
