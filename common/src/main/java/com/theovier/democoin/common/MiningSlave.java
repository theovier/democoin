package com.theovier.democoin.common;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class MiningSlave implements Runnable {

    private AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Address payoutAddress;
    private final Blockchain blockchain;

    //todo remove this. just for testing
    private final Miner master;

    public MiningSlave(final Blockchain blockchain, final Address payoutAddress, final Miner master) {
        this.blockchain = blockchain;
        this.payoutAddress = payoutAddress;
        this.master = master;
    }

    public void stop() {
        isRunning.set(false);
    }

    @Override
    public void run() {
        while (isRunning.get()) {
            Block block = mineBlock();
            if (block != null) {
                if (blockchain.append(block)) {
                    master.stop(); //todo remove this. just for testing.
                    //blockchain.save();
                    //todo broadcast
                }
            }
        }
    }

    private Block mineBlock() {
        long nonce = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        while (isRunning.get()) {
            //todo get Transactions from TransactionPool
            Block candidate = new Block(blockchain.getLastBlock(),  nonce, payoutAddress);
            //just check the pow here, so we don't have to falsely claim a block valid
            if (BlockValidator.hasValidProofOfWork(candidate)) {
                return candidate;
            }
            nonce = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        }
        return null;
    }
}
