package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand
        extends BasicCommand {
    public SetCommand() {
        super("Set Channel Setting");
        setDescription(getMessage("command_set"));
        setUsage("/ch set §8<channel> <setting> <value>");
        setArgumentRange(3, 3);
        setIdentifiers("ch set", "herochat set");
        setNotes("§cSettings:§e nick, format, password, distance, color, shortcut, verbose, crossworld, chatcost");
        setNotes("§cNote:§e setting the password to 'none' clears it");
    }

    private static boolean argToBoolean(String arg) {
        arg = arg.toLowerCase();
        return (arg.equals("1")) || (arg.equals("t")) || (arg.equals("true")) || (arg.equals("y")) || (arg.equals("yes")) || (arg.equals("on"));
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        String name = args[0];
        String setting = args[1].toLowerCase();
        String value = args[2];

        ChannelManager channelMngr = Herochat.getChannelManager();
        Channel channel = channelMngr.getChannel(name);
        if (channel == null) {
            Messaging.send(sender, getMessage("set_noChannel"));
            return true;
        }
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            Chatter chatter = Herochat.getChatterManager().getChatter(player);
            if (chatter.canModify(setting, channel) != Chatter.Result.ALLOWED) {
                Messaging.send(sender, getMessage("set_noPermission"), setting, channel.getColor() + channel.getName());
                return true;
            }
        }
        switch (setting) {
            case "nick":
                if (channelMngr.hasChannel(value)) {
                    Messaging.send(sender, getMessage("set_identifierTaken"));
                } else {
                    channel.setNick(value);
                    channelMngr.removeChannel(channel);
                    channelMngr.addChannel(channel);
                    Messaging.send(sender, getMessage("set_confirmNick"));
                }
                break;
            case "format":
                channel.setFormat(value);
                Messaging.send(sender, getMessage("set_confirmFormat"));
                break;
            case "password":
                if (setting.equals("none")) {
                    channel.setPassword(null);
                } else {
                    channel.setPassword(value);
                }
                Messaging.send(sender, getMessage("set_confirmPassword"));
                break;
            case "distance":
                try {
                    int distance = Integer.parseInt(value);
                    channel.setDistance(distance);
                    Messaging.send(sender, getMessage("set_confirmDistance"));
                } catch (NumberFormatException e) {
                    Messaging.send(sender, getMessage("set_badDistance"));
                }
                break;
            case "color":
                ChatColor color = Messaging.parseColor(value);
                if (color == null) {
                    Messaging.send(sender, getMessage("set_badColor"));
                } else {
                    channel.setColor(color);
                    Messaging.send(sender, getMessage("set_confirmColor"));
                }
                break;
            case "shortcut":
                if (argToBoolean(value)) {
                    channel.setShortcutAllowed(true);
                    Messaging.send(sender, getMessage("set_enableQuickmsg"));
                } else {
                    channel.setShortcutAllowed(false);
                    Messaging.send(sender, getMessage("set_disableQuickmsg"));
                }
                break;
            case "verbose":
                if (argToBoolean(value)) {
                    channel.setVerbose(true);
                    Messaging.send(sender, getMessage("set_enableVerbose"));
                } else {
                    channel.setVerbose(false);
                    Messaging.send(sender, getMessage("set_disableVerbose"));
                }
                break;
            case "muted":
                if (argToBoolean(value)) {
                    channel.announce(getMessage("set_enableMute"));
                    channel.setMuted(true);
                    Messaging.send(sender, getMessage("set_enableMute"));
                } else {
                    channel.announce(getMessage("set_disableMute"));
                    channel.setMuted(false);
                    Messaging.send(sender, getMessage("set_disableMute"));
                }
                break;
        }
        return true;
    }
}
