package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.Peer;

import java.util.UUID;

public class Pong extends Response {

    private static final long serialVersionUID = -2187663355922038434L;

    public Pong(UUID requestID) {
        super(requestID);
    }

    @Override
    public String toString() {
        return "pong";
    }

    @Override
    public void handle(Peer receiver) {
        receiver.receivedResponse(this);
    }
}
