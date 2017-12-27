package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.IMessage;
import com.theovier.democoin.node.network.messages.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

public class AddressResponse extends Response implements IMessage {

    private List<InetSocketAddress> addresses;

    public AddressResponse(final UUID requestId) {
        super(requestId);
    }

    public void setAddresses(List<InetSocketAddress> addresses) {
        this.addresses = addresses;
    }

    public List<InetSocketAddress> getAddresses() {
        return addresses;
    }

    @Override
    public void handle(Peer receiver) throws IOException {
        receiver.receivedResponse(this);
    }
}
