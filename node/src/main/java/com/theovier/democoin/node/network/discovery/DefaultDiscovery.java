package com.theovier.democoin.node.network.discovery;


import com.theovier.democoin.node.network.NetworkParams;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class DefaultDiscovery implements PeerDiscovery {

    private Set<InetSocketAddress> addresses = new HashSet<>();

    @Override
    public Set<InetSocketAddress> getPeerAddresses() {
        for (String defaultHost : NetworkParams.DEFAULT_HOSTS) {
            InetSocketAddress address = new InetSocketAddress(defaultHost, NetworkParams.PORT);
            addresses.add(address);
        }
        return addresses;
    }
}
