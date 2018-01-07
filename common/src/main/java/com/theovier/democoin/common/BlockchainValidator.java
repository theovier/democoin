package com.theovier.democoin.common;

import org.apache.log4j.Logger;

/**
 * validates a whole blockchain recursively.
 */
public class BlockchainValidator {

    private transient static final Logger LOG = Logger.getLogger(BlockchainValidator.class);

    public static boolean isValid(final Blockchain blockchain) {
        return false;
    }
}
