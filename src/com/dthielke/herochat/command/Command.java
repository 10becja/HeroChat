package com.dthielke.herochat.command;

import org.bukkit.command.CommandSender;

public interface Command {
    void cancelInteraction(CommandSender paramCommandSender);

    boolean execute(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString);

    String getDescription();

    String[] getIdentifiers();

    int getMaxArguments();

    int getMinArguments();

    String getName();

    String[] getNotes();

    String getPermission();

    String getUsage();

    boolean isIdentifier(CommandSender paramCommandSender, String paramString);

    boolean isInProgress(CommandSender paramCommandSender);

    boolean isInteractive();

    boolean isShownOnHelpMenu();
}
