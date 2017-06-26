package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand
        extends BasicCommand {
    public InfoCommand() {
        super("Info");
        setDescription(getMessage("command_info"));
        setUsage("/ch info §8[channel]");
        setArgumentRange(0, 1);
        setIdentifiers("ch info", "herochat info");
        setNotes("§cNote:§e If no channel is given, your active", "      channel is used.");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Chatter chatter = null;
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            chatter = Herochat.getChatterManager().getChatter(player);
        }
        Channel channel;
        if (args.length == 0) {
            if (chatter != null) {
                channel = chatter.getActiveChannel();
            } else {
                channel = Herochat.getChannelManager().getDefaultChannel();
            }
        } else {
            channel = Herochat.getChannelManager().getChannel(args[0]);
        }
        if (channel == null) {
            Messaging.send(sender, getMessage("info_noChannel"));
            return true;
        }
        if ((chatter != null) && (chatter.canViewInfo(channel) != Chatter.Result.ALLOWED)) {
            Messaging.send(sender, getMessage("info_noPermission"), channel.getColor() + channel.getName());
            return true;
        }
        sender.sendMessage(ChatColor.RED + "------------[ " + channel.getColor() + channel.getName() + ChatColor.RED + " ]------------");
        sender.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + channel.getName());
        sender.sendMessage(ChatColor.YELLOW + "Nick: " + ChatColor.WHITE + channel.getNick());
        sender.sendMessage(ChatColor.YELLOW + "Format: " + ChatColor.WHITE + channel.getFormat());
        if (!channel.getPassword().isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Password: " + ChatColor.WHITE + channel.getPassword());
        }
        if (channel.getDistance() > 0) {
            sender.sendMessage(ChatColor.YELLOW + "Distance: " + ChatColor.WHITE + channel.getDistance());
        }
        sender.sendMessage(ChatColor.YELLOW + "Shortcut Allowed: " + ChatColor.WHITE + (channel.isShortcutAllowed() ? "true" : "false"));

        return true;
    }
}
