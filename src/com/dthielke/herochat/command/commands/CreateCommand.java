package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.StandardChannel;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand
        extends BasicCommand {
    public CreateCommand() {
        super("Create Channel");
        setDescription(getMessage("command_create"));
        setUsage("/ch create §8<name> [nick]");
        setArgumentRange(1, 2);
        setIdentifiers("ch create", "herochat create");
        setPermission("herochat.create");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        String name = args[0];
        ChannelManager channelMngr = Herochat.getChannelManager();
        if (channelMngr.hasChannel(name)) {
            Messaging.send(sender, getMessage("create_nameTaken"));
            return true;
        }
        if (!name.matches("[a-zA-Z0-9]+")) {
            Messaging.send(sender, getMessage("create_nameInvalid"));
            return true;
        }
        String nick;
        if (args.length == 2) {
            nick = args[1];
            if (!nick.matches("[a-zA-Z0-9]+")) {
                Messaging.send(sender, getMessage("create_nickInvalid"));
                return true;
            }
        } else {
            nick = name;
            for (int i = 0; i < name.length(); i++) {
                nick = name.substring(0, i + 1);
                if (!channelMngr.hasChannel(nick)) {
                    break;
                }
            }
        }
        if (channelMngr.hasChannel(nick)) {
            Messaging.send(sender, "create_nickTaken");
            return true;
        }
        Channel channel = new StandardChannel(channelMngr.getStorage(), name, nick, channelMngr);
        if ((sender instanceof Player)) {
            channel.setModerator(sender.getName(), true);
        }
        channelMngr.addChannel(channel);
        channelMngr.getStorage().update(channel);
        Messaging.send(sender, getMessage("create_confirm"));

        return true;
    }
}
