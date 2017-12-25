package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.messages.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node {

    private static final Logger LOG = Logger.getLogger(Node.class);
    private final List<Peer> outgoingConnections = new ArrayList<>(NetworkParams.MAX_OUT_CONNECTIONS);
    private final NetworkListener networkListener = new NetworkListener();
    private final ExecutorService listenExecutor = Executors.newSingleThreadExecutor();
    private final OutgoingConnectionHandler outgoingConnectionHandler = new OutgoingConnectionHandler();

    public void start() throws IOException {
        //1) try to connect to a hardcoded node
        //2) get X other nodes to connect to
        //3) download most recent blockchain from random node
        //4) start listening for incoming connections
        startListening();
        outgoingConnectionHandler.connectWithOtherNodes();
    }

    public void shutdown() {
        stopListening();
    }

    private void startListening() throws IOException {
        networkListener.startAcceptingConnections();
        listenExecutor.execute(networkListener);
    }

    private void stopListening() {
        networkListener.stop();
        listenExecutor.shutdown();
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

