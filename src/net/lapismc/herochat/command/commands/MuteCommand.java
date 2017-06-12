package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Channel;
import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand
        extends BasicCommand {
    public MuteCommand() {
        super("Mute");
        setDescription(getMessage("command_mute"));
        setUsage("/ch mute §8[channel] <player>");
        setArgumentRange(1, 2);
        setIdentifiers("ch mute", "herochat mute");
        setNotes("§cNote:§e If no channel is given, user is", "      globally muted.");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Chatter chatter = null;
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            chatter = Herochat.getChatterManager().getChatter(player);
        }
        String targetName = args[(args.length - 1)];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }
        if (args.length == 2) {
            Channel channel = Herochat.getChannelManager().getChannel(args[0]);
            if (channel == null) {
                Messaging.send(sender, getMessage("mute_noChannel"));
                return true;
            }
            if ((chatter != null) && (chatter.canMute(channel) != Chatter.Result.ALLOWED)) {
                Messaging.send(sender, getMessage("mute_noPermission"));
                return true;
            }
            if (channel.isMuted(targetName)) {
                channel.setMuted(targetName, false);
                Messaging.send(sender, getMessage("mute_confirmUnmute"), targetName, channel.getColor() + channel.getName());
                if (targetPlayer != null) {
                    Messaging.send(targetPlayer, getMessage("mute_notifyUnmute"), channel.getColor() + channel.getName());
                }
            } else {
                channel.setMuted(targetName, true);
                Messaging.send(sender, getMessage("mute_confirmMute"), targetName, channel.getColor() + channel.getName());
                if (targetPlayer != null) {
                    Messaging.send(targetPlayer, getMessage("mute_notifyMute"), channel.getColor() + channel.getName());
                }
            }
        } else {
            if ((chatter != null) && (!chatter.getPlayer().hasPermission("herochat.mute"))) {
                Messaging.send(sender, getMessage("mute_noPermission"));
                return true;
            }
            if (targetPlayer == null) {
                Messaging.send(sender, getMessage("mute_noPlayer"));
                return true;
            }
            Chatter targetChatter = Herochat.getChatterManager().getChatter(targetPlayer);
            if (targetChatter.isMuted()) {
                targetChatter.setMuted(false, true);
                Messaging.send(sender, getMessage("mute_confirmGlobalUnmute"), targetPlayer.getName());
                Messaging.send(targetPlayer, getMessage("mute_notifyGlobalUnmute"));
            } else {
                targetChatter.setMuted(true, true);
                Messaging.send(sender, getMessage("mute_confirmGlobalMute"), targetPlayer.getName());
                Messaging.send(targetPlayer, getMessage("mute_notifyGlobalMute"));
            }
        }
        return true;
    }
}
