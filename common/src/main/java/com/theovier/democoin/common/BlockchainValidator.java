package com.theovier.democoin.common;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * validates an entire blockchain recursively.
 */
public class BlockchainValidator implements Validator<Blockchain> {

    private transient static final Logger LOG = Logger.getLogger(BlockchainValidator.class);

    public boolean isValid(final Blockchain blockchain) {
        Blockchain stepByStepCopy = new Blockchain();
        List<Block> blocks = blockchain.getBlocks();

        for (int i = 1; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (!stepByStepCopy.append(block)) {
                return false;
            }
        }
        return true;
    }
}
