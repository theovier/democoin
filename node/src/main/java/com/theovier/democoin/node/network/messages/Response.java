package com.theovier.democoin.node.network.messages;

public class Response extends Message {

    private final String requestID;

    public Response(final String requestID) {
        this.requestID = requestID;
    }

    public String getRequestID() {
        return requestID;
    }

}
