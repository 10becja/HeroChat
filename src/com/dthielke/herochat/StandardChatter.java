package com.dthielke.herochat;

import com.dthielke.herochat.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class StandardChatter
        implements Chatter {
    private final Player player;
    private Chatter lastPMSource;
    private Channel activeChannel;
    private Channel lastActiveChannel;
    private Channel lastFocusableChannel;
    private ChatterStorage storage;
    private Set<Channel> channels = new HashSet<>();
    private Set<String> ignores = new HashSet<>();
    private String afkMessage = "";
    private boolean muted = false;
    private boolean afk = false;

    StandardChatter(ChatterStorage storage, Player player) {
        this.storage = storage;
        this.player = player;
    }

    public boolean addChannel(Channel channel, boolean announce, boolean flagUpdate) {
        if (this.channels.contains(channel)) {
            return false;
        }
        this.channels.add(channel);
        if (!channel.isMember(this)) {
            channel.addMember(this, announce, flagUpdate);
        }
        if (flagUpdate) {
            this.storage.flagUpdate(this);
        }
        return true;
    }

    public void attachStorage(ChatterStorage storage) {
        this.storage = storage;
    }

    public Chatter.Result canBan(Channel channel) {
        if (Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.BAN)) {
            return Chatter.Result.ALLOWED;
        }
        if ((channel.isModerator(this.player.getName())) && (Herochat.getChannelManager().checkModPermission(Chatter.Permission.BAN))) {
            return Chatter.Result.ALLOWED;
        }
        return Chatter.Result.NO_PERMISSION;
    }

    public Chatter.Result canColorMessages(Channel channel, ChatColor color) {
        if (Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.COLOR)) {
            return Chatter.Result.ALLOWED;
        }
        if (!Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.valueOf(color.name()))) {
            return Chatter.Result.NO_PERMISSION;
        }
        return Chatter.Result.ALLOWED;
    }

    public Chatter.Result canEmote(Channel channel) {
        if (channel == null) {
            return Chatter.Result.NO_CHANNEL;
        }
        if (!channel.isMember(this)) {
            return Chatter.Result.INVALID;
        }
        if ((channel.isTransient()) || (!Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.EMOTE))) {
            return Chatter.Result.NO_PERMISSION;
        }
        if ((this.muted) || (channel.isMuted(this.player.getName()))) {
            return Chatter.Result.MUTED;
        }
        return Chatter.Result.ALLOWED;
    }

    public Chatter.Result canFocus(Channel channel) {
        if (!Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.FOCUS)) {
            return Chatter.Result.NO_PERMISSION;
        }
        Chatter.Result speak = canSpeak(channel);
        if ((speak != Chatter.Result.ALLOWED) && (speak != Chatter.Result.INVALID)) {
            return Chatter.Result.NO_PERMISSION;
        }
        return Chatter.Result.ALLOWED;
    }

    public Chatter.Result canJoin(Channel channel, String password) {
        if (channel.isMember(this)) {
            return Chatter.Result.INVALID;
        }
        if (!Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.JOIN)) {
            return Chatter.Result.NO_PERMISSION;
        }
        if (channel.isBanned(this.player.getName())) {
            return Chatter.Result.BANNED;
        }
        if (!password.equals(channel.getPassword())) {
            return Chatter.Result.BAD_PASSWORD;
        }
        return Chatter.Result.ALLOWED;
    }

    public Chatter.Result canKick(Channel channel) {
        if (Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.KICK)) {
            return Chatter.Result.ALLOWED;
        }
        if ((channel.isModerator(this.player.getName())) && (Herochat.getChannelManager().checkModPermission(Chatter.Permission.KICK))) {
            return Chatter.Result.ALLOWED;
        }
        return Chatter.Result.NO_PERMISSION;
    }

    public Chatter.Result canLeave(Channel channel) {
        if (!channel.isMember(this)) {
            return Chatter.Result.INVALID;
        }
        if (!Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.LEAVE)) {
            return Chatter.Result.NO_PERMISSION;
        }
        return Chatter.Result.ALLOWED;
    }

    public Chatter.Result canModify(String setting, Channel channel) {
        setting = setting.toLowerCase();
        Chatter.Permission permission;
        if (setting.equals("nick")) {
            permission = Chatter.Permission.MODIFY_NICK;
        } else {
            if (setting.equals("format")) {
                permission = Chatter.Permission.MODIFY_FORMAT;
            } else {
                if (setting.equals("distance")) {
                    permission = Chatter.Permission.MODIFY_DISTANCE;
                } else {
                    if (setting.equals("color")) {
                        permission = Chatter.Permission.MODIFY_COLOR;
                    } else {
                        if (setting.equals("shortcut")) {
                            permission = Chatter.Permission.MODIFY_SHORTCUT;
                        } else {
                            if (setting.equals("password")) {
                                permission = Chatter.Permission.MODIFY_PASSWORD;
                            } else {
                                if (setting.equals("verbose")) {
                                    permission = Chatter.Permission.MODIFY_VERBOSE;
                                } else {
                                    if (setting.equals("chatcost")) {
                                        permission = Chatter.Permission.MODIFY_CHATCOST;
                                    } else {
                                        if (setting.equals("crossworld")) {
                                            permission = Chatter.Permission.MODIFY_CROSSWORLD;
                                        } else {
                                            if (setting.equals("focusable")) {
                                                permission = Chatter.Permission.MODIFY_FOCUSABLE;
                                            } else {
                                                return Chatter.Result.INVALID;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (Herochat.hasChannelPermission(this.player, channel, permission)) {
            return Chatter.Result.ALLOWED;
        }
        if ((channel.isModerator(this.player.getName())) && (Herochat.getChannelManager().checkModPermission(permission))) {
            return Chatter.Result.ALLOWED;
        }
        return Chatter.Result.NO_PERMISSION;
    }

    public Chatter.Result canMute(Channel channel) {
        if (Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.MUTE)) {
            return Chatter.Result.ALLOWED;
        }
        if ((channel.isModerator(this.player.getName())) && (Herochat.getChannelManager().checkModPermission(Chatter.Permission.BAN))) {
            return Chatter.Result.ALLOWED;
        }
        return Chatter.Result.NO_PERMISSION;
    }

    public Chatter.Result canRemove(Channel channel) {
        if (Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.REMOVE)) {
            return Chatter.Result.ALLOWED;
        }
        if ((channel.isModerator(this.player.getName())) && (Herochat.getChannelManager().checkModPermission(Chatter.Permission.REMOVE))) {
            return Chatter.Result.ALLOWED;
        }
        return Chatter.Result.NO_PERMISSION;
    }

    public Chatter.Result canSpeak(Channel channel) {
        if (!channel.isMember(this)) {
            return Chatter.Result.INVALID;
        }
        if ((!channel.isTransient()) && (!Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.SPEAK))) {
            return Chatter.Result.NO_PERMISSION;
        }
        if ((this.muted) || (channel.isMuted(this.player.getName()))) {
            return Chatter.Result.MUTED;
        }
        return Chatter.Result.ALLOWED;
    }

    public Chatter.Result canViewInfo(Channel channel) {
        if (!Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.INFO)) {
            return Chatter.Result.NO_PERMISSION;
        }
        return Chatter.Result.ALLOWED;
    }

    public boolean equals(Object other) {
        return other == this || other != null && other instanceof Chatter && this.player.equals(((Chatter) other).getPlayer());
    }

    public Channel getActiveChannel() {
        return this.activeChannel;
    }

    public String getAFKMessage() {
        return this.afkMessage;
    }

    public void setAFKMessage(String message) {
        this.afkMessage = message;
    }

    public Set<Channel> getChannels() {
        return this.channels;
    }

    public Set<String> getIgnores() {
        return this.ignores;
    }

    public Channel getLastActiveChannel() {
        return this.lastActiveChannel;
    }

    public Channel getLastFocusableChannel() {
        return this.lastFocusableChannel;
    }

    public Chatter getLastPrivateMessageSource() {
        return this.lastPMSource;
    }

    public void setLastPrivateMessageSource(Chatter chatter) {
        this.lastPMSource = chatter;
    }

    public String getName() {
        return this.player.getName();
    }

    public Player getPlayer() {
        return this.player;
    }

    public ChatterStorage getStorage() {
        return this.storage;
    }

    public boolean hasChannel(Channel channel) {
        return this.channels.contains(channel);
    }

    public int hashCode() {
        return this.player.hashCode();
    }

    public boolean isAFK() {
        return this.afk;
    }

    public void setAFK(boolean afk) {
        this.afk = afk;
    }

    public boolean isIgnoring(Chatter other) {
        return isIgnoring(other.getName());
    }

    public boolean isIgnoring(String name) {
        return this.ignores.contains(name.toLowerCase());
    }

    public boolean isInRange(Chatter other, int distance) {
        Player otherPlayer = other.getPlayer();
        return this.player.getWorld().equals(otherPlayer.getWorld()) && this.player.getLocation().distanceSquared(otherPlayer.getLocation()) <= distance * distance;
    }

    public boolean isMuted() {
        return this.muted;
    }

    public void removeChannel(Channel channel, boolean announce, boolean flagUpdate) {
        if (!this.channels.contains(channel)) {
            return;
        }
        this.channels.remove(channel);
        if (channel.isMember(this)) {
            channel.removeMember(this, announce, flagUpdate);
        }
        if (flagUpdate) {
            this.storage.flagUpdate(this);
        }
    }

    public void setActiveChannel(Channel channel, boolean announce, boolean flagUpdate) {
        if ((channel != null) && (channel.equals(this.activeChannel))) {
            return;
        }
        if (this.activeChannel != null) {
            this.activeChannel.onFocusLoss(this);
        }
        if ((this.activeChannel != null) && (!this.activeChannel.isTransient())) {
            this.lastActiveChannel = this.activeChannel;
            if (canFocus(this.activeChannel) == Chatter.Result.ALLOWED) {
                this.lastFocusableChannel = this.activeChannel;
            }
        }
        this.activeChannel = channel;
        if (this.activeChannel != null) {
            this.activeChannel.onFocusGain(this);
            if (announce) {
                try {
                    Messaging.send(this.player, Herochat.getMessage("chatter_focus"), channel.getColor() + channel.getName());
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: chatter_focus");
                }
            }
        }
        if (flagUpdate) {
            this.storage.flagUpdate(this);
        }
    }

    public void setIgnore(String name, boolean ignore, boolean flagUpdate) {
        if (ignore) {
            this.ignores.add(name.toLowerCase());
        } else {
            this.ignores.remove(name.toLowerCase());
        }
        if (flagUpdate) {
            this.storage.flagUpdate(this);
        }
    }

    public void setMuted(boolean muted, boolean flagUpdate) {
        this.muted = muted;
        if (flagUpdate) {
            this.storage.flagUpdate(this);
        }
    }

    public boolean shouldAutoJoin(Channel channel) {
        return Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.AUTOJOIN);
    }

    public boolean shouldForceJoin(Channel channel) {
        return Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.FORCE_JOIN);
    }

    public boolean shouldForceLeave(Channel channel) {
        return Herochat.hasChannelPermission(this.player, channel, Chatter.Permission.FORCE_LEAVE);
    }

    public void refocus() {
        if (this.activeChannel != null) {
            if (canFocus(this.activeChannel) == Chatter.Result.ALLOWED) {
                return;
            }
            this.lastActiveChannel = this.activeChannel;
            this.lastActiveChannel.onFocusLoss(this);
            this.activeChannel = null;
        }
        for (Channel channel : getChannels()) {
            if (canFocus(channel) == Chatter.Result.ALLOWED) {
                this.activeChannel = channel;
                this.activeChannel.onFocusGain(this);
                try {
                    Messaging.send(this.player, Herochat.getMessage("chatter_focus"), channel.getColor() + channel.getName());
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: chatter_focus");
                }
            }
        }
    }

    public Chatter.Result canIgnore(Chatter other) {
        return other.getPlayer().hasPermission("herochat.admin.unignore") ? Chatter.Result.NO_PERMISSION : Chatter.Result.ALLOWED;
    }

    public void disconnect() {
        Iterator<Channel> iter = this.channels.iterator();
        while (iter.hasNext()) {
            Channel channel = iter.next();
            iter.remove();
            channel.removeMember(this, false, false);
        }
    }
}
