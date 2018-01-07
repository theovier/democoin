package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.messages.Message;

import java.util.UUID;

public abstract class Response extends Message {

    private static final long serialVersionUID = 8784916174047247235L;
    private final UUID requestID;

    Response(final UUID requestID) {
        this.requestID = requestID;
    }

    public UUID getRequestID() {
        return requestID;
    }

}
