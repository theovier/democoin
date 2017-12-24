package com.theovier.democoin.node.network.messages;

import com.theovier.democoin.node.network.NetworkParams;

public class VersionMessage extends Message {

    public VersionMessage() {

    }

    public VersionMessage(int bestHeight) {
        //todo
    }

    public int getVersion() {
        return NetworkParams.PROTOCOL_VERSION;
    }
}
