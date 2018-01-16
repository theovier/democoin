package com.theovier.democoin.common;

import com.theovier.democoin.common.Block;

public interface BlockFoundListener {
    void onBlockFound(final Block block);
}
