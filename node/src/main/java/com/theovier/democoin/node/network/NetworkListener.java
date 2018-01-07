package com.theovier.democoin.node.network;

import org.apache.log4j.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
   listens for incoming connections and accepts them if possible.
 */
public class NetworkListener implements Runnable {

    private static final Logger LOG = Logger.getLogger(NetworkListener.class);

    private boolean isRunning;
    private ServerSocket serverSocket;
    private final PeerObserver observer;

    public NetworkListener(final PeerObserver observer) {
        this.observer = observer;
    }

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
        while (isRunning) {
            handleIncomingConnections();
        }
        LOG.info("stop listening for connections on port " + serverSocket.getLocalPort());
    }

    private void handleIncomingConnections() {
        try {
            Socket socket = serverSocket.accept(); //blocking
            if (observer.isAcceptingConnections()) {
                socket.setSoTimeout(0);
                Peer peer = new Peer(socket, observer);
                peer.start();
            } else {
                socket.close();
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
