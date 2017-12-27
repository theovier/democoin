package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.messages.Message;

public class Pong extends Message {

    @Override
    public String toString() {
        return "pong";
    }
}
