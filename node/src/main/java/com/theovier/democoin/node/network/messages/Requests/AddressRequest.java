package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.IMessage;
import com.theovier.democoin.node.network.messages.Request;
import com.theovier.democoin.node.network.messages.Responses.AddressResponse;

import java.io.IOException;

public class AddressRequest extends Request {
    public static final int MAX_ADDRESSES = 100;

    @Override
    public void handle(Peer receiver) throws IOException {
        receiver.sendMessage(new AddressResponse(getID()));
    }
}
