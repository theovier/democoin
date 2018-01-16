package com.theovier.democoin.node.network.messages.Notifications;


import com.theovier.democoin.node.network.messages.Message;

/**
 * notifications don't expect an answser from the other party.
 */
public abstract class Notification extends Message {
    private static final long serialVersionUID = 281759061353508675L;
}
