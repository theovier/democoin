package com.theovier.democoin.node.network.discovery;

import com.theovier.democoin.node.network.Peer;

import java.net.InetSocketAddress;
import java.util.Set;

public interface PeerDiscovery {
    Set<InetSocketAddress> getPeerAddresses();
    Set<Peer> getRandomPeers() throws PeerDiscoveryException;
}
