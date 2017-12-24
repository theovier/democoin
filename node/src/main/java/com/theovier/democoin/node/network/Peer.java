package com.theovier.democoin.node.network;

import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.node.network.messages.Message;
import com.theovier.democoin.node.network.messages.Ping;
import com.theovier.democoin.node.network.messages.Pong;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer implements Runnable {

    private static final Logger LOG = Logger.getLogger(Peer.class);
    private boolean isRunning;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private final NetworkConnection connection;
    private final Blockchain blockchain;

    public Peer(final NetworkConnection connection) {
        this.connection = connection;
        this.blockchain = null;
    }

    public Peer(final Socket socket) throws IOException {
        this.connection = new NetworkConnection(socket);
        this.blockchain = null;
    }

    public Peer(final NetworkConnection connection, final Blockchain blockchain) {
        this.connection = connection;
        this.blockchain = blockchain;
    }

    public void start() {
        isRunning = true;
        executor.execute(this);
    }

    public void disconnect() {
        isRunning = false;
        connection.close();
        executor.shutdown();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("peer" + connection);
        try {
            while (isRunning) {
                Message msg = connection.readMessage();
                processMessage(msg);
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    private void processMessage(Message msg) throws IOException {
        if (msg instanceof Ping) {
            connection.sendMessage(new Pong());
        }
        String info = String.format("Peer %s received msg: '%s'", connection, msg);
        LOG.info(info);
    }

    public void sendMessage(Message msg) throws IOException {
        connection.sendMessage(msg);
    }
}
