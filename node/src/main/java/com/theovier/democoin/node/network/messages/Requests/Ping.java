package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.messages.Message;

public class Ping extends Message {

    @Override
    public String toString() {
        return "ping";
    }
}
