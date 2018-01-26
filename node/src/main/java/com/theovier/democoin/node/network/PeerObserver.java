package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.messages.Message;

import java.net.InetSocketAddress;
import java.util.List;

public interface PeerObserver {
    boolean isAcceptingConnections();
    void onPeerConnectionEstablished(Peer peer);
    void onPeerConnectionClosed(Peer peer);
    void broadcast(Message msg, Peer sender);
    List<InetSocketAddress> getConnectedAddresses();
}
