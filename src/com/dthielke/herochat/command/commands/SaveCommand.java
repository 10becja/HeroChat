package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.command.CommandSender;

public class SaveCommand
        extends BasicCommand {
    public SaveCommand() {
        super("Save");
        setDescription(getMessage("command_save"));
        setUsage("/ch save");
        setArgumentRange(0, 0);
        setIdentifiers("ch save", "herochat save");
        setPermission("herochat.save");
    }

    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Herochat.getChannelManager().getStorage().update();
        Herochat.getChatterManager().getStorage().update();
        Messaging.send(sender, getMessage("save_confirm"));
        return true;
    }
}
