package com.theovier.democoin.node.network;

public interface PeerObserver {
    boolean isAcceptingConnections();
    void onPeerConnectionEstablished(Peer peer);
    void onPeerConnectionClosed(Peer peer);
}
