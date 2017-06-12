package net.lapismc.herochat;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChatCompleteEvent
        extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Chatter sender;
    private final Channel channel;
    private final String msg;

    ChatCompleteEvent(Chatter sender, Channel channel, String msg) {
        this.sender = sender;
        this.channel = channel;
        this.msg = msg;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Chatter getSender() {
        return this.sender;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public String getMsg() {
        return this.msg;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
