package com.theovier.democoin.node.network.messages.Responses;

import com.theovier.democoin.common.Blockchain;

import java.util.UUID;

public class BlockchainResponse extends Response {

    private static final long serialVersionUID = 2598004580138802517L;
    private final Blockchain blockchain;

    public BlockchainResponse(final UUID requestId, final Blockchain blockchain) {
        super(requestId);
        this.blockchain = blockchain;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    @Override
    public String toString() {
        return "BlockchainResponse";
    }
}
