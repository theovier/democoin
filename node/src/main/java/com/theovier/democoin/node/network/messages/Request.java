package com.theovier.democoin.node.network.messages;

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
