package com.theovier.democoin.node;

import com.theovier.democoin.node.network.Node;
import com.theovier.democoin.node.network.messages.Ping;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class NodeMain {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NodeMain.class);

    public static void main(String[] args) throws Exception {
        Node node = new Node();
        node.start();

        Socket socket = new Socket("192.168.1.48", 7777);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(new Ping());
    }
}
