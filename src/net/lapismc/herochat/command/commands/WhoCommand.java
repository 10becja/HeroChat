package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Channel;
import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhoCommand
        extends BasicCommand {
    public WhoCommand() {
        super("Who");
        setDescription(getMessage("command_who"));
        setUsage("/ch who §8[channel]");
        setArgumentRange(0, 1);
        setIdentifiers("ch who", "herochat who");
        setNotes("§cNote:§e If no channel is given, your active", "      channel is used.");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Chatter chatter = null;
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            chatter = Herochat.getChatterManager().getChatter(player);
        }
        Channel channel;
        if (args.length == 0) {
            if (chatter != null) {
                channel = chatter.getActiveChannel();
            } else {
                channel = Herochat.getChannelManager().getDefaultChannel();
            }
        } else {
            channel = Herochat.getChannelManager().getChannel(args[0]);
        }
        if (channel == null) {
            Messaging.send(sender, getMessage("who_noChannel"));
            return true;
        }
        List<String> names = new ArrayList<>();
        for (Chatter member : channel.getMembers()) {
            if ((!(sender instanceof Player)) || (((Player) sender).canSee(member.getPlayer()))) {
                names.add(member.getPlayer().getName());
            }
        }
        names.sort(String::compareToIgnoreCase);
        sender.sendMessage(ChatColor.RED + "------------[ " + channel.getColor() + channel.getName() + ChatColor.RED + " ]------------");
        int count = names.size();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String name = names.get(i);
            if (channel.isMuted(name)) {
                name = ChatColor.RED + name + ChatColor.WHITE;
            } else if (channel.isModerator(name)) {
                name = ChatColor.GREEN + name + ChatColor.WHITE;
            }
            if (i + 1 < count) {
                name = name + ", ";
            }
            if (line.length() + name.length() > 64) {
                sender.sendMessage(line.toString().trim());
                line = new StringBuilder();
                i--;
            } else {
                line.append(name);
                if (i + 1 == count) {
                    sender.sendMessage(line.toString().trim());
                }
            }
        }
        return true;
    }
}
