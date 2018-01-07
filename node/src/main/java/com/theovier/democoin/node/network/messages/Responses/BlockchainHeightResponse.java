package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.node.network.Peer;

import java.io.IOException;
import java.util.UUID;

public class BlockchainHeightResponse extends Response {

    private static final long serialVersionUID = -1155053010840163972L;
    private final long height;

    public BlockchainHeightResponse(final UUID requestId, final long height) {
        super(requestId);
        this.height = height;
    }

    @Override
    public void handle(Peer receiver) throws IOException {
        long localHeight = receiver.getBlockchain().getHeight();

        if (localHeight < height) {
            //send BlockchainRequest to obtain the longer Blockchain
        } else if (localHeight > height) {
            //inform other Node that our Blockchain is longer (ReplaceBlockchainRequest)
        } else {
            //same length - what to do?
        }
    }

    public long getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "BlockchainHeightResponse";
    }
}
