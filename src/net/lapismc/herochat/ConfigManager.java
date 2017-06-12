package net.lapismc.herochat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ConfigManager {
    public void load(File file)
            throws ClassNotFoundException {
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.exists()) {
                config.load(file);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        config.setDefaults(getDefaults());

        ChannelManager channelManager = Herochat.getChannelManager();
        if (config.getBoolean("moderator-permissions.can-kick")) {
            channelManager.addModPermission(Chatter.Permission.KICK);
        }
        if (config.getBoolean("moderator-permissions.can-ban")) {
            channelManager.addModPermission(Chatter.Permission.BAN);
        }
        if (config.getBoolean("moderator-permissions.can-mute")) {
            channelManager.addModPermission(Chatter.Permission.MUTE);
        }
        if (config.getBoolean("moderator-permissions.can-remove-channel")) {
            channelManager.addModPermission(Chatter.Permission.REMOVE);
        }
        if (config.getBoolean("moderator-permissions.can-modify-nick")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_NICK);
        }
        if (config.getBoolean("moderator-permissions.can-modify-color")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_COLOR);
        }
        if (config.getBoolean("moderator-permissions.can-modify-distance")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_DISTANCE);
        }
        if (config.getBoolean("moderator-permissions.can-modify-password")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_PASSWORD);
        }
        if (config.getBoolean("moderator-permissions.can-modify-format")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_FORMAT);
        }
        if (config.getBoolean("moderator-permissions.can-modify-shortcut")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_SHORTCUT);
        }
        if (config.getBoolean("moderator-permissions.can-modify-verbose")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_VERBOSE);
        }
        if (config.getBoolean("moderator-permissions.can-modify-focusable")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_FOCUSABLE);
        }
        if (config.getBoolean("moderator-permissions.can-modify-crossworld")) {
            channelManager.addModPermission(Chatter.Permission.MODIFY_CROSSWORLD);
        }
        if (config.getBoolean("moderator-permissions.can-color-messages")) {
            channelManager.addModPermission(Chatter.Permission.COLOR);
        }
        if (config.getBoolean("moderator-permissions.can-view-info")) {
            channelManager.addModPermission(Chatter.Permission.INFO);
        }
        if (config.getBoolean("moderator-permissions.can-focus")) {
            channelManager.addModPermission(Chatter.Permission.FOCUS);
        }
        if (channelManager.getChannels().isEmpty()) {
            Channel defaultChannel = new StandardChannel(channelManager.getStorage(), "Global", "G", channelManager);
            defaultChannel.setColor(ChatColor.DARK_GREEN);
            channelManager.addChannel(defaultChannel);
        }
        String defaultChannel = config.getString("default-channel");
        if ((defaultChannel != null) && (channelManager.hasChannel(defaultChannel))) {
            channelManager.setDefaultChannel(channelManager.getChannel(defaultChannel));
        }
        Herochat.getMessageHandler().setCensors(config.getStringList("censors"));
        channelManager.setStandardFormat(config.getString("format.default"));
        channelManager.setAnnounceFormat(config.getString("format.announce"));
        channelManager.setEmoteFormat(config.getString("format.emote"));
        channelManager.setConversationFormat(config.getString("format.private-message"));
        channelManager.setUsingEmotes(config.getBoolean("use-channel-emotes", true));
        Herochat.setLocale(loadLocale(config.getString("locale")));
        Herochat.setLogToBukkitEnabled(config.getBoolean("log-to-bukkit", false));
        Herochat.getMessageHandler().setTwitterStyleMsgs(config.getBoolean("twitter-style-private-messages", true));
        try {
            config.options().copyDefaults(true);
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Locale loadLocale(String locale) {
        if (locale.contains("_")) {
            int index = locale.indexOf("_");
            return new Locale(locale.substring(0, index), locale.substring(index + 1));
        }
        if (locale.contains("-")) {
            int index = locale.indexOf("-");
            return new Locale(locale.substring(0, index), locale.substring(index + 1));
        }
        return new Locale(locale);
    }

    private MemoryConfiguration getDefaults() {
        MemoryConfiguration config = new MemoryConfiguration();
        config.set("moderator-permissions.can-kick", true);
        config.set("moderator-permissions.can-ban", true);
        config.set("moderator-permissions.can-mute", true);
        config.set("moderator-permissions.can-remove-channel", true);
        config.set("moderator-permissions.can-modify-nick", true);
        config.set("moderator-permissions.can-modify-color", true);
        config.set("moderator-permissions.can-modify-distance", true);
        config.set("moderator-permissions.can-modify-password", true);
        config.set("moderator-permissions.can-modify-format", false);
        config.set("moderator-permissions.can-modify-shortcut", false);
        config.set("moderator-permissions.can-modify-verbose", true);
        config.set("moderator-permissions.can-modify-focusable", false);
        config.set("moderator-permissions.can-modify-crossworld", false);
        config.set("moderator-permissions.can-color-messages", true);
        config.set("moderator-permissions.can-view-info", true);
        config.set("moderator-permissions.can-focus", true);
        config.set("default-channel", "Global");
        config.set("censors", new ArrayList<>());
        config.set("format.default", Herochat.getChannelManager().getStandardFormat());
        config.set("format.announce", Herochat.getChannelManager().getAnnounceFormat());
        config.set("format.emote", Herochat.getChannelManager().getEmoteFormat());
        config.set("format.private-message", Herochat.getChannelManager().getConversationFormat());
        config.set("use-channel-emotes", true);
        config.set("locale", "en_US");
        config.set("log-chat", true);
        config.set("log-to-bukkit", false);
        config.set("twitter-style-private-messages", true);
        return config;
    }
}
