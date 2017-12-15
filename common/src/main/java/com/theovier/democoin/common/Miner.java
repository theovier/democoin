package com.theovier.democoin.common;

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

    public Miner(final Blockchain blockchain, final Address payoutAddress) {
        this.blockchain = blockchain;
        this.payoutAddress = payoutAddress;
    }

    public void start() {
        LOG.info(String.format("start mining with %d threads", SLAVE_POOL_SIZE));
        startMiningSlaves();
    }

    public void stop() {
        LOG.info("stop mining.");
        LOG.info(blockchain);
        stopMiningSlaves();
    }

    private void startMiningSlaves() {
        for (int i = 0; i < SLAVE_POOL_SIZE; i++) {
            MiningSlave slave = new MiningSlave(blockchain, payoutAddress, this);
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
