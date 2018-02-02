package com.theovier.democoin.node.network.messages.Responses;

import java.util.UUID;

public class TransactionBroadcastResponse extends Response {

    private static final long serialVersionUID = -1565395342413255153L;
    private final boolean isValidTx;

    public TransactionBroadcastResponse(final UUID requestID, final boolean isValidTx) {
        super(requestID);
        this.isValidTx = isValidTx;
    }

    public boolean isValidTx() {
        return isValidTx;
    }

    @Override
    public String toString() {
        return "TransactionBroadcastResponse";
    }
}
