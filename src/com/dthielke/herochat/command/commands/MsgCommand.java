package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.*;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand
        extends BasicCommand {
    public MsgCommand() {
        super("Private Message");
        setDescription(getMessage("command_msg"));
        setUsage("/msg §8<player> [message]");
        setArgumentRange(0, 2147483647);
        setPermission("herochat.pm");
        setIdentifiers("msg", "w", "tell", "pm", "ch msg", "herochat msg");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                Messaging.send(sender, getMessage("console_noMessage"));
                return true;
            }
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target == null) {
                Messaging.send(sender, getMessage("msg_noPlayer"));
                return true;
            }
            StringBuilder msg = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                msg.append(args[i]).append(" ");
            }
            Messaging.send(target, ChatColor.GREEN + "<Console> --->" + ChatColor.WHITE + "  $1", msg.toString().trim());
            Messaging.send(sender, "----> $1: $2", target.getName(), msg.toString().trim());
            return true;
        }
        Player player = (Player) sender;
        Chatter playerChatter = Herochat.getChatterManager().getChatter(player);
        if (args.length == 0) {
            Channel currentChannel = playerChatter.getActiveChannel();
            if ((currentChannel != null) && (currentChannel.isTransient())) {
                Channel lastActiveChannel = playerChatter.getLastFocusableChannel();
                if (lastActiveChannel != null) {
                    playerChatter.setActiveChannel(lastActiveChannel, true, true);
                }
            }
            return true;
        }
        Player target = Bukkit.getServer().getPlayer(args[0]);
        if ((target == null) || (!player.canSee(target))) {
            Messaging.send(sender, getMessage("msg_noPlayer"));
            return true;
        }
        if (target.equals(player)) {
            Messaging.send(sender, getMessage("msg_selfMsg"));
            return true;
        }
        Chatter targetChatter = Herochat.getChatterManager().getChatter(target);
        ChannelManager channelManager = Herochat.getChannelManager();
        String channelName = "convo" + player.getName() + target.getName();
        if (!channelManager.hasChannel(channelName)) {
            Channel convo = new ConversationChannel(playerChatter, targetChatter, channelManager);
            channelManager.addChannel(convo);
        }
        Channel convo = channelManager.getChannel(channelName);
        if (args.length == 1) {
            playerChatter.setActiveChannel(convo, false, false);
            Messaging.send(player, getMessage("msg_confirm"), target.getName());
        } else {
            StringBuilder msg = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                msg.append(args[i]).append(" ");
            }
            Channel active = playerChatter.getActiveChannel();
            playerChatter.setActiveChannel(convo, false, false);
            Herochat.getMessageHandler().handle(player, msg.toString().trim(), "<%1$s> %2$s");
            playerChatter.setActiveChannel(active, false, false);
        }
        return true;
    }
}
