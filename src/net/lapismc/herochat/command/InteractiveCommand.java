package net.lapismc.herochat.command;

import org.bukkit.command.CommandSender;

public interface InteractiveCommand
        extends Command {
    String getCancelIdentifier();

    void onCommandCancelled(CommandSender paramCommandSender);
}
