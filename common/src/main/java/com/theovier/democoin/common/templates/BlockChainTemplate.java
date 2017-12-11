package com.theovier.democoin.common.templates;

import com.theovier.democoin.common.Blockchain;


public class BlockChainTemplate extends Template {

    private static final String TEMPLATE_PATH    = "/VelocityTemplates/BlockChainTemplate.vm";
    private static final String BLOCKS = "blocks";

    private final Blockchain blockchain;

    public BlockChainTemplate(final Blockchain blockchain) {
        super();
        this.blockchain = blockchain;
    }

    @Override
    protected void initVelocityTemplate() {
        this.template = this.ve.getTemplate( TEMPLATE_PATH );
    }

    @Override
    protected void fillPlaceholder() {
        this.context.put(BLOCKS, blockchain );
    }

    @Override
    public String getFilledTemplate() {
        fillPlaceholder();
        return super.createFilledTemplate();
    }
}
