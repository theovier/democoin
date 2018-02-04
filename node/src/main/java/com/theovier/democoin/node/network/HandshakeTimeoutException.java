package com.theovier.democoin.node.network;

public class HandshakeTimeoutException extends HandshakeFailedException {

    public HandshakeTimeoutException(String peerName) {
        super("timeout occurred for " + peerName);
    }
}
