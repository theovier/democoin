package com.theovier.democoin.node.network.messages.Notifications;

import com.theovier.democoin.common.Block;
import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.node.network.Peer;
import org.apache.log4j.Logger;

import java.io.IOException;


public final class BlockFoundNotification extends Notification {

    private static final Logger LOG = Logger.getLogger(BlockFoundNotification.class);
    private static final long serialVersionUID = -6256195613395593467L;
    private final Block foundBlock;

    public BlockFoundNotification(final Block block) {
        this.foundBlock = block;
    }

    @Override
    public void handle(Peer receiver) {
        if(receiver.isLightweight()) {
            return;
        }

        Blockchain blockchain = receiver.getBlockchain();
        long index = foundBlock.getIndex();
        long currentIndex = blockchain.getHeight() - 1;
        if (index > currentIndex + 1) {
            downloadLongerBlockchain(receiver, blockchain);
            receiver.broadcast(this);
        } else if (blockchain.append(foundBlock)) {
            blockchain.saveToDisc();
            receiver.broadcast(this);
        }
    }

    private void downloadLongerBlockchain(Peer peer, Blockchain current) {
        try {
            Blockchain other = peer.requestBlockchain();
            if (current.substitute(other)) {
                current.saveToDisc();
            }
        } catch (IOException | InterruptedException e) {
            LOG.error(e);
        }
    }

    @Override
    public String toString() {
        return "BlockFoundNotification";
    }
}
