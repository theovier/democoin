package com.theovier.democoin.node.network.messages.Notifications;

import com.theovier.democoin.node.network.NetworkParams;
import com.theovier.democoin.node.network.Peer;
import org.apache.log4j.Logger;


public class VersionRejectNotification extends VersionNotification {

    private static final long serialVersionUID = 6804229203342533283L;
    private static final Logger LOG = Logger.getLogger(VersionRejectNotification.class);
    private final int requiredVersion;

    public VersionRejectNotification(final int requiredVersion) {
        this.requiredVersion = requiredVersion;
    }

    @Override
    public void handle(Peer receiver) {
        LOG.warn(String.format("node (running version %d) rejected the connection because we use a different version (%d)",
                requiredVersion, NetworkParams.PROTOCOL_VERSION));
    }

    @Override
    public String toString() {
        return "VersionRejectNotification";
    }
}
