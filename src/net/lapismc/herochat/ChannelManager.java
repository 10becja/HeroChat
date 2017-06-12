package net.lapismc.herochat;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class ChannelManager implements MessageFormatSupplier {
    private HashMap<String, Channel> channels = new HashMap<>();
    private Channel defaultChannel;
    private HashMap<Chatter.Permission, Permission> wildcardPermissions = new HashMap<>();
    private Set<Chatter.Permission> modPermissions = EnumSet.noneOf(Chatter.Permission.class);
    private ChannelStorage storage;
    private String standardFormat = "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}";
    private String emoteFormat = "{color}[{nick}] * {msg}";
    private String announceFormat = "{color}[{nick}] {msg}";
    private String conversationFormat = "&d{convoaddress} {convopartner}&d: {msg}";
    private boolean usingEmotes;

    public ChannelManager() {
        registerChannelPermissions();
    }

    public void addChannel(Channel channel) {
        this.channels.put(channel.getName().toLowerCase(), channel);
        this.channels.put(channel.getNick().toLowerCase(), channel);
        if (!channel.isTransient()) {
            for (Chatter.Permission p : Chatter.Permission.values()) {
                Permission perm = this.wildcardPermissions.get(p);
                perm.getChildren().put(p.form(channel).toLowerCase(), true);
                perm.recalculatePermissibles();
            }
            PluginManager pm = Bukkit.getServer().getPluginManager();
            String focusPermission = Chatter.Permission.FOCUS.form(channel).toLowerCase();
            try {
                pm.addPermission(new Permission(focusPermission, PermissionDefault.TRUE));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            String autojoinPermission = Chatter.Permission.AUTOJOIN.form(channel).toLowerCase();
            try {
                pm.addPermission(new Permission(autojoinPermission, PermissionDefault.FALSE));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            String forcejoinPermission = Chatter.Permission.FORCE_JOIN.form(channel).toLowerCase();
            try {
                pm.addPermission(new Permission(forcejoinPermission, PermissionDefault.FALSE));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            String forceleavePermission = Chatter.Permission.FORCE_LEAVE.form(channel).toLowerCase();
            try {
                pm.addPermission(new Permission(forceleavePermission, PermissionDefault.FALSE));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            if (this.defaultChannel == null) {
                this.defaultChannel = channel;
            }
            this.storage.addChannel(channel);
        }
    }

    void addModPermission(Chatter.Permission permission) {
        this.modPermissions.add(permission);
    }

    boolean checkModPermission(Chatter.Permission permission) {
        return this.modPermissions.contains(permission);
    }

    public void clear() {
        this.defaultChannel = null;
        for (Channel channel : this.channels.values()) {
            removeChannel(channel);
        }
        this.modPermissions.clear();
        this.storage = null;
        this.standardFormat = "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}";
        this.announceFormat = "{color}[{nick}] {msg}";
        this.emoteFormat = "{color}[{nick}] * {msg}";
        this.conversationFormat = "&d{convoaddress} {convopartner}&d: {msg}";
    }

    public Channel getChannel(String identifier) {
        return this.channels.get(identifier.toLowerCase());
    }

    public List<Channel> getChannels() {
        List<Channel> list = new ArrayList<>();
        for (Channel channel : this.channels.values()) {
            if (!list.contains(channel)) {
                list.add(channel);
            }
        }
        return list;
    }

    public String getConversationFormat() {
        return this.conversationFormat;
    }

    void setConversationFormat(String conversationFormat) {
        this.conversationFormat = conversationFormat;
    }

    public Channel getDefaultChannel() {
        return this.defaultChannel;
    }

    void setDefaultChannel(Channel channel) {
        this.defaultChannel = channel;
    }

    public Set<Chatter.Permission> getModPermissions() {
        return this.modPermissions;
    }

    public void setModPermissions(Set<Chatter.Permission> modPermissions) {
        this.modPermissions = modPermissions;
    }

    public String getStandardFormat() {
        return this.standardFormat;
    }

    void setStandardFormat(String standardFormat) {
        this.standardFormat = standardFormat;
    }

    public String getAnnounceFormat() {
        return this.announceFormat;
    }

    void setAnnounceFormat(String announceFormat) {
        this.announceFormat = announceFormat;
    }

    public String getEmoteFormat() {
        return this.emoteFormat;
    }

    void setEmoteFormat(String emoteFormat) {
        this.emoteFormat = emoteFormat;
    }

    public ChannelStorage getStorage() {
        return this.storage;
    }

    void setStorage(ChannelStorage storage) {
        this.storage = storage;
    }

    public boolean isUsingEmotes() {
        return this.usingEmotes;
    }

    void setUsingEmotes(boolean usingEmotes) {
        this.usingEmotes = usingEmotes;
    }

    public boolean hasChannel(String identifier) {
        return this.channels.containsKey(identifier.toLowerCase());
    }

    public void loadChannels() {
        for (Channel channel : this.storage.loadChannels()) {
            if (null != channel) {
                addChannel(channel);
            }
        }
    }

    private void registerChannelPermissions() {
        for (Chatter.Permission p : Chatter.Permission.values()) {
            Permission perm = new Permission(p.formWildcard(), PermissionDefault.FALSE);
            Bukkit.getServer().getPluginManager().addPermission(perm);
            this.wildcardPermissions.put(p, perm);
        }
    }

    public void removeChannel(Channel channel) {
        this.channels.remove(channel.getName().toLowerCase());
        this.channels.remove(channel.getNick().toLowerCase());
        if (!channel.isTransient()) {
            for (Chatter.Permission p : Chatter.Permission.values()) {
                Permission perm = this.wildcardPermissions.get(p);
                perm.getChildren().remove(p.form(channel).toLowerCase());
                perm.recalculatePermissibles();
            }
            PluginManager pm = Bukkit.getServer().getPluginManager();
            String focusPermission = Chatter.Permission.FOCUS.form(channel).toLowerCase();
            pm.removePermission(focusPermission);
            String autojoinPermission = Chatter.Permission.AUTOJOIN.form(channel).toLowerCase();
            pm.removePermission(autojoinPermission);
            String forcejoinPermission = Chatter.Permission.FORCE_JOIN.form(channel).toLowerCase();
            pm.removePermission(forcejoinPermission);
            String forceleavePermission = Chatter.Permission.FORCE_LEAVE.form(channel).toLowerCase();
            pm.removePermission(forceleavePermission);

            this.storage.removeChannel(channel);
        }
    }
}
