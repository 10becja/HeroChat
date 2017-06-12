package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Channel;
import net.lapismc.herochat.ChannelManager;
import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand
        extends BasicCommand {
    public JoinCommand() {
        super("Join");
        setDescription(getMessage("command_join"));
        setUsage("/ch join §8<channel> [password]");
        setArgumentRange(1, 2);
        setIdentifiers("join", "ch join", "herochat join");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        ChannelManager channelMngr = Herochat.getChannelManager();
        Channel channel = channelMngr.getChannel(args[0]);
        if (channel == null) {
            Messaging.send(sender, getMessage("join_noChannel"));
            return true;
        }
        String password = "";
        if (args.length == 2) {
            password = args[1];
        }
        Chatter chatter = Herochat.getChatterManager().getChatter(player);
        Chatter.Result result = chatter.canJoin(channel, password);
        switch (result.ordinal())
        {
            case 1:
                Messaging.send(sender, getMessage("join_redundant"), channel.getColor() + channel.getName());
                return true;
            case 2:
                Messaging.send(sender, getMessage("join_noPermission"), channel.getColor() + channel.getName());
                return true;
            case 3:
                Messaging.send(sender, getMessage("join_banned"), channel.getColor() + channel.getName());
                return true;
            case 4:
                Messaging.send(sender, getMessage("join_badPassword"));
                return true;
        }
        channel.addMember(chatter, true, true);
        Messaging.send(player, getMessage("join_confirm"), channel.getColor() + channel.getName());
        return true;
    }
}
