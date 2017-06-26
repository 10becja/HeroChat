package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKCommand
        extends BasicCommand {
    public AFKCommand() {
        super("AFK");
        setDescription(getMessage("command_afk"));
        setUsage("/afk [message]");
        setArgumentRange(0, 2147483647);
        setIdentifiers("afk", "ch afk", "herochat afk");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Chatter chatter = Herochat.getChatterManager().getChatter(player);
        if (chatter.isAFK()) {
            chatter.setAFK(false);
            chatter.setAFKMessage("");
            Messaging.send(player, getMessage("afk_disable"));
        } else {
            chatter.setAFK(true);
            if (args.length >= 1) {
                StringBuilder msg = new StringBuilder();
                for (String arg : args) {
                    msg.append(arg).append(" ");
                }
                chatter.setAFKMessage(msg.toString().trim());
            }
            Messaging.send(player, getMessage("afk_enable"));
        }
        return true;
    }
}
