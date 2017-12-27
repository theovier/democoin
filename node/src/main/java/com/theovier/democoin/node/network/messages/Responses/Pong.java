package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.messages.Response;

import java.util.UUID;

public class Pong extends Response {

    public Pong(UUID requestID) {
        super(requestID);
    }

    @Override
    public String toString() {
        return "pong";
    }
}
