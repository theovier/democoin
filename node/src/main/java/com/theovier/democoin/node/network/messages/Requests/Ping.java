package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.IMessage;
import com.theovier.democoin.node.network.messages.Request;
import com.theovier.democoin.node.network.messages.Responses.Pong;

import java.io.IOException;

public class Ping extends Request implements IMessage {

    @Override
    public String toString() {
        return "ping";
    }

    @Override
    public void handle(Peer receiver) throws IOException {
        receiver.sendMessage(new Pong(getID()));
    }
}
