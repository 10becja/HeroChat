package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.ChatterManager;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class ReloadCommand
        extends BasicCommand {
    public ReloadCommand() {
        super("Reload");
        setDescription(getMessage("command_reload"));
        setUsage("/ch reload");
        setArgumentRange(0, 0);
        setIdentifiers("ch reload", "herochat reload");
        setPermission("herochat.reload");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        ChannelManager channelManager = Herochat.getChannelManager();
        ChatterManager chatterManager = Herochat.getChatterManager();

        channelManager.clear();
        chatterManager.clear();
        Herochat.getPlugin().setupStorage();

        channelManager.loadChannels();
        try {
            Herochat.getConfigManager().load(new File(Herochat.getPlugin().getDataFolder(), "config.yml"));
        } catch (ClassNotFoundException e) {
            Herochat.info("Unable to load translation information.");
            Bukkit.getPluginManager().disablePlugin(Herochat.getPlugin());
            return true;
        }
        channelManager.getStorage().update();
        chatterManager.reset();
        for (Player player : Bukkit.getOnlinePlayers()) {
            chatterManager.addChatter(player);
        }
        Messaging.send(sender, getMessage("reload_confirm"));
        return true;
    }
}
