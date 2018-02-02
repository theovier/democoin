package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Notifications.TransactionNotification;
import com.theovier.democoin.node.network.messages.Responses.TransactionBroadcastResponse;

import java.io.IOException;

/**same as TransactionNotification but expects a response (boolean) if the tx was accepted */
public class TransactionBroadcastRequest extends Request {

    private static final long serialVersionUID = -4282872889787657273L;
    private final Transaction tx;

    public TransactionBroadcastRequest(final Transaction tx) {
        super();
        this.tx = tx;
    }

    @Override
    public void handle(Peer receiver) throws IOException {
        Blockchain blockchain = receiver.getBlockchain();
        boolean accepted = blockchain.addToMemPool(tx);
        if (accepted) {
            receiver.broadcast(new TransactionNotification(tx));
        }
        receiver.sendMessage(new TransactionBroadcastResponse(this.getID(), accepted));
    }

    @Override
    public String toString() {
        return "TransactionBroadcastRequest";
    }
}
