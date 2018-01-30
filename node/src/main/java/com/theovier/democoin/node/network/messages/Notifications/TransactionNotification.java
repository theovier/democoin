package com.theovier.democoin.node.network.messages.Notifications;

import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.node.network.Peer;


public class TransactionNotification extends Notification {

    private static final long serialVersionUID = 500891143907505394L;
    private final Transaction tx;

    public TransactionNotification(final Transaction tx) {
        this.tx = tx;
    }

    @Override
    public void handle(Peer receiver) {
        Blockchain blockchain = receiver.getBlockchain();
        if (blockchain.addToMemPool(tx)) {
            receiver.broadcast(this);
        }
    }

    @Override
    public String toString() {
        return "TransactionNotification";
    }
}
