package com.dthielke.herochat;

import com.dthielke.herochat.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YMLChannelStorage
        implements ChannelStorage {
    private final File channelFolder;
    private Map<Channel, FileConfiguration> configs = new HashMap<>();
    private Set<Channel> updates = new HashSet<>();

    YMLChannelStorage(File channelFolder) {
        this.channelFolder = channelFolder;
    }

    public void addChannel(Channel channel) {
        if ((this.configs.containsKey(channel)) || (channel.isTransient())) {
            return;
        }
        File file = new File(this.channelFolder, channel.getName() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.exists()) {
                config.load(file);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        this.configs.put(channel, config);
        flagUpdate(channel);
    }

    public void flagUpdate(Channel channel) {
        if (!channel.isTransient()) {
            this.updates.add(channel);
        }
    }

    public Channel load(String name) {
        File file = new File(this.channelFolder, name + ".yml");
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            Herochat.severe("Could not open file " + file.getName());
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            Herochat.severe("Could not load channel " + file.getName());
            e.printStackTrace();
        }
        String nick = config.getString("nick", name);
        String format = config.getString("format", "{default}");
        String password = config.getString("password", "");
        ChatColor color = Messaging.parseColor(config.getString("color", "WHITE"));
        if (color == null) {
            Herochat.warning("The color '" + config.getString("color") + "' is not valid.");
            color = ChatColor.WHITE;
        }
        int distance = config.getInt("distance", 0);
        boolean shortcut;
        if (config.contains("shortcutAllowed")) {
            shortcut = config.getBoolean("shortcutAllowed", false);
        } else {
            shortcut = config.getBoolean("shortcut", false);
        }
        boolean verbose = config.getBoolean("verbose", true);
        boolean muted = config.getBoolean("muted", false);
        config.addDefault("worlds", new ArrayList());
        config.addDefault("bans", new ArrayList());
        config.addDefault("mutes", new ArrayList());
        config.addDefault("moderators", new ArrayList());
        Set<String> bans = new HashSet<>(config.getStringList("bans"));
        Set<String> mutes = new HashSet<>(config.getStringList("mutes"));
        Set<String> moderators = new HashSet<>(config.getStringList("moderators"));

        Channel channel = new StandardChannel(this, name, nick, Herochat.getChannelManager());
        channel.setFormat(format);
        channel.setPassword(password);
        channel.setColor(color);
        channel.setDistance(distance);
        channel.setShortcutAllowed(shortcut);
        channel.setVerbose(verbose);
        channel.setMuted(muted);
        channel.setBans(bans);
        channel.setMutes(mutes);
        channel.setModerators(moderators);
        addChannel(channel);
        return channel;
    }

    public Set<Channel> loadChannels() {
        Set<Channel> channels = new HashSet<>();
        for (String name : this.channelFolder.list()) {
            name = name.substring(0, name.lastIndexOf('.'));
            Channel channel = load(name);
            addChannel(channel);
            channels.add(channel);
        }
        return channels;
    }

    public void removeChannel(Channel channel) {
        this.configs.remove(channel);
        flagUpdate(channel);
    }

    public void update() {
        Herochat.info("Saving channels");
        Iterator<Channel> iter = this.updates.iterator();
        while (iter.hasNext()) {
            Channel channel = iter.next();
            update(channel);
            iter.remove();
        }
        Herochat.info("Save complete");
    }

    public void update(Channel c) {
        Channel channel = Herochat.getChannelManager().getChannel(c.getName());
        File file = new File(this.channelFolder, c.getName() + ".yml");
        if (channel != null) {
            FileConfiguration config = this.configs.get(channel);
            config.options().copyDefaults(true);
            config.set("name", channel.getName());
            config.set("nick", channel.getNick());
            config.set("format", channel.getFormat());
            config.set("password", channel.getPassword());
            config.set("color", channel.getColor().name());
            config.set("distance", channel.getDistance());
            config.set("shortcut", channel.isShortcutAllowed());
            config.set("verbose", channel.isVerbose());
            config.set("muted", channel.isMuted());
            config.set("bans", new ArrayList<>(channel.getBans()));
            config.set("mutes", new ArrayList<>(channel.getMutes()));
            config.set("moderators", new ArrayList<>(channel.getModerators()));
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
        }
    }
}
