package com.theovier.democoin.node.network.discovery;

import com.theovier.democoin.node.network.Peer;

import java.net.InetSocketAddress;
import java.util.List;

public interface PeerDiscovery {
    List<InetSocketAddress> getPeerAddresses();
    List<Peer> getRandomPeers() throws PeerDiscoveryException;
}
