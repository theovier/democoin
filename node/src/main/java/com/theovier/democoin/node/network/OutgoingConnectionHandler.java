package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.discovery.DefaultDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscoveryException;
import org.apache.log4j.Logger;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//todo return list of peers -> rename (not handler but connector), return the list so the node can handle the connections
/**
    tries to establish connection with known peers on node startup.
    1) connect with 1 known node
    2) query for more node addresses
 */
public class OutgoingConnectionHandler {

    private static final Logger LOG = Logger.getLogger(Node.class);

    private final PeerDiscovery peerDiscovery = new DefaultDiscovery();
    private List<Peer> outgoingConnections = new ArrayList<>(NetworkParams.MAX_OUT_CONNECTIONS);

    public void connectWithOtherNodes() throws PeerDiscoveryException {
        Peer otherNode = null;
        Set<InetSocketAddress> possiblePeerAddresses = peerDiscovery.getPeerAddresses();
        for (InetSocketAddress peerAddress : possiblePeerAddresses) {
            try {
                Socket socket = new Socket(peerAddress.getHostName(), peerAddress.getPort());
                otherNode = new Peer(socket);
                break;
            } catch (IOException e) {
                //pass, try other addresses
            }
        }
        if (otherNode == null) {
            //we couldn't connect to any known node :(
            throw new PeerDiscoveryException();
        }
        otherNode.start();
        outgoingConnections.add(otherNode);
    }

    private boolean hasNotReachedMaxConnections() {
        return outgoingConnections.size() < NetworkParams.MAX_OUT_CONNECTIONS;
    }
}
