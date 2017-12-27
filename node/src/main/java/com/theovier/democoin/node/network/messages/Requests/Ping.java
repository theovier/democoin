package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.messages.Request;

public class Ping extends Request {

    @Override
    public String toString() {
        return "ping";
    }
}
