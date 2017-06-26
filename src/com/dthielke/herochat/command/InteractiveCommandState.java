package com.dthielke.herochat.command;

import org.bukkit.command.CommandSender;

public interface InteractiveCommandState {
    boolean execute(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString);

    int getMaxArguments();

    int getMinArguments();

    boolean isIdentifier(String paramString);
}
