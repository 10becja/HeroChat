package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Channel;
import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand
        extends BasicCommand {
    public BanCommand() {
        super("Ban");
        setDescription(getMessage("command_ban"));
        setUsage("/ch ban §8[channel] <player>");
        setArgumentRange(1, 2);
        setIdentifiers("ch ban", "herochat ban");
        setNotes("§cNote:§e If no channel is given, your active", "      channel is used.");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Chatter chatter = null;
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            chatter = Herochat.getChatterManager().getChatter(player);
        }
        Channel channel;
        if (args.length == 1) {
            if (chatter != null) {
                channel = chatter.getActiveChannel();
            } else {
                channel = Herochat.getChannelManager().getDefaultChannel();
            }
        } else {
            channel = Herochat.getChannelManager().getChannel(args[0]);
        }
        if (channel == null) {
            Messaging.send(sender, getMessage("ban_noChannel"));
            return true;
        }
        if ((chatter != null) && (chatter.canBan(channel) != Chatter.Result.ALLOWED)) {
            Messaging.send(sender, getMessage("ban_noPermission"), channel.getColor() + channel.getName());
            return true;
        }
        String targetName = args[(args.length - 1)];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }
        if (channel.isBanned(targetName)) {
            channel.setBanned(targetName, false);
            Messaging.send(sender, getMessage("ban_confirmUnban"), targetName, channel.getColor() + channel.getName());
            if (targetPlayer != null) {
                Messaging.send(targetPlayer, getMessage("ban_notifyUnban"), channel.getColor() + channel.getName());
            }
        } else {
            if (targetPlayer != null) {
                Chatter target = Herochat.getChatterManager().getChatter(targetPlayer);
                channel.banMember(target);
                if (target.getChannels().isEmpty()) {
                    Herochat.getChannelManager().getDefaultChannel().addMember(target, true, true);
                }
                if (channel.equals(target.getActiveChannel())) {
                    Channel focus = target.getChannels().iterator().next();
                    target.setActiveChannel(focus, true, true);
                }
            } else {
                channel.setBanned(targetName, true);
            }
            Messaging.send(sender, getMessage("ban_confirmBan"), targetName, channel.getColor() + channel.getName());
        }
        return true;
    }
}
