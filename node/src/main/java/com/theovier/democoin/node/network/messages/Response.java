package com.theovier.democoin.node.network.messages;

import java.util.UUID;

public abstract class Response extends Message {

    private final UUID requestID;

    public Response(final UUID requestID) {
        this.requestID = requestID;
    }

    public UUID getRequestID() {
        return requestID;
    }

}
