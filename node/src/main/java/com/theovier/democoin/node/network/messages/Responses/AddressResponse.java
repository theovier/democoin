package com.theovier.democoin.node.network.messages.Responses;


import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

public class AddressResponse extends Response {

    private static final long serialVersionUID = 5573934517671506034L;
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
    public String toString() {
        return "AddressResponse";
    }
}
