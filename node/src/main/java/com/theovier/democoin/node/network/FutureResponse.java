package com.theovier.democoin.node.network;

import com.theovier.democoin.node.network.messages.Request;
import com.theovier.democoin.node.network.messages.Response;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//credit: https://github.com/bitcoin-labs/bitcoinj-minimal/blob/master/core/Peer.java
public class FutureResponse implements Future<Response> {

    private boolean cancelled;
    private Request request;
    private Response result;

    public FutureResponse(Request request) {
        this.request = request;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        //we cant cancel a sent request
        cancelled = true;
        return false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return result == null || cancelled;
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        //...
        return result;
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    public void setResult(Response result) {
        //this.thread == currentThread?!
        this.result = result;
    }

    public UUID requestID() {
        return request.getID();
    }
}
