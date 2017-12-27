package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.discovery.DefaultDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscoveryException;
import com.theovier.democoin.node.network.messages.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Node {

    private static final Logger LOG = Logger.getLogger(Node.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final NetworkListener networkListener = new NetworkListener();
    private final PeerDiscovery peerDiscovery = new DefaultDiscovery();
    private List<Peer> outgoingConnections = new ArrayList<>(NetworkParams.MAX_OUT_CONNECTIONS); //todo should manage all peers here. even the incomings

    public void start() throws IOException {
        connectToOtherPeers();
        downloadMostRecentBlockchain();
        startListening();
        LOG.info("outgoing connection count: " + outgoingConnections.size());
    }

    public void shutdown() {
        stopListening();
    }

    private void connectToOtherPeers() {
        connectToDefaultPeers();
        discoverAndConnectToNewPeers();
    }

    private void connectToDefaultPeers() {
        try {
            outgoingConnections = peerDiscovery.connectToDefaultPeers(NetworkParams.MAX_OUT_CONNECTIONS);
        } catch (PeerDiscoveryException e) {
            //pass
            LOG.warn("could not connect to any known host. seems we are the first one.", e);
        }
    }

    private void discoverAndConnectToNewPeers() {
        int freeSlots = NetworkParams.MAX_OUT_CONNECTIONS - outgoingConnections.size();
        try {
            outgoingConnections.addAll(
                    peerDiscovery.discoverAndConnect(outgoingConnections, freeSlots)
            );
        } catch (PeerDiscoveryException e) {
            //pass
            LOG.warn("could not connect to any known host. seems we are the first one.", e);
        }
    }

    private void downloadMostRecentBlockchain() {

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
        for (Peer peer : outgoingConnections) {
            try {
                peer.sendMessage(msg);
            } catch (IOException e) {
                LOG.error(e);
                //remove peer if unable to receive messages.
            }
        }
    }

}

