package com.theovier.democoin.common;

import com.theovier.democoin.common.transaction.CoinbaseTransaction;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Miner {

    private static final Logger LOG = Logger.getLogger(Miner.class);
    private static final int SLAVE_POOL_SIZE = 10;
    private static final int SLAVE_TERMINATION_TIMEOUT = 10;

    private List<MiningSlave> slaves = new ArrayList<>(SLAVE_POOL_SIZE);
    private BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
            .namingPattern("mining-slave-%d")
            .build();
    private ExecutorService executor= Executors.newFixedThreadPool(SLAVE_POOL_SIZE, threadFactory);
    private final Address payoutAddress;
    private final Blockchain blockchain;
    private final String coinbaseMsg;

    public Miner(final Blockchain blockchain, final Address payoutAddress) {
        this.blockchain = blockchain;
        this.payoutAddress = payoutAddress;
        this.coinbaseMsg = CoinbaseTransaction.COINBASE_MSG;
    }

    public Miner(final Blockchain blockchain, final Address payoutAddress, final String optionalCoinbaseMsg) {
        this.blockchain = blockchain;
        this.payoutAddress = payoutAddress;
        this.coinbaseMsg = optionalCoinbaseMsg;
    }

    public void start() {
        LOG.info(String.format("start mining with %d threads", SLAVE_POOL_SIZE));
        startMiningSlaves();
    }

    public synchronized void stop() {
        LOG.info("stop mining.");
        blockchain.saveToDisc(); //todo remove. just for testing
        stopMiningSlaves();
    }

    private void startMiningSlaves() {
        for (int i = 0; i < SLAVE_POOL_SIZE; i++) {
            MiningSlave slave = new MiningSlave(blockchain, payoutAddress, coinbaseMsg, this);
            slaves.add(slave);
            executor.execute(slave);
        }
    }

    private void stopMiningSlaves() {
        for (int i = 0; i < SLAVE_POOL_SIZE; i++) {
           slaves.get(i).stop();
        }
        try {
            executor.shutdown();
            executor.awaitTermination(SLAVE_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
    }
}
