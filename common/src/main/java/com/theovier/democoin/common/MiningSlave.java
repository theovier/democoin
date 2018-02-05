package com.theovier.democoin.common;

import com.theovier.democoin.common.transaction.Transaction;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MiningSlave implements Runnable {

    private AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Address payoutAddress;
    private final Blockchain blockchain;
    private final String coinbaseMsg;
    private final BlockFoundListener minerMaster;

    MiningSlave(final Blockchain blockchain, final Address payoutAddress, final String coinbaseMsg, final BlockFoundListener minerMaster) {
        this.blockchain = blockchain;
        this.payoutAddress = payoutAddress;
        this.coinbaseMsg = coinbaseMsg;
        this.minerMaster = minerMaster;
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
                    minerMaster.onBlockFound(block);
                    blockchain.saveToDisc();
                }
            }
        }
    }

    private Block mineBlock() {
        long nonce = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        while (isRunning.get()) {

            String difficultyTarget = Pow.getNextWorkRequired(blockchain);

            Set<Transaction> transactions = blockchain.getMemPool().getPendingTransactions()
                    .stream()
                    .limit(ConsensusParams.MAX_TRANSACTIONS_PER_BLOCK)
                    .collect(Collectors.toSet());

            Block candidate = new Block(blockchain.getLastBlock(),  nonce, difficultyTarget, payoutAddress, coinbaseMsg, transactions);

            //just precheck the pow here, so we don't have to falsely claim a block valid
            if (Pow.checkProofOfWork(candidate.getHash(), candidate.getPowTarget())) {
                return candidate;
            }
            nonce = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        }
        return null;
    }
}
