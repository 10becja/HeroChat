package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand
        extends BasicCommand {
    public LeaveCommand() {
        super("Leave");
        setDescription(getMessage("command_leave"));
        setUsage("/ch leave §8<channel>");
        setArgumentRange(1, 1);
        setIdentifiers("leave", "ch leave", "herochat leave");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        ChannelManager channelMngr = Herochat.getChannelManager();
        Channel channel = channelMngr.getChannel(args[0]);
        if (channel == null) {
            Messaging.send(sender, getMessage("leave_noChannel"));
            return true;
        }
        Chatter chatter = Herochat.getChatterManager().getChatter(player);
        Chatter.Result result = chatter.canLeave(channel);
        switch (result.ordinal()) {
            case 1:
                Messaging.send(sender, getMessage("leave_badChannel"), channel.getColor() + channel.getName());
                return true;
            case 2:
                Messaging.send(sender, getMessage("leave_noPermission"), channel.getColor() + channel.getName());
                return true;
        }
        int channelCount = chatter.getChannels().size();
        if (channelCount == 1) {
            Messaging.send(sender, getMessage("leave_lastChannel"));
            return true;
        }
        channel.removeMember(chatter, true, true);
        Messaging.send(player, getMessage("leave_confirm"), channel.getColor() + channel.getName());
        if ((chatter.getActiveChannel() == null) || (chatter.getActiveChannel().equals(channel))) {
            Channel newFocus = chatter.getLastFocusableChannel();
            if ((newFocus == null) || (newFocus.equals(channel))) {
                newFocus = channelMngr.getDefaultChannel();
            }
            chatter.setActiveChannel(newFocus, true, true);
        }
        return true;
    }
}
