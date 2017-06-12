package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Channel;
import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand
        extends BasicCommand {
    public KickCommand() {
        super("Kick");
        setDescription(getMessage("command_kick"));
        setUsage("/ch kick §8[channel] <player>");
        setArgumentRange(1, 2);
        setIdentifiers("ch kick", "herochat kick");
        setNotes("§cNote:§e If no channel is given, your active", "      channel is used.");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Channel channel = null;
        Chatter chatter = null;
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            chatter = Herochat.getChatterManager().getChatter(player);
            channel = chatter.getActiveChannel();
        }
        if (args.length == 1) {
            if (chatter != null) {
                channel = chatter.getActiveChannel();
            }
        } else {
            channel = Herochat.getChannelManager().getChannel(args[0]);
        }
        if (channel == null) {
            Messaging.send(sender, getMessage("kick_noChannel"));
            return true;
        }
        if ((chatter != null) && (chatter.canKick(channel) != Chatter.Result.ALLOWED)) {
            Messaging.send(sender, getMessage("kick_noPermission"), channel.getColor() + channel.getName());
            return true;
        }
        Player targetPlayer = Bukkit.getServer().getPlayer(args[(args.length - 1)]);
        if (targetPlayer == null) {
            Messaging.send(sender, getMessage("kick_noPlayer"));
            return true;
        }
        Chatter target = Herochat.getChatterManager().getChatter(targetPlayer);
        if (!target.hasChannel(channel)) {
            Messaging.send(sender, getMessage("kick_badPlayer"));
            return true;
    }
        channel.kickMember(target);
        Messaging.send(sender, getMessage("kick_confirm"), targetPlayer.getName(), channel.getColor() + channel.getName());
        Messaging.send(targetPlayer, getMessage("kick_notify"), channel.getColor() + channel.getName());
        if (target.getChannels().isEmpty()) {
            Herochat.getChannelManager().getDefaultChannel().addMember(target, true, true);
        }
        if (channel.equals(target.getActiveChannel())) {
            Channel focus = target.getChannels().iterator().next();
            target.setActiveChannel(focus, true, true);
        }
        return true;
    }
}
