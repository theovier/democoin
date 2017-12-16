package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import org.apache.log4j.Logger;

public class Pow {

    private static final Logger LOG = Logger.getLogger(Pow.class);

    //todo instead of blockchain use prevBlock variable in block, to iterate in time?
    public static int getNextWorkRequired(final Blockchain blockchain) {
        Block latestBlock = blockchain.getLastBlock();
        long latestBlockIndex = latestBlock.getIndex();

        if ((latestBlockIndex+1) % Config.DIFFICULTY_ADJUSTMENT_INTERVAL != 0) {
            return latestBlock.getPowTarget(); //requires same work as the previous block
        }

        //genesis block
        if (latestBlockIndex == 0) {
            return Config.MIN_DIFFICULTY;
        }

        //get the block before the last adjustment
        long lastAdjustmentBlockIndex = latestBlockIndex - Config.DIFFICULTY_ADJUSTMENT_INTERVAL - 1;

        if (lastAdjustmentBlockIndex < 0) {
            lastAdjustmentBlockIndex = 0;
        }
        Block lastBlockBeforeAdjustment = blockchain.get((int)lastAdjustmentBlockIndex);
        if (lastBlockBeforeAdjustment == null) {
            //throw error.
        }
        return calculateNextWorkRequired(lastBlockBeforeAdjustment, latestBlock);
    }

    private static int calculateNextWorkRequired(final Block firstBlockInDifficulty, final Block lastBlockInDifficulty) {
        int actualTimeSpan = (int) (lastBlockInDifficulty.getTimestamp() - firstBlockInDifficulty.getTimestamp());

        LOG.info(String.format("needed time: %d, expected time %d", actualTimeSpan, Config.TARGET_TIMESPAN));

        //limit adjustment
        if (actualTimeSpan < Config.TARGET_TIMESPAN / 4) {
            actualTimeSpan = Config.TARGET_TIMESPAN / 4;
        }
        if (actualTimeSpan > Config.TARGET_TIMESPAN * 4) {
            actualTimeSpan = Config.TARGET_TIMESPAN * 4;
        }

        //todo DO NOT MULTIPLY THE ZEROES! ITS GETTING EXPONENTIALLY HARDER FOR EACH 0.
        //retarget (higher target = higher difficulty, while in bitcoin lower target = higher difficulty)
        int newTarget = (lastBlockInDifficulty.getPowTarget() * Config.TARGET_TIMESPAN) / actualTimeSpan;

        if (newTarget > Config.MAX_DIFFICULTY) {
            newTarget = Config.MAX_DIFFICULTY;
        }
        if (newTarget < Config.MIN_DIFFICULTY) {
            newTarget = Config.MIN_DIFFICULTY;
        }
        return newTarget;
    }

    public static boolean checkProofOfWork(final Sha256Hash hash, int requiredTarget) {
        return getLeadingZerosCount(hash) >= requiredTarget;
    }

    public static int getLeadingZerosCount(final Sha256Hash blockhash) {
        String hashHex = blockhash.toString();
        for (int i = 0; i < hashHex.length(); i++) {
            if (hashHex.charAt(i) != '0') {
                return i;
            }
        }
        return hashHex.length();
    }
}
