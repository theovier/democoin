package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.messages.IMessage;
import com.theovier.democoin.node.network.messages.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.Socket;

public class NetworkConnection {

    private static final Logger LOG = Logger.getLogger(NetworkConnection.class);
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final InetAddress remoteAddress;

    public NetworkConnection(final Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.remoteAddress = socket.getInetAddress();
    }

    public IMessage readMessage() throws IOException {
        try {
            return (IMessage) in.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new ProtocolException();
        }
    }

    public void sendMessage(final IMessage message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
            out.flush();
        }
    }

    public synchronized void close() {
        try {
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public String toString() {
        return  "[" + remoteAddress + ":" +
                NetworkParams.PORT +
                ']';
    }
}
