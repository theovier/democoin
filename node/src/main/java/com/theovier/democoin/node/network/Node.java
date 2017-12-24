package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.messages.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//todo initial connect to other nodes and query their lists.
public class Node implements Runnable {

    private static final Logger LOG = Logger.getLogger(Node.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isRunning;
    private ServerSocket serverSocket;
    private List<Peer> connectedPeers = new ArrayList<>(NetworkParams.MAX_CONNECTIONS);

    public void start() throws IOException {
        serverSocket = new ServerSocket(NetworkParams.PORT);
        isRunning = true;
        executor.execute(this);
    }

    public void shutdown() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOG.error(e);
        }
        executor.shutdown();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("node");
        LOG.info("Node running on Port " + serverSocket.getLocalPort());
        while (isRunning && hasNotReachedMaxConnectionsYet()) {
            handleIncomingConnections();
        }
        LOG.info("Node stopped on Port " + serverSocket.getLocalPort());
    }

    private boolean hasNotReachedMaxConnectionsYet() {
        return connectedPeers.size() < NetworkParams.MAX_CONNECTIONS;
    }

    private void handleIncomingConnections() {
        try {
            Socket socket = serverSocket.accept(); //blocking
            socket.setSoTimeout(0);

            //todo extract me
            Peer peer = new Peer(socket);
            peer.start();
            connectedPeers.add(peer);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public void broadcastMessage(Message msg) {
        for (Peer peer : connectedPeers) {
            try {
                peer.sendMessage(msg);
            } catch (IOException e) {
                LOG.error(e);
                //remove peer if unable to receive messages.
            }
        }
    }
}
