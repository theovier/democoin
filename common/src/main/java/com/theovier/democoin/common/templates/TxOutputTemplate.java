package com.theovier.democoin.common.templates;

import com.theovier.democoin.common.transaction.TxOutput;

public class TxOutputTemplate extends Template {

    private static final String TEMPLATE_PATH = "/VelocityTemplates/TxOutputTemplate.vm";
    private static final String ADDRESS = "address";
    private static final String VALUE = "value";


    private final TxOutput output;

    public TxOutputTemplate(final TxOutput output) {
        super();
        this.output = output;
    }

    @Override
    protected void initVelocityTemplate() {
        this.template = this.ve.getTemplate( TEMPLATE_PATH );
    }

    @Override
    protected void fillPlaceholder() {
        this.context.put( ADDRESS, output.getRecipientAddress());
        this.context.put( VALUE, output.getValue());
    }

    @Override
    public String getFilledTemplate() {
        fillPlaceholder();
        return super.createFilledTemplate();
    }
}
