package com.theovier.democoin.common;

//todo rename ConsensusParams?
public abstract class Config {

    public static final String BLOCKCHAIN_FILE = "blockchain.dat";

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

    //min difficulty for pow
    public static final String MIN_DIFFICULTY = "00000F0000000000000000000000000000000000000000000000000000000000";

    //max difficulty for pow
    public static final String MAX_DIFFICULTY = "00000000000000000000000000000000F0000000000000000000000000000000";

}
