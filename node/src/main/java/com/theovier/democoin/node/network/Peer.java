package com.theovier.democoin.node.network;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.node.network.messages.*;
import com.theovier.democoin.node.network.messages.Notifications.VersionNotification;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Peer implements Runnable {

    private static final Logger LOG = Logger.getLogger(Peer.class);
    private boolean isRunning = true;
    private final boolean isLightweight;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ExecutorService messageHandler = Executors.newCachedThreadPool();
    private final NetworkConnection connection;
    private final PeerObserver observer;
    private final Blockchain blockchain;
    private final List<FutureResponse> pendingRequests = new ArrayList<>();

    public Peer(final Socket socket, final PeerObserver observer, final Blockchain blockchain) throws IOException {
        this.connection = new NetworkConnection(socket);
        this.observer = observer;
        this.blockchain = blockchain;
        this.isLightweight = false;
    }

    public Peer(final Socket socket, final PeerObserver observer, final Blockchain blockchain, final boolean isLightweight) throws IOException {
        this.connection = new NetworkConnection(socket);
        this.observer = observer;
        this.blockchain = blockchain;
        this.isLightweight = isLightweight;
    }

    /** calley by a node after accepting a connection request */
    public void startHandshake(long timeout, TimeUnit unit) throws HandshakeFailedException {
        LOG.info(String.format("%s wants to connect. Starting handshake.", toString()));
        ExecutorService handshakeExecutor = Executors.newSingleThreadExecutor();
        SimpleTimeLimiter timeLimiter = SimpleTimeLimiter.create(handshakeExecutor);
        Handshake versionHandshake = timeLimiter.newProxy(new VersionHandshake(), Handshake.class, timeout, unit);
        try {
            versionHandshake.initiateHandshake(this);
        } catch (HandshakeFailedException e) {
            disconnect();
            throw new HandshakeFailedException(String.format("decline connection request for peer %s.", toString()));
        } catch (UncheckedTimeoutException e) {
            disconnect();
            throw new HandshakeTimeoutException(toString());
        } finally {
            handshakeExecutor.shutdown();
        }
    }

    /** called by a node after requesting a connection with another node*/
    public void answerHandshake(long timeout, TimeUnit unit) throws HandshakeFailedException {
        LOG.info(String.format("trying to connect to %s. Waiting for handshake...", toString()));
        ExecutorService handshakeExecutor = Executors.newSingleThreadExecutor();
        SimpleTimeLimiter timeLimiter = SimpleTimeLimiter.create(handshakeExecutor);
        Handshake versionHandshake = timeLimiter.newProxy(new VersionHandshake(), Handshake.class, timeout, unit);
        try {
            versionHandshake.answerHandshake(this);
        } catch (HandshakeFailedException e) {
            disconnect();
            throw new HandshakeFailedException(e);
        } catch (UncheckedTimeoutException e) {
            disconnect();
            throw new HandshakeTimeoutException(toString());
        } finally {
            handshakeExecutor.shutdown();
        }
    }

    /** called by the handshake messages */
    public void start() {
        observer.onPeerConnectionEstablished(this);
        executor.execute(this);
    }

    public void disconnect() {
        if (isRunning) {
            isRunning = false;
            connection.close();
            observer.onPeerConnectionClosed(this);
            executor.shutdown();
            messageHandler.shutdown();
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("peer" + connection);
        try {
            while (isRunning) {
                Message msg = readMessage();
                messageHandler.execute(new MessageDispatcher(msg, this));
            }
        } catch (IOException e) {
            disconnect();
        }
    }

    /** blocking */
    Message readMessage() throws IOException {
        return connection.readMessage();
    }

    public void onResponseReceived(Response response) {
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
    }

    public InetSocketAddress getRemoteAddress() {
        return connection.getRemoteAddress();
    }

    public List<InetSocketAddress> getKnownAddressesFromNode() {
        return observer.getConnectedAddresses();
    }

    public final Blockchain getBlockchain() {
        return blockchain;
    }

    public final boolean isLightweight() {
        return isLightweight;
    }

    public void broadcast(final Message message) {
        observer.broadcast(message, this);
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

    public long requestBlockchainHeight(long timeout, TimeUnit unit) {
        Request heightRequest = new BlockchainHeightRequest();
        FutureResponse futureResponse = new FutureResponse(heightRequest);
        pendingRequests.add(futureResponse);
        try {
            sendMessage(heightRequest);
            BlockchainHeightResponse response = (BlockchainHeightResponse) futureResponse.get(timeout, unit);
            return response.getHeight();
        } catch (IOException | InterruptedException | TimeoutException e) {
            LOG.error(e);
            pendingRequests.remove(futureResponse);
            return 0;
        }
    }

    public Blockchain requestBlockchain() throws IOException, InterruptedException {
        Request request = new BlockchainRequest();
        FutureResponse futureResponse = new FutureResponse(request);
        pendingRequests.add(futureResponse);
        sendMessage(request);
        BlockchainResponse response = (BlockchainResponse) futureResponse.get();
        return response.getBlockchain();
    }
    
    public Blockchain requestBlockchain(long timeout, TimeUnit unit) {
        Request request = new BlockchainRequest();
        FutureResponse futureResponse = new FutureResponse(request);
        pendingRequests.add(futureResponse);
        try {
            sendMessage(request);
            BlockchainResponse response = (BlockchainResponse) futureResponse.get(timeout, unit);
            return response.getBlockchain();
        } catch (IOException | InterruptedException | TimeoutException e) {
            LOG.error(e);
            pendingRequests.remove(futureResponse);
            return new Blockchain();
        }
    }

    public boolean requestTransactionBroadcast(final Transaction tx) throws IOException, InterruptedException {
        Request request = new TransactionBroadcastRequest(tx);
        FutureResponse futureResponse = new FutureResponse(request);
        pendingRequests.add(futureResponse);
        sendMessage(request);
        TransactionBroadcastResponse response = (TransactionBroadcastResponse) futureResponse.get();
        return response.isValidTx();
    }


    @Override
    public String toString() {
        return connection.toString();
    }
}
