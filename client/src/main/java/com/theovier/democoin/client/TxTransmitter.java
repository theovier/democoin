package com.theovier.democoin.client;

import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.node.network.NetworkParams;
import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.PeerObserver;
import com.theovier.democoin.node.network.messages.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.theovier.democoin.node.network.NetworkParams.HANDSHAKE_TIMEOUT;
import static com.theovier.democoin.node.network.NetworkParams.HANDSHAKE_TIMEUNIT;

class TxTransmitter {

    static boolean sendTransactionToHost(final Transaction tx, final String host) throws IOException, InterruptedException {
        Peer peer = new Peer(new Socket(host, NetworkParams.PORT), new ObserverMock(), null, true);
        peer.answerHandshake(HANDSHAKE_TIMEOUT, HANDSHAKE_TIMEUNIT);
        boolean accepted = peer.requestTransactionBroadcast(tx);
        peer.disconnect();
        return accepted;
    }

    private static class ObserverMock implements PeerObserver {

        @Override
        public boolean isAcceptingConnections() {
            return false;
        }

        @Override
        public void onPeerConnectionEstablished(Peer peer) {

        }

        @Override
        public void onPeerConnectionClosed(Peer peer) {

        }

        @Override
        public void broadcast(Message msg, Peer sender) {

        }

        @Override
        public List<InetSocketAddress> getConnectedAddresses() {
            return new ArrayList<>();
        }
    }

}
