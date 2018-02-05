package com.theovier.democoin.node.network;

import com.theovier.democoin.common.*;
import com.theovier.democoin.node.network.discovery.DefaultDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscovery;
import com.theovier.democoin.node.network.discovery.PeerDiscoveryException;
import com.theovier.democoin.node.network.messages.Message;
import com.theovier.democoin.node.network.messages.Notifications.BlockFoundNotification;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Node implements PeerObserver, BlockFoundListener {

    private static final Logger LOG = Logger.getLogger(Node.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final PeerDiscovery peerDiscovery;
    private final NetworkListener networkListener;
    private final List<Peer> connections = new ArrayList<>(NetworkParams.MAX_CONNECTIONS);
    private final Blockchain blockchain = Blockchain.loadFromDisc();
    private final Miner miner;

    public Node() {
        this.peerDiscovery = new DefaultDiscovery(this, blockchain);
        this.networkListener = new NetworkListener(this, blockchain);
        this.miner = new Miner(blockchain, ConsensusParams.GENESIS_ADDRESS);
    }

    public Node(final Address payoutAddress) {
        this.peerDiscovery = new DefaultDiscovery(this, blockchain);
        this.networkListener = new NetworkListener(this, blockchain);
        this.miner = new Miner(blockchain, payoutAddress);
    }

    public Node(final Address payoutAddress, final String optionalCoinbaseMsg) {
        this.peerDiscovery = new DefaultDiscovery(this, blockchain);
        this.networkListener = new NetworkListener(this, blockchain);
        this.miner = new Miner(blockchain, payoutAddress, optionalCoinbaseMsg);
    }

    public void start() throws IOException {
        connectToOtherPeers();
        downloadMostRecentBlockchain();
        startListening();
        startMining();
    }

    public void shutdown() {
        stopListening();
        connections.forEach(Peer::disconnect);
    }

    private void connectToOtherPeers() {
        connectToDefaultPeers();
        if (!connections.isEmpty()) {
            discoverAndConnectToNewPeers();
        }
    }

    private void connectToDefaultPeers() {
        try {
            LOG.info("trying to connect to default peers...");
            peerDiscovery.connectToDefaultPeers(NetworkParams.MAX_OUT_CONNECTIONS);
        } catch (PeerDiscoveryException e) {
            LOG.warn("could not connect to any known host. seems we are the first one.");
        }
    }

    private void discoverAndConnectToNewPeers() {
        int freeSlots = NetworkParams.MAX_OUT_CONNECTIONS - connections.size();
        try {
            LOG.info("trying to discover new peers by querying default peers");
            List<Peer> discovered = peerDiscovery.discoverAndConnect(connections, freeSlots);
            LOG.info(String.format("discovered %d new peers and connected to them", discovered.size()));
        } catch (PeerDiscoveryException e) {
            LOG.warn("could not connect to any newly discovered hosts");
        }
    }

    private void downloadMostRecentBlockchain() {
        InitialBlockchainDownloader.downloadLongestBlockchain(blockchain, connections, 10, TimeUnit.SECONDS);
        broadcast(new BlockFoundNotification(blockchain.getLastBlock())); //try to spread the longest blockchains
    }

    private void startListening() throws IOException {
        networkListener.startAcceptingConnections();
        executor.execute(networkListener);
    }

    private void stopListening() {
        networkListener.stop();
        executor.shutdown();
    }

    private void startMining() {
        miner.registerBlockFoundListener(this);
        miner.start();
    }

    private void broadcast(Message msg) {
        for (Peer peer : connections) {
            try {
                peer.sendMessage(msg);
            } catch (IOException e) {
                peer.disconnect();
            }
        }
    }

    @Override
    public boolean isAcceptingConnections() {
        return connections.size() < NetworkParams.MAX_CONNECTIONS;
    }

    @Override
    public void onPeerConnectionEstablished(Peer peer) {
        synchronized (connections) {
            if (!connections.contains(peer)) {
                connections.add(peer);
                LOG.info("connection established " + peer);
            }
        }
    }

    @Override
    public void onPeerConnectionClosed(Peer peer) {
        synchronized (connections) {
            connections.remove(peer);
            LOG.info("connection closed " + peer);
        }
    }

    @Override
    public List<InetSocketAddress> getConnectedAddresses() {
        List<InetSocketAddress> knownAddresses = new ArrayList<>(connections.size());
        connections.forEach(peer -> knownAddresses.add(peer.getRemoteAddress()));
        return knownAddresses;
    }

    @Override
    public void broadcast(Message msg, Peer sender) {
        for (Peer peer : connections) {
            if (peer.equals(sender)) {
                continue;
            }
            try {
                peer.sendMessage(msg);
            } catch (IOException e) {
                peer.disconnect();
            }
        }
    }

    @Override
    public void onBlockFound(Block block) {
        LOG.info(String.format("new block found (height: %d, %s)", block.getIndex() + 1, block.getHash()));
        broadcast(new BlockFoundNotification(block));
    }
}

