package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.discovery.DefaultDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscovery;
import com.theovier.democoin.node.network.messages.Message;
import com.theovier.democoin.node.network.messages.Responses.Pong;
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
    private List<Peer> outgoingConnections = new ArrayList<>(NetworkParams.MAX_OUT_CONNECTIONS);

    public void start() throws IOException {
        //1) try to connect to a hardcoded node
        //2) get X other nodes to connect to
        //3) download most recent blockchain from random node
        //4) start listening for incoming connections
        startListening();
        outgoingConnections = peerDiscovery.getRandomPeers()
                        .stream()
                        .limit(NetworkParams.MAX_OUT_CONNECTIONS)
                        .collect(Collectors.toList());
        LOG.info(outgoingConnections.size());


        Peer myself = outgoingConnections.get(0);
        Pong pong = myself.ping();
        LOG.info(pong.toString());
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

