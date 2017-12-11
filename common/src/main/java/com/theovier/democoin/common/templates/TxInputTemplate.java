package com.theovier.democoin.common.templates;

import com.theovier.democoin.common.transaction.TxInput;

public class TxInputTemplate extends Template {

    private static final String TEMPLATE_PATH    = "/VelocityTemplates/TXInputTemplate.vm";
    private static final String PREV_TX_HASH = "prevTransactionHash";
    private static final String OUTPUT_INDEX = "outputIndex";
    private static final String SIGNATURE = "signature";

    private final TxInput input;

    public TxInputTemplate(final TxInput input) {
        super();
        this.input = input;
    }

    @Override
    protected void initVelocityTemplate() {
        this.template = this.ve.getTemplate( TEMPLATE_PATH );
    }

    @Override
    protected void fillPlaceholder() {
        this.context.put( PREV_TX_HASH, input.getPrevOutputInfo().getTransactionHash());
        this.context.put( OUTPUT_INDEX, input.getPrevOutputInfo().getOutputIndex());
        this.context.put( SIGNATURE, input.getSignature());
    }

    @Override
    public String getFilledTemplate() {
        fillPlaceholder();
        return super.createFilledTemplate();
    }
}
