package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Channel;
import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListCommand
        extends BasicCommand {
    private static final int CHANNELS_PER_PAGE = 8;

    public ListCommand() {
        super("List");
        setDescription(getMessage("command_list"));
        setUsage("/ch list §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("ch list", "herochat list");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException ignored) {
            }
        }
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : Herochat.getChannelManager().getChannels()) {
            if ((!channel.isHidden()) && (Herochat.hasChannelPermission(sender, channel, Chatter.Permission.JOIN))) {
                channels.add(channel);
            }
        }
        Chatter chatter = null;
        if ((sender instanceof Player)) {
            chatter = Herochat.getChatterManager().getChatter((Player) sender);
        }
        int numPages = channels.size() / 8;
        if (channels.size() % 8 != 0) {
            numPages++;
        }
        if (numPages == 0) {
            numPages = 1;
        }
        if ((page >= numPages) || (page < 0)) {
            page = 0;
    }
        sender.sendMessage("§c-----[ §fHerochat Channels <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * 8;
        int end = start + 8;
        if (end > channels.size()) {
            end = channels.size();
        }
        for (int c = start; c < end; c++) {
            Channel channel = channels.get(c);
            String line = channel.getColor() + "  [" + channel.getNick() + "] " + channel.getName();
            if ((chatter != null) && (channel.isMember(chatter))) {
                line = line + "*";
            }
            sender.sendMessage(line);
    }
        return true;
    }
}
