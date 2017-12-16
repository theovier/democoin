package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import org.apache.log4j.Logger;

import java.math.BigInteger;

public class Pow {

    private static final Logger LOG = Logger.getLogger(Pow.class);

    //todo instead of blockchain use prevBlock variable in block, to iterate in time?
    public static String getNextWorkRequired(final Blockchain blockchain) {
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

    private static String calculateNextWorkRequired(final Block firstBlockInDifficulty, final Block lastBlockInDifficulty) {
        int actualTimeSpan = (int) (lastBlockInDifficulty.getTimestamp() - firstBlockInDifficulty.getTimestamp());

        //LOG.debug(String.format("needed time: %d, expected time %d", actualTimeSpan, Config.TARGET_TIMESPAN));

        /*
        //limit adjustment
        if (actualTimeSpan < Config.TARGET_TIMESPAN / 4) {
            actualTimeSpan = Config.TARGET_TIMESPAN / 4;
        }
        if (actualTimeSpan > Config.TARGET_TIMESPAN * 4) {
            actualTimeSpan = Config.TARGET_TIMESPAN * 4;
        }
        */

        //retarget
        BigInteger currentTarget = new BigInteger(lastBlockInDifficulty.getPowTarget(), 16);
        BigInteger newTarget = currentTarget.multiply(BigInteger.valueOf(actualTimeSpan));
        newTarget = newTarget.divide(BigInteger.valueOf(Config.TARGET_TIMESPAN)); //= (lastBlockInDifficulty.getPowTarget() * actualTimeSpan) / Config.TARGET_TIMESPAN;

        String newTargetHex = newTarget.toString(16);
        newTargetHex = Utils.fillTo64ByteWithLeadingZeros(newTargetHex);

        //todo check is!
        if (newTargetHex.compareTo(Config.MAX_DIFFICULTY) < 0) {
            newTargetHex = Config.MAX_DIFFICULTY;
        }
        if (newTargetHex.compareTo(Config.MIN_DIFFICULTY) > 0) {
            newTargetHex = Config.MIN_DIFFICULTY;
        }

        return newTargetHex;
    }

    public static boolean checkProofOfWork(final Sha256Hash hash, String requiredTarget) {
        return hash.toString().compareTo(requiredTarget) <= 0;
    }
}
