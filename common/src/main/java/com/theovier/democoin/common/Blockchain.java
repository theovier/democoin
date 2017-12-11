package com.theovier.democoin.common;

import com.theovier.democoin.common.templates.BlockChainTemplate;
import com.theovier.democoin.common.templates.FillableTemplate;

import java.util.ArrayList;

public class Blockchain extends ArrayList<Block> {

    private FillableTemplate template = new BlockChainTemplate(this);

    public Block getLastBlock() {
        if (this.size() > 0) {
            return this.get(this.size() - 1);
        } else {
            return null;
        }
    }

    public String toXML() {
        return template.getFilledTemplate();
    }

    @Override
    public String toString() {
        return toXML();
    }
}
