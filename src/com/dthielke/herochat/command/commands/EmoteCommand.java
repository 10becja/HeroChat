package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.*;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmoteCommand
        extends BasicCommand {
    public EmoteCommand() {
        super("Emote");
        setDescription(getMessage("command_emote"));
        setUsage("/me [message]");
        setArgumentRange(1, 2147483647);
        setIdentifiers("me");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        StringBuilder msg = new StringBuilder();
        if ((sender instanceof Player)) {
            msg.append(((Player) sender).getDisplayName());
        } else {
            msg.append(sender.getName());
        }
        for (String arg : args) {
            msg.append(" ").append(arg);
        }
        if ((!Herochat.getChannelManager().isUsingEmotes()) || (!(sender instanceof Player))) {
            if (sender.hasPermission("herochat.emote")) {
                Bukkit.broadcastMessage("* " + msg);
            } else {
                Messaging.send(sender, getMessage("emote_noPermission"));
            }
            return true;
        }
        Player player = (Player) sender;
        Chatter chatter = Herochat.getChatterManager().getChatter(player);
        Channel channel = chatter.getActiveChannel();
        Chatter.Result result = chatter.canEmote(channel);
        ChannelChatEvent channelEvent = MessageHandler.throwChannelEvent(chatter, channel, result, msg.toString(), "", channel.getFormatSupplier().getEmoteFormat());
        result = channelEvent.getResult();
        switch (result.ordinal()) {
            case 1:
                try {
                    Messaging.send(player, Herochat.getMessage("messageHandler_noChannel"));
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_noChannel");
                }
            case 2:
                try {
                    Messaging.send(player, Herochat.getMessage("messageHandler_notInChannel"));
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_notInChannel");
                }
            case 3:
                try {
                    Messaging.send(player, Herochat.getMessage("messageHandler_muted"));
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_muted");
                }
            case 4:
                try {
                    Messaging.send(player, Herochat.getMessage("messageHandler_noPermission"), channel.getColor() + channel.getName());
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_noPermission");
                }
            case 5:
                try {
                    Messaging.send(player, Herochat.getMessage("messageHandler_badWorld"), channel.getColor() + channel.getName());
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_badWorld");
                }
        }
        if (result != Chatter.Result.ALLOWED) {
            return true;
        }
        channel.emote(chatter, msg.toString());
        return true;
    }
}
