package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.node.network.Peer;
import com.theovier.democoin.node.network.messages.Message;
import com.theovier.democoin.node.network.messages.Responses.BlockchainHeightResponse;

import java.io.IOException;

public class BlockchainHeightRequest extends Request {

    @Override
    public void handle(Peer receiver) throws IOException {
        long blockchainHeight = receiver.getBlockchain().getHeight();
        Message response = new BlockchainHeightResponse(this.getID(), blockchainHeight);
        receiver.sendMessage(response);
    }

    @Override
    public String toString() {
        return "BlockChainHeightRequest";
    }
}
