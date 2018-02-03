package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.NetworkParams;
import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Responses.VersionResponse;

import java.io.IOException;

public class VersionRequest extends Request {

    private static final long serialVersionUID = -5372915632591935549L;

    @Override
    public void handle(Peer receiver) throws IOException {
        receiver.sendMessage(new VersionResponse(this.getID(), NetworkParams.PROTOCOL_VERSION));
    }

    @Override
    public String toString() {
        return "VersionRequest";
    }
}
