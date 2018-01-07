package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.messages.Message;

import java.util.UUID;

public abstract class Request extends Message {

    private final UUID ID;

    public Request() {
        this.ID = UUID.randomUUID();
    }

    public UUID getID() {
        return ID;
    }

}
