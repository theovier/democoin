package com.theovier.democoin.node.network;

import java.io.IOException;

public class HandshakeFailedException extends IOException {

    public HandshakeFailedException() {

    }

    public HandshakeFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandshakeFailedException(String message) {
        super(message);
    }

    public HandshakeFailedException(Throwable cause) {
        super(cause);
    }
}
