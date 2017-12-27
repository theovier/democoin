package com.theovier.democoin.node.network.messages;


public abstract class Request extends Message {

    //find something unique for the id? timestamp + inetaddress?
    private final String id;

    public Request() {
        this.id = "todo";
    }

    public String getID() {
        return id;
    }

}
