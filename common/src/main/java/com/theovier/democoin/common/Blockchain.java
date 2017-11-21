package com.theovier.democoin.common;

import java.util.ArrayList;

public class Blockchain extends ArrayList<Block> {

    //todo: make class with member arraylist to hide the remove functions

    public Block getLastBlock() {
        if (this.size() > 0) {
            return this.get(this.size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Logging Blockchain: " + System.lineSeparator());
        this.forEach((block) -> sb.append(block + System.lineSeparator()));
        return sb.toString();
    }
}
