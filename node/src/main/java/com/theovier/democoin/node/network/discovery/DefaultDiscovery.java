package com.theovier.democoin.node.network.discovery;


import com.theovier.democoin.node.network.NetworkParams;
import com.theovier.democoin.node.network.Peer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * todo do this in a new thread?
 try to establish connection with known peers on node startup.
 1) connect with known nodes
 2) query for more node addresses
 */
public class DefaultDiscovery implements PeerDiscovery {

    private static final Logger LOG = Logger.getLogger(DefaultDiscovery.class);

    @Override
    public List<InetSocketAddress> getPeerAddresses() {
        List<InetSocketAddress> addresses = new ArrayList<>();
        for (String defaultHost : NetworkParams.DEFAULT_HOSTS) {
            InetSocketAddress address = new InetSocketAddress(defaultHost, NetworkParams.PORT);
            addresses.add(address);
        }
        return addresses;
    }

    @Override
    /**
     * 1) connect to 1 random default node (x)
     * 2) query this node for new addresses
     * 3) connect to the newly discovered addresses
     */
    public List<Peer> getRandomPeers() throws PeerDiscoveryException {
        List<InetSocketAddress> defaultAddresses = getPeerAddresses();
        //Peer randomDefaultPeer = getRandom(defaultAddresses);
        //List<InetSocketAddress> knownAddresses = randomDefaultPeer.requestAddresses();
        return getPeersFromAddresses(defaultAddresses);
    }

    private Peer getRandom(List<InetSocketAddress> addresses) throws PeerDiscoveryException {
        Collections.shuffle(addresses);
        for (InetSocketAddress randomAddress : addresses) {
            try {
                return getPeerFromAddress(randomAddress);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        throw new PeerDiscoveryException();
    }

    private Peer getPeerFromAddress(InetSocketAddress address) throws IOException {
        Socket socket = new Socket(address.getHostName(), address.getPort());
        Peer peer = new Peer(socket);
        peer.start();
        return peer;
    }

    public List<Peer> getKnownPeersFromSeeds(List<Peer> seeds) throws PeerDiscoveryException {
        List<InetSocketAddress> discovered = new ArrayList<>();
        for (Peer seed : seeds) {
            List<InetSocketAddress> knownAddresses = seed.requestAddresses();
            discovered.addAll(knownAddresses);
        }
        return getPeersFromAddresses(discovered);
    }


    private List<Peer> getPeersFromAddresses(List<InetSocketAddress> addresses) throws PeerDiscoveryException {
        List<Peer> peers = new ArrayList<>();
        for (InetSocketAddress address : addresses) {
            try {
                Peer peer = getPeerFromAddress(address);
                peers.add(peer);
            } catch (IOException e) {
                LOG.error("could not connect to " + address, e);
            }
        }
        if (peers.isEmpty()) {
            throw new PeerDiscoveryException();
        }
        return peers;
    }
}
