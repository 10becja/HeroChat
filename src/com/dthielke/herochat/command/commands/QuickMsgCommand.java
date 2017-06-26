package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuickMsgCommand
        extends BasicCommand {
    public QuickMsgCommand() {
        super("Quick Message");
        setDescription(getMessage("command_qmsg"));
        setUsage("/ch qm §8<channel> <message>");
        setArgumentRange(2, 2147483647);
        setIdentifiers("ch qm", "herochat qm");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            Channel channel = Herochat.getChannelManager().getChannel(args[0]);
            if (channel == null) {
                Messaging.send(sender, getMessage("quickmsg_noChannel"));
                return true;
            }
            StringBuilder msg = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                msg.append(args[i]).append(" ");
            }
            channel.announce(msg.toString());
            sender.sendMessage("Announcement sent to " + channel.getName());
            return true;
        }
        Player player = (Player) sender;
        Chatter chatter = Herochat.getChatterManager().getChatter(player);
        Channel channel = Herochat.getChannelManager().getChannel(args[0]);
        if (channel == null) {
            Messaging.send(sender, getMessage("quickmsg_noChannel"));
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            msg.append(args[i]).append(" ");
        }
        Channel active = chatter.getActiveChannel();

        chatter.setActiveChannel(channel, false, false);
        Herochat.getMessageHandler().handle(player, msg.toString().trim(), "<%1$s> %2$s");
        chatter.setActiveChannel(active, false, false);
        return true;
    }
}
