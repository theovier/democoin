package com.theovier.democoin.node.network.messages.Requests;

import com.theovier.democoin.common.Block;
import com.theovier.democoin.node.network.Peer;
import org.apache.log4j.Logger;


public final class BlockFoundNotification extends Request {

    private static final Logger LOG = Logger.getLogger(BlockFoundNotification.class);
    private final Block foundBlock;

    public BlockFoundNotification(final Block block) {
        super();
        this.foundBlock = block;
    }

    @Override
    public void handle(Peer receiver) {
        boolean appended = receiver.getBlockchain().append(foundBlock);
        LOG.info(appended);
    }
}
