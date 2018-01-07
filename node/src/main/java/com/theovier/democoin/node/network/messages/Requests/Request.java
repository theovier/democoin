package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.messages.Message;

import java.util.UUID;

public abstract class Request extends Message {

    private static final long serialVersionUID = -4231416944257709858L;
    private final UUID ID;

    Request() {
        this.ID = UUID.randomUUID();
    }

    public UUID getID() {
        return ID;
    }

}
