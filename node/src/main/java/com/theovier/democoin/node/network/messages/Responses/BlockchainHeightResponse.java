package com.theovier.democoin.node.network.messages.Responses;

import java.util.UUID;

public class BlockchainHeightResponse extends Response {

    private static final long serialVersionUID = -1155053010840163972L;
    private final long height;

    public BlockchainHeightResponse(final UUID requestId, final long height) {
        super(requestId);
        this.height = height;
    }

    public long getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "BlockchainHeightResponse";
    }
}
