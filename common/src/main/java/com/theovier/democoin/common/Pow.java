package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;

import java.math.BigInteger;

public class Pow {

    /**
     * returns the targetHex which a blockHash has to beat (be lower)
     */
    public static String getNextWorkRequired(final Blockchain blockchain) {
        Block lastBlock = blockchain.getLastBlock();
        long currentIndex = lastBlock.getIndex();

        if ((currentIndex + 1) % Config.DIFFICULTY_ADJUSTMENT_INTERVAL != 0) {
            return lastBlock.getPowTarget();
        }

        //first target adjustment
        if (currentIndex + 1 == Config.DIFFICULTY_ADJUSTMENT_INTERVAL) {
            return calculateNextWorkRequired(blockchain.getGenesisBlock(), lastBlock);
        } else {
            //further target adjustments. e.g. (29 + 1) - 10 = 20
            //look how long we did take from the creation of 20 to creation of 29.
            long lastAdjustedBlockIndex = (currentIndex + 1) - Config.DIFFICULTY_ADJUSTMENT_INTERVAL;
            Block lastAdjustedBlock = blockchain.get((int) lastAdjustedBlockIndex);
            return calculateNextWorkRequired(lastAdjustedBlock, lastBlock);
        }
    }

    private static String calculateNextWorkRequired(final Block firstBlockInDifficulty, final Block lastBlockInDifficulty) {
        long timeNeeded = lastBlockInDifficulty.getTimestamp() - firstBlockInDifficulty.getTimestamp();
        long limitedTimeNeeded = Utils.clamp(timeNeeded, Config.MIN_TIMESPAN_ADJUSTMENT, Config.MAX_TIMESPAN_ADJUSTMENT);
        BigInteger adjustedTarget = calculateAdjustedTarget(lastBlockInDifficulty.getPowTarget(), limitedTimeNeeded, Config.TARGET_TIMESPAN);
        String adjustedTargetHex = convertTargetToHex(adjustedTarget);
        return Utils.clampHex(adjustedTargetHex, Config.LOWEST_POSSIBLE_TARGET, Config.HIGHEST_POSSIBLE_TARGET);
    }

    //adjustedTarget = (currentTarget * neededTime) / intendedTime
    private static BigInteger calculateAdjustedTarget(String currentTargetHex, long neededTime, long intendedTime) {
        BigInteger currentTarget = new BigInteger(currentTargetHex, 16);
        BigInteger timeNeeded = BigInteger.valueOf(neededTime);
        BigInteger timeIntended = BigInteger.valueOf(intendedTime);
        return currentTarget.multiply(timeNeeded).divide(timeIntended);
    }

    private static String convertTargetToHex(BigInteger target) {
        String adjustedTargetHex = target.toString(16);
        return Utils.fillTo64CharsWithLeadingZeros(adjustedTargetHex);
    }

    public static boolean checkProofOfWork(final Sha256Hash hash, String requiredTarget) {
        return hash.toString().compareTo(requiredTarget) <= 0;
    }

}
