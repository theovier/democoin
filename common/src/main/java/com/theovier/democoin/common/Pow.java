package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;

public class Pow {

    //todo instead of blockchain use prevBlock variable in block, to iterate in time?
    //returns how many zeros a blockHash must start with
    public static int getNextWorkRequired(final Blockchain blockchain) {
        Block latestBlock = blockchain.getLastBlock();
        long latestBlockIndex = latestBlock.getIndex();

        if ((latestBlockIndex+1) % Config.DIFFICULTY_ADJUSTMENT_INTERVAL != 0) {
            return latestBlock.getTargetZeros(); //requires same work as the previous block
        }

        //genesis block
        if (latestBlockIndex == 0) {
            return Config.MIN_DIFFICULTY;
        }

        //get the block before the last adjustment
        long lastAdjustmentBlockIndex = latestBlockIndex - Config.DIFFICULTY_ADJUSTMENT_INTERVAL - 1;
        assert(lastAdjustmentBlockIndex >= 0);
        Block lastBlockBeforeAdjustment = blockchain.get((int)lastAdjustmentBlockIndex);
        assert(lastBlockBeforeAdjustment != null);
        return calculateNextWorkRequired(lastBlockBeforeAdjustment, latestBlock);
    }

    private static int calculateNextWorkRequired(final Block firstBlockInDifficulty, final Block lastBlockInDifficulty) {
        int actualTimeSpan = (int) (lastBlockInDifficulty.getTimestamp() - firstBlockInDifficulty.getTimestamp());

        //limit adjustment
        if (actualTimeSpan < Config.TARGET_TIMESPAN / 4) {
            actualTimeSpan = Config.TARGET_TIMESPAN / 4;
        }
        if (actualTimeSpan > Config.TARGET_TIMESPAN * 4) {
            actualTimeSpan = Config.TARGET_TIMESPAN * 4;
        }

        //retarget (higher target = higher difficulty, while in bitcoin lower target = higher difficulty)
        int newTarget = (lastBlockInDifficulty.getTargetZeros() * Config.TARGET_TIMESPAN) / actualTimeSpan;

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
