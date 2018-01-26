package com.theovier.democoin.node.network.messages;

import com.theovier.democoin.node.network.Peer;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MessageDispatcher implements Runnable {

    private static final Logger LOG = Logger.getLogger(MessageDispatcher.class);
    private final Message message;
    private final Peer receiver;

    public MessageDispatcher(final Message message, final Peer receiver) {
        this.message = message;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        try {
            message.handle(receiver);
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
