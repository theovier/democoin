package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.messages.*;
import com.theovier.democoin.node.network.messages.Requests.AddressRequest;
import com.theovier.democoin.node.network.messages.Request;
import com.theovier.democoin.node.network.messages.Requests.Ping;
import com.theovier.democoin.node.network.messages.Responses.AddressResponse;
import com.theovier.democoin.node.network.messages.Responses.Pong;
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
    //private final Blockchain blockchain;
    private final List<FutureResponse> pendingRequests = new ArrayList<>();

    public Peer(final Socket socket, final PeerObserver observer) throws IOException {
        this.connection = new NetworkConnection(socket);
        this.observer = observer;
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
                processMessage(msg);
            }
        } catch (IOException e) {
            LOG.error(e);
            disconnect();
        }
    }

    private void processMessage(Message msg) throws IOException {
        if (msg instanceof Request) {
            processRequests((Request) msg);
        }

        if (msg instanceof Response) {
            processResponse((Response) msg);
        }

        LOG.info(String.format("received msg <%s>", msg));
    }

    private void processResponse(Response response) {
        synchronized (pendingRequests) {
            for (FutureResponse sentRequests : pendingRequests) {
                if (sentRequests.requestID().equals(response.getRequestID())) {
                    sentRequests.setResult(response);
                }
            }
        }
    }

    private void processRequests(Request request) throws IOException {
        Response response; //let the caller cast the response, he knows what he requested.
        if (request instanceof AddressRequest) {
            response = new AddressResponse(request.getID());
        } else {
            response = new Pong(request.getID());
        }
        sendMessage(response);
    }


    public void sendMessage(Message msg) throws IOException {
        connection.sendMessage(msg);
    }

    public Pong ping() throws IOException, InterruptedException  {
        Ping ping = new Ping();
        FutureResponse futureResponse = new FutureResponse(ping);
        pendingRequests.add(futureResponse);
        sendMessage(ping);
        return (Pong)futureResponse.get(); //blocking
    }

    public List<InetSocketAddress> requestAddresses() {
        //AddressRequest addressRequest = new AddressRequest();
        //FutureResponse futureResponse = new FutureResponse(addressRequest);
        //AddressResponse response = futureResponse.get();
        //sendMessage(addressRequest);

        //just for testing
        List<InetSocketAddress> addresses = new ArrayList<>();
        addresses.add(new InetSocketAddress("192.168.1.48", NetworkParams.PORT));
        return addresses;
    }

    @Override
    public String toString() {
        return connection.toString();
    }
}
