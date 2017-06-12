package net.lapismc.herochat.command.commands;

import net.lapismc.herochat.Chatter;
import net.lapismc.herochat.Herochat;
import net.lapismc.herochat.command.BasicCommand;
import net.lapismc.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand
        extends BasicCommand {
    public IgnoreCommand() {
        super("Ignore");
        setDescription(getMessage("command_ignore"));
        setUsage("/ch ignore §8[player]");
        setArgumentRange(0, 1);
        setIdentifiers("ignore", "ch ignore", "herochat ignore");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Chatter chatter = Herochat.getChatterManager().getChatter(player);
        if (args.length == 0) {
            StringBuilder msg = new StringBuilder(getMessage("ignore_listHead"));
            if (chatter.getIgnores().isEmpty()) {
                msg.append(" ").append(getMessage("ignore_listEmpty"));
            } else {
                for (String name : chatter.getIgnores()) {
                    msg.append(" ").append(name);
                }
            }
            Messaging.send(sender, msg.toString());
        } else {
            String targetName = args[(args.length - 1)];
            OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(targetName);
            if (targetPlayer == null) {
                Messaging.send(sender, getMessage("ignore_noPlayer"), targetName);
                return true;
            }
            if (chatter.isIgnoring(targetName)) {
                chatter.setIgnore(targetName, false, true);
                Messaging.send(sender, getMessage("ignore_confirmUnignore"), targetName);
            } else {
                chatter.setIgnore(targetName, true, true);
                Messaging.send(sender, getMessage("ignore_confirmIgnore"), targetName);
            }
    }
        return true;
    }
}
