package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Message;

import java.io.IOException;
import java.util.UUID;

public abstract class Response extends Message {

    private static final long serialVersionUID = 8784916174047247235L;
    private final UUID requestID;

    Response(final UUID requestID) {
        this.requestID = requestID;
    }

    @Override
    public void handle(Peer receiver) throws IOException {
        receiver.onResponseReceived(this);
    }

    public UUID getRequestID() {
        return requestID;
    }

}
