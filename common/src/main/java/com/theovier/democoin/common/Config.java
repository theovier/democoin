package com.theovier.democoin.common;

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

    //number of zeroes a blockHash must start with.
    public static final long DIFFICULTY = 6; //todo make it possible to adjust this value.
}
