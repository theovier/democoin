package com.theovier.democoin.node.network.discovery;

import java.net.InetSocketAddress;
import java.util.Set;

public interface PeerDiscovery {
    Set<InetSocketAddress> getPeerAddresses();
}
