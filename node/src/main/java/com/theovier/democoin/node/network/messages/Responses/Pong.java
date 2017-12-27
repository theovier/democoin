package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Response;

import java.io.IOException;
import java.util.UUID;

public class Pong extends Response {

    public Pong(UUID requestID) {
        super(requestID);
    }

    @Override
    public String toString() {
        return "pong";
    }

    @Override
    public void handle(Peer receiver) throws IOException {
        receiver.receivedResponse(this);
    }
}
