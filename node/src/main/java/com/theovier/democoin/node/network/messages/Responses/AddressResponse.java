package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.messages.Response;

import java.net.InetSocketAddress;
import java.util.List;

public class AddressResponse extends Response {

    List<InetSocketAddress> addresses;

    public AddressResponse(final String requestId) {
        super(requestId);
    }
}
