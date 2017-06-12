package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Channel;
import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModCommand
        extends BasicCommand {
    public ModCommand() {
        super("Mod");
        setDescription(getMessage("command_mod"));
        setUsage("/ch mod §8[channel] <player>");
        setArgumentRange(1, 2);
        setIdentifiers("ch mod", "herochat mod");
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
            Messaging.send(sender, getMessage("mod_noChannel"));
            return true;
        }
        if ((!sender.hasPermission("herochat.mod")) && ((chatter == null) || (!channel.isModerator(chatter.getPlayer().getName())))) {
            Messaging.send(sender, getMessage("mod_noPermission"), channel.getColor() + channel.getName());
            return true;
    }
        String targetName = args[(args.length - 1)];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetName);
        if (channel.isModerator(targetName)) {
            channel.setModerator(targetName, false);
            Messaging.send(sender, getMessage("mod_confirmUnmod"), channel.getColor() + channel.getName(), targetName);
            if (targetPlayer != null) {
                Messaging.send(targetPlayer, getMessage("mod_notifyUnmod"), channel.getColor() + channel.getName());
            }
        } else {
            channel.setModerator(targetName, true);
            Messaging.send(sender, getMessage("mod_confirmMod"), channel.getColor() + channel.getName(), targetName);
            if (targetPlayer != null) {
                Messaging.send(targetPlayer, getMessage("mod_notifyMod"), channel.getColor() + channel.getName());
            }
        }
        return true;
    }
}
