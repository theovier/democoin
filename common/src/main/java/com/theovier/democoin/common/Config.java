package com.theovier.democoin.common;

public abstract class Config {

    public static final String BLOCKCHAIN_FILE = "blockchain.dat";
    public static final Address GENESIS_ADDRESS = new Address("1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB");

    public static final long MAX_COINS = 21_000_000; //todo change.
    public static final int COINBASE_REWARD = 100;
    public static final int COINBASE_MATURITY = 1;
    public static final int MAX_TRANSACTIONS_PER_BLOCK = 5;



}
