package com.theovier.democoin.node.network.discovery;

import com.theovier.democoin.node.network.Peer;

import java.util.List;

public interface PeerDiscovery {
    List<Peer> connectToDefaultPeers(final int maxConnections) throws PeerDiscoveryException;
    List<Peer> discoverAndConnect(final List<Peer> seed, final int maxConnections) throws PeerDiscoveryException;
}
