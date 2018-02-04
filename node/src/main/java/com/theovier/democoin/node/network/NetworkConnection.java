package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.messages.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Socket;

public class NetworkConnection {

    private static final Logger LOG = Logger.getLogger(NetworkConnection.class);
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final InetSocketAddress remoteAddress;

    NetworkConnection(final Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.remoteAddress = new InetSocketAddress(socket.getInetAddress(), NetworkParams.PORT);
    }

    public Message readMessage() throws IOException {
        try {
            Message msg =  (Message) in.readObject();
            LOG.debug(String.format("received msg <%s> by peer %s", msg, toString()));
            return msg;
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new ProtocolException();
        }
    }

    public void sendMessage(final Message message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
            out.flush();
            LOG.debug(String.format("sent msg <%s> to peer %s", message, toString()));
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

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public String toString() {
        return  "[" + remoteAddress + ']';
    }
}
