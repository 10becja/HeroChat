package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Channel;
import net.lapismc.herochat.ChannelManager;
import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveCommand
        extends BasicCommand {
    public RemoveCommand() {
        super("Remove Channel");
        setDescription(getMessage("command_remove"));
        setUsage("/ch remove §8<channel>");
        setArgumentRange(1, 1);
        setIdentifiers("ch remove", "herochat remove");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        String name = args[0];

        ChannelManager channelMngr = Herochat.getChannelManager();
        Channel channel = channelMngr.getChannel(name);
        if (channel == null) {
            Messaging.send(sender, getMessage("remove_noChannel"));
            return true;
    }
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            Chatter chatter = Herochat.getChatterManager().getChatter(player);
            if (chatter.canRemove(channel) != Chatter.Result.ALLOWED) {
                Messaging.send(sender, getMessage("remove_noPermission"), channel.getColor() + channel.getName());
                return true;
            }
        }
        for (Chatter target : channel.getMembers()) {
            channel.kickMember(target);
            if (target.getChannels().isEmpty()) {
                Herochat.getChannelManager().getDefaultChannel().addMember(target, true, true);
            }
            if (channel.equals(target.getActiveChannel())) {
                Channel focus = target.getChannels().iterator().next();
                target.setActiveChannel(focus, true, true);
            }
        }
        channelMngr.removeChannel(channel);
        Messaging.send(sender, getMessage("remove_confirm"));
        return true;
    }
}
