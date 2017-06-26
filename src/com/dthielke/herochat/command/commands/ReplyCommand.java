package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.*;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand
        extends BasicCommand {
    public ReplyCommand() {
        super("Reply");
        setDescription(getMessage("command_remove"));
        setUsage("/r [message]");
        setArgumentRange(0, 2147483647);
        setIdentifiers("reply", "r", "ch reply", "herochat reply");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Chatter playerChatter = Herochat.getChatterManager().getChatter(player);
        Chatter storedTargetChatter = playerChatter.getLastPrivateMessageSource();
        if (storedTargetChatter == null) {
            Messaging.send(sender, getMessage("reply_noMessages"));
            return true;
        }
        Player target = Bukkit.getPlayer(storedTargetChatter.getName());
        if (target == null) {
            Messaging.send(sender, getMessage("reply_noPlayer"));
            return true;
        }
        if (target.equals(player)) {
            Messaging.send(sender, getMessage("reply_selfReply"));
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
        if (args.length == 0) {
            playerChatter.setActiveChannel(convo, false, true);
            Messaging.send(player, getMessage("reply_confirm"), target.getName());
        } else {
            StringBuilder msg = new StringBuilder();
            for (String arg : args) {
                msg.append(arg).append(" ");
            }
            Channel active = playerChatter.getActiveChannel();
            playerChatter.setActiveChannel(convo, false, false);
            Herochat.getMessageHandler().handle(player, msg.toString().trim(), "<%1$s> %2$s");
            playerChatter.setActiveChannel(active, false, false);
        }
        return true;
    }
}
