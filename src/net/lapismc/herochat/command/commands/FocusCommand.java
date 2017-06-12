package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.*;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FocusCommand
        extends BasicCommand {
    public FocusCommand() {
        super("Focus");
        setDescription(getMessage("command_focus"));
        setUsage("/ch §8<channel> [password]");
        setArgumentRange(1, 2);
        setIdentifiers("ch", "herochat");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        ChannelManager channelMngr = Herochat.getChannelManager();
        Channel channel = channelMngr.getChannel(args[0]);
        if (channel == null) {
            Messaging.send(sender, getMessage("focus_noChannel"));
            return true;
        }
        String password = "";
        if (args.length == 2) {
            password = args[1];
        }
        Chatter chatter = Herochat.getChatterManager().getChatter(player);
        if (chatter.canFocus(channel) == Chatter.Result.NO_PERMISSION) {
            Messaging.send(sender, getMessage("focus_noPermission"), channel.getColor() + channel.getName());
            return true;
        }
        if (!chatter.hasChannel(channel)) {
            Chatter.Result result = chatter.canJoin(channel, password);
            switch (result.ordinal())
            {
                case 1:
                    Messaging.send(sender, getMessage("join_noPermission"), channel.getColor() + channel.getName());
                    return true;
                case 2:
                    Messaging.send(sender, getMessage("focus_banned"), channel.getColor() + channel.getName());
                    return true;
                case 3:
                    Messaging.send(sender, getMessage("focus_badPassword"));
                    return true;
            }
            channel.addMember(chatter, true, true);
            Messaging.send(player, getMessage("focus_confirm"), channel.getColor() + channel.getName());
        }
        chatter.setActiveChannel(channel, true, true);


        Channel lastChannel = chatter.getLastActiveChannel();
        if ((lastChannel instanceof ConversationChannel)) {
            for (Chatter otherChatter : lastChannel.getMembers()) {
                if ((!otherChatter.equals(chatter)) &&
                        (!otherChatter.getActiveChannel().equals(lastChannel))) {
                    lastChannel.removeMember(chatter, false, true);
                    lastChannel.removeMember(otherChatter, false, true);
                }
            }
        }
        return true;
    }
}
