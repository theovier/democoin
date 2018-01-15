package com.theovier.democoin.node.network;

public abstract class NetworkParams {

    public static final int PROTOCOL_VERSION = 1;
    public static final int PORT = 7777;
    public static final int MAX_OUT_CONNECTIONS = 5;
    public static final int MAX_IN_CONNECTIONS = 10;
    public static final int MAX_CONNECTIONS = MAX_IN_CONNECTIONS + MAX_OUT_CONNECTIONS;
    public static final String[] DEFAULT_HOSTS = new String[] {
            "192.168.1.48",
            "192.168.1.47"
    };
}
