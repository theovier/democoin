package com.theovier.democoin.common.templates;

import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.common.transaction.TxInput;
import com.theovier.democoin.common.transaction.TxOutput;

import java.util.List;

public class TransactionTemplate extends Template {

    private static final String TEMPLATE_PATH    = "/VelocityTemplates/TransactionTemplate.vm";
    private static final String TXID = "txid";
    private static final String TIMESTAMP = "timestamp";
    private static final String MSG = "msg";
    private static final String COINBASE = "coinbase";
    private static final String INPUTS = "inputs";
    private static final String OUTPUTS = "outputs";

    private final Transaction transaction;

    public TransactionTemplate(final Transaction transaction) {
        super();
        this.transaction = transaction;
    }

    @Override
    protected void initVelocityTemplate() {
        this.template = this.ve.getTemplate( TEMPLATE_PATH );
    }

    @Override
    protected void fillPlaceholder() {
        this.context.put( TXID, transaction.getTxId() );
        this.context.put( TIMESTAMP, transaction.getTimestamp() );
        this.context.put( MSG, transaction.getMessage());
        this.context.put( INPUTS, transaction.getInputs() );
        this.context.put( OUTPUTS, transaction.getOutputs() );
    }

    @Override
    public String getFilledTemplate() {
        fillPlaceholder();
        return super.createFilledTemplate();
    }
}
