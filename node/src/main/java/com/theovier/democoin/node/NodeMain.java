package com.theovier.democoin.node;

import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.common.Miner;
import com.theovier.democoin.node.network.Node;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.Security;

public class NodeMain {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NodeMain.class);

    public static void main(String[] args)  {
        Security.addProvider(new BouncyCastleProvider());
        Blockchain blockchain = Blockchain.loadFromDisc();
        Node node = new Node(blockchain);
        try {
            node.start();
        } catch (IOException e) {
            node.shutdown();
        }
    }
}
