package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.messages.Response;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

public class AddressResponse extends Response {

    List<InetSocketAddress> addresses;

    public AddressResponse(final UUID requestId) {
        super(requestId);
    }
}
