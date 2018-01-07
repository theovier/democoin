package com.theovier.democoin.node;

import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.node.network.Node;

import java.io.IOException;

public class NodeMain {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NodeMain.class);

    public static void main(String[] args)  {

        Blockchain blockchain = Blockchain.load();
        Node node = new Node(blockchain);
        try {
            node.start();
        } catch (IOException e) {
            node.shutdown();
        }
    }
}
