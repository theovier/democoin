package com.theovier.democoin.common;

public interface BlockFinder {
    void registerBlockFoundListener(final BlockFoundListener listener);
    void unregisterBlockFoundListener(final BlockFoundListener listener);
    void notifyBlockFoundListeners(final Block block);
}
