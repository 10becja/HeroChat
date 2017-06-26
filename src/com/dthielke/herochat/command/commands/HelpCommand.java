package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.command.Command;
import com.dthielke.herochat.command.CommandHandler;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand
        extends BasicCommand {
    private static final int CMDS_PER_PAGE = 8;

    public HelpCommand() {
        super("Help");
        setDescription(getMessage("command_help"));
        setUsage("/ch help §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("ch help", "herochat help");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException ignored) {
            }
        }
        List<Command> sortCommands = Herochat.getCommandHandler().getCommands();
        List<Command> commands = new ArrayList<>();
        for (Command command : sortCommands) {
            if ((command.isShownOnHelpMenu()) &&
                    (CommandHandler.hasPermission(sender, command.getPermission()))) {
                commands.add(command);
            }
        }
        int numPages = commands.size() / 8;
        if (commands.size() % 8 != 0) {
            numPages++;
        }
        if (numPages == 0) {
            numPages = 1;
        }
        if ((page >= numPages) || (page < 0)) {
            page = 0;
        }
        sender.sendMessage("§c-----[ §fHerochat Help <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * 8;
        int end = start + 8;
        if (end > commands.size()) {
            end = commands.size();
        }
        for (int c = start; c < end; c++) {
            Command cmd = commands.get(c);
            sender.sendMessage("  §a" + cmd.getUsage());
        }
        Messaging.send(sender, getMessage("help_moreInfo"), getMessage("help_infoCommand"));
        return true;
    }
}
