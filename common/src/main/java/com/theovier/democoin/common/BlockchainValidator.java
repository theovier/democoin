package com.theovier.democoin.common;

/**
 * validates a whole blockchain recursively.
 */
public class BlockchainValidator {

    public static boolean isValid(final Blockchain blockchain) {
        while (blockchain.getBlocks().size() > 1) {
            Block lastBlock = blockchain.getBlocks().pop();
            if (!BlockValidator.isValid(lastBlock, blockchain)) {
                return false;
            }
        }
        return true;
    }
}
