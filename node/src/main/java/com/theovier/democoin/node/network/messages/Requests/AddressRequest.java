package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Responses.AddressResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class AddressRequest extends Request {
    private static final int MAX_REQUESTED_ADDRESSES = 5;

    @Override
    public void handle(Peer receiver) throws IOException {
        AddressResponse response = new AddressResponse(getID());
        List<InetSocketAddress> knownAddresses = receiver.getKnownAddressesFromNode().stream()
                .limit(MAX_REQUESTED_ADDRESSES)
                .unordered()
                .collect(Collectors.toList());
        response.setAddresses(knownAddresses);
        receiver.sendMessage(response);
    }

    @Override
    public String toString() {
        return "AddressRequest";
    }
}
