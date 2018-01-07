package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Message;
import com.theovier.democoin.node.network.messages.Responses.BlockchainResponse;

import java.io.IOException;

public class BlockchainRequest extends Request {

    private static final long serialVersionUID = -115822910295021979L;

    @Override
    public void handle(Peer receiver) throws IOException {
        Message response = new BlockchainResponse(getID(), receiver.getBlockchain());
        receiver.sendMessage(response);
    }

    @Override
    public String toString() {
        return "BlockchainRequest";
    }
}
