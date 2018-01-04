package com.theovier.democoin.node.network;

import java.net.InetSocketAddress;
import java.util.List;

public interface PeerObserver {
    boolean isAcceptingConnections();
    void onPeerConnectionEstablished(Peer peer);
    void onPeerConnectionClosed(Peer peer);
    List<InetSocketAddress> getConnectedAddresses();
}
