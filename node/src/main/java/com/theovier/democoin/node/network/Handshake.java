package com.theovier.democoin.node.network;

public interface Handshake {
    void initiateHandshake(Peer initiator) throws HandshakeFailedException;
    void answerHandshake(Peer receiver) throws HandshakeFailedException;
}