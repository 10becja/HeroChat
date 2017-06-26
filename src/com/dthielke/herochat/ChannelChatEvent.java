package com.dthielke.herochat;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChannelChatEvent
        extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Chatter sender;
    private Channel channel;
    private Chatter.Result result;
    private String msg;
    private String format;
    private String bukkitFormat;

    ChannelChatEvent(Chatter sender, Channel channel, Chatter.Result result, String msg, String bukkitFormat, String channelFormat) {
        this.sender = sender;
        this.channel = channel;
        this.result = result;
        this.msg = msg;
        this.format = channelFormat;
        this.bukkitFormat = bukkitFormat;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getMessage() {
        return this.msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getBukkitFormat() {
        return this.bukkitFormat;
    }

    public void setBukkitFormat(String bukkitFormat) {
        this.bukkitFormat = bukkitFormat;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void setChannel(Channel channel) {
        if (channel != null) {
            this.channel = channel;
        }
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Chatter.Result getResult() {
        return this.result;
    }

    public void setResult(Chatter.Result result) {
        this.result = result;
    }

    public Chatter getSender() {
        return this.sender;
    }
}
