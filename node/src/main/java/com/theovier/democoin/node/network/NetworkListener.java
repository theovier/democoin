package com.theovier.democoin.node.network;

import com.theovier.democoin.common.Blockchain;
import org.apache.log4j.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static com.theovier.democoin.node.network.NetworkParams.HANDSHAKE_TIMEOUT;
import static com.theovier.democoin.node.network.NetworkParams.HANDSHAKE_TIMEUNIT;

/**
   listens for incoming connections and accepts them if possible.
 */
public class NetworkListener implements Runnable {

    private static final Logger LOG = Logger.getLogger(NetworkListener.class);

    private boolean isRunning;
    private ServerSocket serverSocket;
    private final PeerObserver observer;
    private final Blockchain blockchain;

    NetworkListener(final PeerObserver observer, final Blockchain blockchain) {
        this.observer = observer;
        this.blockchain = blockchain;
    }

    public void startAcceptingConnections() throws IOException {
        isRunning = true;
        serverSocket = new ServerSocket(NetworkParams.PORT);
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
                Peer peer = new Peer(socket, observer, blockchain);
                peer.startHandshake(HANDSHAKE_TIMEOUT, HANDSHAKE_TIMEUNIT);
            } else {
                socket.close();
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
