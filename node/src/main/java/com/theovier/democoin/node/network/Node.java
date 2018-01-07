package com.theovier.democoin.node.network;

import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.node.network.discovery.DefaultDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscoveryException;
import com.theovier.democoin.node.network.messages.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node implements PeerObserver {

    private static final Logger LOG = Logger.getLogger(Node.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final PeerDiscovery peerDiscovery;
    private final NetworkListener networkListener;
    private final List<Peer> connections = new ArrayList<>(NetworkParams.MAX_CONNECTIONS);
    private final Blockchain blockchain;

    public Node(final Blockchain blockchain) {
        this.blockchain = blockchain;
        this.peerDiscovery = new DefaultDiscovery(this, blockchain);
        this.networkListener = new NetworkListener(this, blockchain);
    }

    public void start() throws IOException {
        connectToOtherPeers();
        downloadMostRecentBlockchain();
        startListening();
        LOG.info("outgoing connection count: " + connections.size());
    }

    public void shutdown() {
        stopListening();
        connections.forEach(Peer::disconnect);
    }

    private void connectToOtherPeers() {
        connectToDefaultPeers();
        discoverAndConnectToNewPeers();
    }

    private void connectToDefaultPeers() {
        try {
            LOG.info("trying to connect to default peers...");
            peerDiscovery.connectToDefaultPeers(NetworkParams.MAX_OUT_CONNECTIONS);
        } catch (PeerDiscoveryException e) {
            //pass
            LOG.warn("could not connect to any known host. seems we are the first one.");
        }
    }

    private void discoverAndConnectToNewPeers() {
        int freeSlots = NetworkParams.MAX_OUT_CONNECTIONS - connections.size();
        try {
            LOG.info("trying to discover new peers by querying default peers");
            List<Peer> discovered = peerDiscovery.discoverAndConnect(connections, freeSlots);
            LOG.info(String.format("discovered %d new peers and connected to them", discovered.size()));
        } catch (PeerDiscoveryException e) {
            LOG.warn("could not connect to any newly discovered hosts");
        }
    }

    private void downloadMostRecentBlockchain() {
        for (Peer peer : connections) {
            try {
                long receivedHeight = peer.requestBlockchainHeight();
                LOG.info(receivedHeight);
                if (blockchain.getHeight() < receivedHeight) {
                    Blockchain remoteBlockchain = peer.requestBlockchain();
                    boolean substituted = blockchain.substitute(remoteBlockchain);
                    if (substituted) {
                        LOG.info("SUBSTITUTED");
                        break;
                    }
                }
            } catch (IOException e) {
                peer.disconnect();
            } catch (InterruptedException e) {
                LOG.error(e);
            }
        }
    }

    private void startListening() throws IOException {
        networkListener.startAcceptingConnections();
        executor.execute(networkListener);
    }

    private void stopListening() {
        networkListener.stop();
        executor.shutdown();
    }

    public void broadcastMessage(Message msg) {
        for (Peer peer : connections) {
            try {
                peer.sendMessage(msg);
            } catch (IOException e) {
                LOG.error(e);
                //remove peer if unable to receive messages.
            }
        }
    }

    @Override
    public boolean isAcceptingConnections() {
        return connections.size() < NetworkParams.MAX_CONNECTIONS;
    }

    @Override
    public void onPeerConnectionEstablished(Peer peer) {
        synchronized (connections) {
            if (!connections.contains(peer)) {
                connections.add(peer);
                LOG.info("connection established " + peer);
            }
        }
    }

    @Override
    public void onPeerConnectionClosed(Peer peer) {
        synchronized (connections) {
            connections.remove(peer);
            LOG.info("connection closed " + peer);
        }
    }

    @Override
    public List<InetSocketAddress> getConnectedAddresses() {
        List<InetSocketAddress> knownAddresses = new ArrayList<>(connections.size());
        connections.forEach(peer -> knownAddresses.add(peer.getRemoteAddress()));
        return knownAddresses;
    }
}

