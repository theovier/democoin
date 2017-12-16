package com.theovier.democoin.common.templates;

import com.theovier.democoin.common.Block;

public class BlockTemplate extends Template {

    private static final String TEMPLATE_PATH    = "/VelocityTemplates/BlockTemplate.vm";
    private static final String HASH = "hash";
    private static final String INDEX = "index";
    private static final String TIMESTAMP = "timestamp";
    private static final String NONCE = "nonce";
    private static final String TARGET = "target";
    private static final String PREVIOUS_BLOCK_HASH = "previousBlockHash";
    private static final String MERKLE_ROOT = "merkleRoot";
    private static final String TRANSACTIONS = "transactions";

    private final Block block;

    public BlockTemplate(final Block block) {
        super();
        this.block = block;
    }

    @Override
    protected void initVelocityTemplate() {
        this.template = this.ve.getTemplate( TEMPLATE_PATH );
    }

    @Override
    protected void fillPlaceholder() {
        this.context.put( HASH, block.getHash() );
        this.context.put( INDEX, block.getIndex() );
        this.context.put( TIMESTAMP, block.getTimestamp() );
        this.context.put( NONCE, block.getNonce() );
        this.context.put( TARGET, block.getTargetZeros() );
        this.context.put( PREVIOUS_BLOCK_HASH,  block.getPreviousBlockHash() );
        this.context.put( MERKLE_ROOT, block.getMerkleRoot() );
        this.context.put( TRANSACTIONS, block.getTransactions() );
    }

    @Override
    public String getFilledTemplate() {
        fillPlaceholder();
        return super.createFilledTemplate();
    }
}
