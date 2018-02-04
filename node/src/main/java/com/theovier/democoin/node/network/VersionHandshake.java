package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.messages.Notifications.VersionNotification;
import com.theovier.democoin.node.network.messages.Requests.VersionRequest;
import com.theovier.democoin.node.network.messages.Responses.VersionResponse;

import java.io.IOException;

/**
 Client - (connect) -> Node
 Client <- (VersionRequest) - Node
 Client - (VersionResponse) -> Node
 Client <- (VersionAcceptanceNotification | VersionRejectionNotification) - Node
 */
public class VersionHandshake implements Handshake {

    @Override
    public void initiateHandshake(Peer initiator) throws HandshakeFailedException {
        try {
            initiator.sendMessage(new VersionRequest());
            VersionResponse response = (VersionResponse) initiator.readMessage();
            response.handle(initiator);
        } catch (IOException | ClassCastException e) {
            throw new HandshakeFailedException(e);
        }
    }

    @Override
    public void answerHandshake(Peer receiver) throws HandshakeFailedException {
        try {
            VersionRequest versionRequest = (VersionRequest) receiver.readMessage();
            versionRequest.handle(receiver);
            VersionNotification response = (VersionNotification) receiver.readMessage();
            response.handle(receiver);
        } catch (IOException | ClassCastException e) {
            throw new HandshakeFailedException(e);
        }
    }

}
