package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Responses.Pong;

import java.io.IOException;

public class Ping extends Request {

    private static final long serialVersionUID = -654467057962772754L;

    @Override
    public String toString() {
        return "ping";
    }

    @Override
    public void handle(Peer receiver) throws IOException {
        receiver.sendMessage(new Pong(getID()));
    }
}
