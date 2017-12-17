package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;

import java.math.BigInteger;

public class Pow {

    public static String getNextWorkRequired(final Blockchain blockchain) {
        Block latestBlock = blockchain.getLastBlock();
        long latestBlockIndex = latestBlock.getIndex();

        //to check the validity of the genesis block //todo remove this and solve this otherwise.
        if (latestBlockIndex == 0) {
            return Config.MIN_DIFFICULTY;
        }

        if ((latestBlockIndex + 1) % Config.DIFFICULTY_ADJUSTMENT_INTERVAL != 0) {
            return latestBlock.getPowTarget();
        }

        //get the block before the last adjustment
        long lastAdjustmentBlockIndex = latestBlockIndex - Config.DIFFICULTY_ADJUSTMENT_INTERVAL - 1;
        if (lastAdjustmentBlockIndex < 0) {
            lastAdjustmentBlockIndex = 0;
        }
        Block lastBlockBeforeAdjustment = blockchain.get((int)lastAdjustmentBlockIndex);
        if (lastBlockBeforeAdjustment == null) {
            throw new IllegalStateException();
        }
        return calculateNextWorkRequired(lastBlockBeforeAdjustment, latestBlock);
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
