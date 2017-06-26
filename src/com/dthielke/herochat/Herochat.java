package com.dthielke.herochat;

import com.dthielke.herochat.command.CommandHandler;
import com.dthielke.herochat.command.commands.*;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Herochat extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final Logger chatLog = Logger.getLogger("Herochat");
    private static final CommandHandler commandHandler = new CommandHandler();
    private static final ChannelManager channelManager = new ChannelManager();
    private static final ChatterManager chatterManager = new ChatterManager();
    private static final MessageHandler messageHandler = new MessageHandler();
    private static final ConfigManager configManager = new ConfigManager();
    private static Chat chatService;
    private static Herochat plugin;
    private static ResourceBundle messages;
    private static boolean logToBukkit;

    public static ChannelManager getChannelManager() {
        return channelManager;
    }

    static Chat getChatService() {
        return chatService;
    }

    public static ChatterManager getChatterManager() {
        return chatterManager;
    }

    public static CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static String getMessage(String key)
            throws MessageNotFoundException {
        return messages.getString(key);
    }

    public static MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public static Herochat getPlugin() {
        return plugin;
    }

    public static boolean hasChannelPermission(CommandSender user, Channel channel, Chatter.Permission permission) {
        String formedPermission = permission.form(channel).toLowerCase();
        return user.isPermissionSet(formedPermission) ? user.hasPermission(formedPermission) : user.hasPermission(permission.formAll());
    }

    public static void info(String message) {
        log.info("[Herochat] " + message);
    }

    static void logChat(String message) {
        chatLog.info(ChatColor.stripColor(message));
    }

    static void setLogToBukkitEnabled(boolean enabled) {
        logToBukkit = enabled;
    }

    static void setLocale(Locale locale)
            throws ClassNotFoundException {
        messages = ResourceBundle.getBundle("com.dthielke.herochat.resources.Messages", locale);
        if (messages == null) {
            throw new ClassNotFoundException("com.dthielke.herochat.resources.Messages");
        }
    }

    public static void severe(String message) {
        log.severe("[Herochat] " + message);
    }

    static void warning(String message) {
        log.warning("[Herochat] " + message);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandHandler.dispatch(sender, label, args);
    }

    public void onDisable() {
        if (channelManager.getStorage() != null) {
            channelManager.getStorage().update();
        }
        if (chatterManager.getStorage() != null) {
            chatterManager.getStorage().update();
        }
        info("Version " + getDescription().getVersion() + " is disabled.");
    }

    public void onEnable() {
        plugin = this;

        setupStorage();
        setupChatService();
        channelManager.loadChannels();
        try {
            configManager.load(new File(getDataFolder(), "config.yml"));
        } catch (ClassNotFoundException e) {
            info("Unable to load translation information.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        channelManager.getStorage().update();
        setupChatLog();
        for (Player player : getServer().getOnlinePlayers()) {
            chatterManager.addChatter(player);
        }
        registerCommands();
        registerEvents();

        info("Version " + getDescription().getVersion() + " is enabled.");
    }

    public void setupStorage() {
        File channelFolder = new File(getDataFolder(), "channels");
        channelFolder.mkdirs();
        ChannelStorage channelStorage = new YMLChannelStorage(channelFolder);
        channelManager.setStorage(channelStorage);

        File chatterFolder = new File(getDataFolder(), "chatters");
        chatterFolder.mkdirs();
        ChatterStorage chatterStorage = new YMLChatterStorage(chatterFolder);
        chatterManager.setStorage(chatterStorage);
    }

    private void registerCommands() {
        commandHandler.addCommand(new FocusCommand());
        commandHandler.addCommand(new JoinCommand());
        commandHandler.addCommand(new LeaveCommand());
        commandHandler.addCommand(new QuickMsgCommand());
        commandHandler.addCommand(new IgnoreCommand());
        commandHandler.addCommand(new MsgCommand());
        commandHandler.addCommand(new ReplyCommand());
        commandHandler.addCommand(new ListCommand());
        commandHandler.addCommand(new WhoCommand());
        commandHandler.addCommand(new AFKCommand());
        commandHandler.addCommand(new EmoteCommand());
        commandHandler.addCommand(new CreateCommand());
        commandHandler.addCommand(new RemoveCommand());
        commandHandler.addCommand(new SetCommand());
        commandHandler.addCommand(new InfoCommand());
        commandHandler.addCommand(new MuteCommand());
        commandHandler.addCommand(new KickCommand());
        commandHandler.addCommand(new BanCommand());
        commandHandler.addCommand(new ModCommand());
        commandHandler.addCommand(new SaveCommand());
        commandHandler.addCommand(new ReloadCommand());
        commandHandler.addCommand(new HelpCommand());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        HCPlayerListener pcl = new HCPlayerListener(this);
        pm.registerEvents(pcl, this);
    }

    private void setupChatLog() {
        chatLog.setLevel(Level.INFO);
        chatLog.setParent(log);
        chatLog.setUseParentHandlers(logToBukkit);
        File logDir = new File(getDataFolder(), "logs");
        logDir.mkdirs();
        String filename = logDir.getAbsolutePath() + "/chat.%g.%u.log";
        try {
            FileHandler chatLogHandler = new FileHandler(filename, 524288, 1000, true);
            chatLogHandler.setFormatter(new ChatLogFormatter());
            chatLog.addHandler(chatLogHandler);
        } catch (IOException e) {
            warning("Failed to create chat log handler.");
            e.printStackTrace();
        }
    }

    private void setupChatService() {
        RegisteredServiceProvider<Chat> svc = getServer().getServicesManager().getRegistration(Chat.class);
        if (svc != null) {
            chatService = svc.getProvider();
        }
    }
}
