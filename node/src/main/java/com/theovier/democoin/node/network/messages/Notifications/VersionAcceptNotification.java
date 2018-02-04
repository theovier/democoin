package com.theovier.democoin.node.network.messages.Notifications;

import com.theovier.democoin.node.network.Peer;


public class VersionAcceptNotification extends VersionNotification {


    private static final long serialVersionUID = -8593518502062736862L;

    @Override
    public void handle(Peer receiver) {
        receiver.start();
    }

    @Override
    public String toString() {
        return "VersionAcceptNotification";
    }
}
