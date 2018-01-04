package com.theovier.democoin.node.network.discovery;


import com.theovier.democoin.node.network.NetworkParams;
import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.PeerObserver;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultDiscovery implements PeerDiscovery {

    private static final Logger LOG = Logger.getLogger(DefaultDiscovery.class);
    private final List<InetSocketAddress> defaultHostAddresses;
    private final PeerObserver observer;

    public DefaultDiscovery(final PeerObserver observer) {
        this.observer = observer;
        defaultHostAddresses = getDefaultHostAddresses();
    }

    private List<InetSocketAddress> getDefaultHostAddresses() {
        List<InetSocketAddress> addresses = new ArrayList<>();
        for (String defaultHost : NetworkParams.DEFAULT_HOSTS) {
            InetSocketAddress address = new InetSocketAddress(defaultHost, NetworkParams.PORT);
            addresses.add(address);
        }
        return addresses;
    }

    @Override
    public List<Peer> connectToDefaultPeers(final int maxConnections) throws PeerDiscoveryException {
        return connectToPeersFromAddresses(defaultHostAddresses, maxConnections);
    }

    @Override
    public Peer connectToRandomDefaultPeer() throws PeerDiscoveryException {
        return connectToRandomPeer(defaultHostAddresses);
    }

    @Override
    public List<Peer> discoverAndConnect(final List<Peer> seed, final int maxConnections) throws PeerDiscoveryException {
        List<InetSocketAddress> alreadyKnown = new ArrayList<>();
        List<InetSocketAddress> discovered = new ArrayList<>();
        for (Peer peer : seed) {
            alreadyKnown.add(peer.getRemoteAddress());
            discovered.addAll(peer.getKnownAddressesFromNode());
        }

        discovered = discovered.stream()
                .filter(discoveredAddress -> !alreadyKnown.contains(discoveredAddress))
                .limit(maxConnections)
                .collect(Collectors.toList());

        return connectToPeersFromAddresses(discovered, maxConnections);
    }

    private List<Peer> connectToPeersFromAddresses(List<InetSocketAddress> addresses, final int maxConnections) throws PeerDiscoveryException {
        List<Peer> peers = new ArrayList<>(maxConnections);
        for (InetSocketAddress address : addresses) {
            try {
                Peer peer = connectToPeer(address);
                peers.add(peer);
            } catch (IOException e) {
                LOG.error("could not connect to " + address);
            }
            if (peers.size() == maxConnections) {
                return peers;
            }
        }
        if (peers.isEmpty()) {
            throw new PeerDiscoveryException();
        }
        return peers;
    }

    private Peer connectToPeer(InetSocketAddress address) throws IOException {
        Socket socket = new Socket(address.getHostName(), address.getPort());
        Peer peer = new Peer(socket, observer);
        peer.start();
        return peer;
    }

    private Peer connectToRandomPeer(List<InetSocketAddress> addresses) throws PeerDiscoveryException {
        Collections.shuffle(addresses);
        for (InetSocketAddress randomAddress : addresses) {
            try {
                return connectToPeer(randomAddress);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        throw new PeerDiscoveryException();
    }
}
