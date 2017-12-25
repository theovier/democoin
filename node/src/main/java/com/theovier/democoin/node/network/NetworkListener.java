package com.theovier.democoin.node.network;

import org.apache.log4j.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//todo ping peers every X minutes to check if they are still there. If not close and remove.
/**
   listens for incoming connections and accepts them if possible.
   connections are added to the peerList which the Node maintains.
 */
public class NetworkListener implements Runnable {

    private static final Logger LOG = Logger.getLogger(Node.class);

    private boolean isRunning;
    private ServerSocket serverSocket;
    private List<Peer> acceptedPeers = new ArrayList<>(NetworkParams.MAX_IN_CONNECTIONS);

    public void startAcceptingConnections() throws IOException {
        isRunning = true;
        this.serverSocket = new ServerSocket(NetworkParams.PORT);
    }

    public void stop() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("listener");
        LOG.info("start listening for connections on port " + serverSocket.getLocalPort());
        while (isRunning && hasNotReachedMaxConnections()) {
            handleIncomingConnections();
        }
        LOG.info("stop listening for connections on port " + serverSocket.getLocalPort());
    }

    private boolean hasNotReachedMaxConnections() {
        return acceptedPeers.size() < NetworkParams.MAX_IN_CONNECTIONS;
    }

    private void handleIncomingConnections() {
        try {
            Socket socket = serverSocket.accept(); //blocking
            socket.setSoTimeout(0);

            //todo extract me
            Peer peer = new Peer(socket);
            peer.start();

            acceptedPeers.add(peer);
            LOG.info("connection accepted: " + peer);
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
