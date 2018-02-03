package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.NetworkParams;
import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Notifications.VersionAcceptNotification;
import com.theovier.democoin.node.network.messages.Notifications.VersionRejectNotification;

import java.io.IOException;
import java.util.UUID;

public class VersionResponse extends Response {

    private static final long serialVersionUID = -3070628136755848015L;
    private final int version;

    public VersionResponse(final UUID requestID, final int version) {
        super(requestID);
        this.version = version;
    }

    @Override
    public void handle(Peer receiver) throws IOException  {
        super.handle(receiver);
        if (version == NetworkParams.PROTOCOL_VERSION) {
            receiver.sendMessage(new VersionAcceptNotification());
        } else {
            receiver.sendMessage(new VersionRejectNotification(NetworkParams.PROTOCOL_VERSION));
            receiver.disconnect();
        }
    }

    @Override
    public String toString() {
        return "VersionResponse";
    }
}
