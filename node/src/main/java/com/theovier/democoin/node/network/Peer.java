package com.theovier.democoin.node.network;

import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.node.network.messages.*;
import com.theovier.democoin.node.network.messages.Requests.*;
import com.theovier.democoin.node.network.messages.Responses.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer implements Runnable {

    private static final Logger LOG = Logger.getLogger(Peer.class);
    private boolean isRunning;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private final NetworkConnection connection;
    private final PeerObserver observer;
    private final Blockchain blockchain;
    private final List<FutureResponse> pendingRequests = new ArrayList<>();

    public Peer(final Socket socket, final PeerObserver observer, final Blockchain blockchain) throws IOException {
        this.connection = new NetworkConnection(socket);
        this.observer = observer;
        this.blockchain = blockchain;
    }

    public void start() {
        isRunning = true;
        observer.onPeerConnectionEstablished(this);
        executor.execute(this);
    }

    public void disconnect() {
        isRunning = false;
        connection.close();
        observer.onPeerConnectionClosed(this);
        executor.shutdown();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("peer" + connection);
        try {
            while (isRunning) {
                Message msg = connection.readMessage();
                LOG.debug(String.format("received msg <%s> by peer %s", msg, toString()));
                msg.handle(this);
            }
        } catch (IOException e) {
            LOG.debug(e);
            disconnect();
        }
    }

    //todo rename
    public void receivedResponse(Response response) {
        synchronized (pendingRequests) {
            for (FutureResponse sentRequests : pendingRequests) {
                if (sentRequests.requestID().equals(response.getRequestID())) {
                    sentRequests.setResult(response);
                    break;
                }
            }
        }
    }

    public void sendMessage(Message msg) throws IOException {
        connection.sendMessage(msg);
        LOG.debug(String.format("sent msg <%s> to peer %s", msg, toString()));
    }

    public InetSocketAddress getRemoteAddress() {
        return connection.getRemoteAddress();
    }

    //known addresses from the node which controls this peer. ugly.
    public List<InetSocketAddress> getKnownAddressesFromNode() {
        return observer.getConnectedAddresses();
    }

    public final Blockchain getBlockchain() {
        return blockchain;
    }

    public Pong requestPong() throws IOException, InterruptedException  {
        Request ping = new Ping();
        FutureResponse futureResponse = new FutureResponse(ping);
        pendingRequests.add(futureResponse);
        sendMessage(ping);
        return (Pong)futureResponse.get(); //blocking
    }

    public List<InetSocketAddress> requestAddresses() throws IOException, InterruptedException {
        Request addressRequest = new AddressRequest();
        FutureResponse futureResponse = new FutureResponse(addressRequest);
        pendingRequests.add(futureResponse);
        sendMessage(addressRequest);
        AddressResponse response = (AddressResponse) futureResponse.get();
        return response.getAddresses();
    }

    public long requestBlockchainHeight() throws IOException, InterruptedException {
        Request heightRequest = new BlockchainHeightRequest();
        FutureResponse futureResponse = new FutureResponse(heightRequest);
        pendingRequests.add(futureResponse);
        sendMessage(heightRequest);
        BlockchainHeightResponse response = (BlockchainHeightResponse) futureResponse.get();
        return response.getHeight();
    }

    public Blockchain requestBlockchain() throws IOException, InterruptedException {
        Request request = new BlockchainRequest();
        FutureResponse futureResponse = new FutureResponse(request);
        pendingRequests.add(futureResponse);
        sendMessage(request);
        BlockchainResponse response = (BlockchainResponse) futureResponse.get();
        return response.getBlockchain();
    }


    @Override
    public String toString() {
        return connection.toString();
    }
}
