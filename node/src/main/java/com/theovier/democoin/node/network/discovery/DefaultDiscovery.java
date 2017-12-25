package com.theovier.democoin.node.network.discovery;


import com.theovier.democoin.node.network.NetworkParams;
import com.theovier.democoin.node.network.Peer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 try to establish connection with known peers on node startup.
 1) connect with 1 known node
 2) query for more node addresses
 */
public class DefaultDiscovery implements PeerDiscovery {

    private static final Logger LOG = Logger.getLogger(DefaultDiscovery.class);

    @Override
    public Set<InetSocketAddress> getPeerAddresses() {
        Set<InetSocketAddress> addresses = new HashSet<>();
        for (String defaultHost : NetworkParams.DEFAULT_HOSTS) {
            InetSocketAddress address = new InetSocketAddress(defaultHost, NetworkParams.PORT);
            addresses.add(address);
        }
        return addresses;
    }

    @Override
    public Set<Peer> getRandomPeers() throws PeerDiscoveryException {
        Set<Peer> defaultPeers = getDefaultPeers();
        Set<Peer> randomPeers = defaultPeers;
        randomPeers.forEach(Peer::start);
        //todo query defaultPeers for more addresses
        //todo connect to several of those addresses
        return randomPeers;
    }

    public Set<Peer> getDefaultPeers() throws PeerDiscoveryException {
        Set<Peer> defaultPeers = new HashSet<>();
        for (InetSocketAddress address : getPeerAddresses()) {
            try {
                Socket socket = new Socket(address.getHostName(), address.getPort());
                defaultPeers.add(new Peer(socket)); //don't forget to start the peer later.
            } catch (IOException e) {
                LOG.error("could not connect to " + address, e);
            }
        }
        if (defaultPeers.isEmpty()) {
            throw new PeerDiscoveryException();
        }
        return defaultPeers;
    }
}
