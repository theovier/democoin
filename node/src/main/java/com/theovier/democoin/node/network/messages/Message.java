package com.theovier.democoin.node.network.messages;

import com.theovier.democoin.node.network.Peer;

import java.io.IOException;
import java.io.Serializable;

public abstract class Message implements Serializable {
    private static final long serialVersionUID = 799235619469481936L;

    public abstract void handle(Peer receiver) throws IOException;
}
