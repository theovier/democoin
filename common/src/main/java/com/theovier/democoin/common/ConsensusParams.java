package com.theovier.democoin.common;

public abstract class ConsensusParams {

    //address the genesis transaction pays to.
    public static final Address GENESIS_ADDRESS = new Address("1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB");

    //approximation of the coin maximum.
    public static final long MAX_COINS = 21_000_000; //todo change.

    //this plus the txFees are rewarded to the miner of a block.
    public static final int COINBASE_REWARD = 100;

    //number of blocks a coinbaseTx must wait to be spend.
    public static final int COINBASE_MATURITY = 1;

    //includes the coinbaseTx.
    public static final int MAX_TRANSACTIONS_PER_BLOCK = 5;

    //seconds until target is adjusted (3600s = 1h)
    public static final int TARGET_TIMESPAN = 60 * 60;

    //how far are blocks apart in seconds (360s = 6min)
    public static final int TARGET_SPACING = 6 * 60;

    //amount of blocks till target adjustment
    public static final int DIFFICULTY_ADJUSTMENT_INTERVAL = TARGET_TIMESPAN / TARGET_SPACING;

    //protect target from too much volatility
    public static final int TIMESPAN_LIMIT_FACTOR = 4;

    //lowest possible adjustment of the needed time
    public static final int MIN_TIMESPAN_ADJUSTMENT = TARGET_TIMESPAN / TIMESPAN_LIMIT_FACTOR;

    //highest possible adjustment of the needed time
    public static final int MAX_TIMESPAN_ADJUSTMENT = TARGET_TIMESPAN * TIMESPAN_LIMIT_FACTOR;

    //min difficulty for pow. Lowest difficulty = highest possible target
    public static final String MIN_DIFFICULTY = "000000F000000000000000000000000000000000000000000000000000000000";
    public static final String HIGHEST_POSSIBLE_TARGET = MIN_DIFFICULTY;

    //max difficulty for pow. Highest difficulty = lowest possible target
    public static final String MAX_DIFFICULTY = "000000000000000000000000000000000000F000000000000000000000000000";
    public static final String LOWEST_POSSIBLE_TARGET = MAX_DIFFICULTY;
}
