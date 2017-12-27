package com.theovier.democoin.node.network.messages;

import com.theovier.democoin.node.network.Peer;

import java.io.IOException;

public interface IMessage {
    void handle(Peer receiver) throws IOException;
}
