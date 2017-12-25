package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.discovery.DefaultDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscovery;
import com.theovier.democoin.node.network.messages.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Node {

    private static final Logger LOG = Logger.getLogger(Node.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final NetworkListener networkListener = new NetworkListener();
    private final PeerDiscovery peerDiscovery = new DefaultDiscovery();
    private final Set<Peer> outgoingConnections = new HashSet<>(NetworkParams.MAX_OUT_CONNECTIONS);

    public void start() throws IOException {
        //1) try to connect to a hardcoded node
        //2) get X other nodes to connect to
        //3) download most recent blockchain from random node
        //4) start listening for incoming connections
        startListening();
        outgoingConnections.addAll(
                peerDiscovery.getRandomPeers()
                        .stream()
                        .limit(NetworkParams.MAX_OUT_CONNECTIONS)
                        .collect(Collectors.toSet())
        );
        LOG.info(outgoingConnections.size());
    }

    public void shutdown() {
        stopListening();
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

