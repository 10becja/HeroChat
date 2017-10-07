package com.dthielke.herochat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YMLChatterStorage
        implements ChatterStorage {
    private final File chatterFolder;
    private Set<Chatter> updates = new HashSet<>();

    YMLChatterStorage(File chatterFolder) {
        this.chatterFolder = chatterFolder;
    }

    public void flagUpdate(Chatter chatter) {
        this.updates.add(chatter);
    }

    public Chatter load(String name) {
        OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(name);
        if (player == null) {
            return null;
        }
        UUID uuid = player.getUniqueId();
        File file = new File(this.chatterFolder, uuid.toString() + ".yml");
        File old = new File(this.chatterFolder, name.substring(0, 1).toLowerCase() + File.separator + name + ".yml");
        if (old.exists()) {
            old.renameTo(file);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.exists()) {
                config.load(file);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Chatter chatter = new StandardChatter(this, player);
        loadChannels(chatter, config);
        loadActiveChannel(chatter, config);
        loadIgnores(chatter, config);
        loadMuted(chatter, config);
        return chatter;
    }

    public void removeChatter(Chatter chatter) {
        update(chatter);
        this.updates.remove(chatter);
    }

    public void update() {
        if (!this.updates.isEmpty()) {
            Herochat.info("Saving players");
            Iterator<Chatter> iter = this.updates.iterator();
            while (iter.hasNext()) {
                Chatter chatter = iter.next();
                update(chatter);
                iter.remove();
            }
            Herochat.info("Save complete");
        }
    }

    public void update(Chatter chatter) {
        FileConfiguration config = new YamlConfiguration();
        String name = chatter.getName();
        String uuid = chatter.getuuid().toString();
        config.set("name", name);
        if (chatter.getActiveChannel() != null) {
            if (chatter.getActiveChannel().isTransient()) {
                config.set("activeChannel", chatter.getLastActiveChannel().getName());
            } else {
                config.set("activeChannel", chatter.getActiveChannel().getName());
            }
        }
        List<String> channels = new ArrayList<>();
        for (Channel channel : chatter.getChannels()) {
            if (!channel.isTransient()) {
                channels.add(channel.getName());
            }
        }
        config.set("channels", channels);
        config.set("ignores", new ArrayList<>(chatter.getIgnores()));
        config.set("muted", chatter.isMuted());
        config.set("autojoin", false);
        File file = new File(this.chatterFolder, uuid + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadActiveChannel(Chatter chatter, MemoryConfiguration config) {
        ChannelManager channelManager = Herochat.getChannelManager();
        Channel defaultChannel = channelManager.getDefaultChannel();
        Channel activeChannel = channelManager.getChannel(config.getString("activeChannel", ""));
        if ((activeChannel == null) || (!chatter.hasChannel(activeChannel))) {
            activeChannel = defaultChannel;
        }
        chatter.setActiveChannel(defaultChannel, false, false);
        chatter.setActiveChannel(activeChannel, false, false);
    }

    private void loadChannels(Chatter chatter, MemoryConfiguration config) {
        ChannelManager channelManager = Herochat.getChannelManager();
        Set<Channel> channels = new HashSet<>();


        config.addDefault("channels", new ArrayList());


        List<String> channelNames = config.getStringList("channels");
        for (String channelName : channelNames) {
            Channel channel = channelManager.getChannel(channelName);
            if ((channel != null) && (chatter.canJoin(channel, channel.getPassword()) == Chatter.Result.ALLOWED)) {
                channels.add(channel);
            }
        }
        boolean autojoin = config.getBoolean("autojoin", true);
        for (Channel channel : channelManager.getChannels()) {
            if (((autojoin) && (chatter.shouldAutoJoin(channel))) || (chatter.shouldForceJoin(channel))) {
                channels.add(channel);
            }
        }
        channels.removeIf(chatter::shouldForceLeave);
        if (channels.isEmpty()) {
            channels.add(channelManager.getDefaultChannel());
        }
        for (Channel channel : channels) {
            if (channel != null) {
                channel.addMember(chatter, false, false);
            }
        }
    }

    private void loadIgnores(Chatter chatter, MemoryConfiguration config) {
        config.addDefault("ignores", new ArrayList());
        List<String> ignores = config.getStringList("ignores");
        for (String ignore : ignores) {
            chatter.setIgnore(ignore, true, false);
        }
    }

    private void loadMuted(Chatter chatter, MemoryConfiguration config) {
        boolean muted = config.getBoolean("muted", false);
        chatter.setMuted(muted, false);
    }
}
