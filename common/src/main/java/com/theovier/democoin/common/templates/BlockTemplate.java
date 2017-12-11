package com.theovier.democoin.common.templates;

import com.theovier.democoin.common.Block;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.common.transaction.TxInput;
import com.theovier.democoin.common.transaction.TxOutput;

import java.util.List;

public class BlockTemplate extends Template {

    private static final String TEMPLATE_PATH    = "/VelocityTemplates/BlockTemplate.vm";
    private static final String HASH = "hash";
    private static final String INDEX = "index";
    private static final String TIMESTAMP = "timestamp";
    private static final String NONCE = "nonce";
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
        this.context.put( PREVIOUS_BLOCK_HASH,  block.getPreviousBlockHash() );
        this.context.put( MERKLE_ROOT, block.getMerkleRoot() );
        this.context.put( TRANSACTIONS, block.getTransactions() );
    }

    @Override
    public String getFilledTemplate() {
        fillPlaceholder();
        return super.createFilledTemplate();
    }

    private String getTransactionsAsXML() {
        List<Transaction> transactions = block.getTransactions();
        if (transactions.size() == 0) return "";

        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < ( transactions.size() - 1 ); i++ ) {
            sb.append(transactions.get(i).toXML());
            sb.append(System.lineSeparator());
        }
        sb.append(transactions.get(transactions.size()-1).toXML());
        return sb.toString();
    }
}
