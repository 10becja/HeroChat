package com.dthielke.herochat.command;

import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.MessageNotFoundException;
import org.bukkit.command.CommandSender;

public abstract class BasicCommand
        implements Command {
    private final String name;
    private String description = "";
    private String usage = "";
    private String permission = "";
    private String[] notes = new String[0];
    private String[] identifiers = new String[0];
    private int minArguments = 0;
    private int maxArguments = 0;

    public BasicCommand(String name) {
        this.name = name;
    }

    protected String getMessage(String key) {
        try {
            return Herochat.getMessage(key);
        } catch (MessageNotFoundException e) {
            Herochat.severe("Messages.properties is missing: " + key);
        }
        return "";
    }

    public void cancelInteraction(CommandSender executor) {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(String... identifiers) {
        this.identifiers = identifiers;
    }

    public int getMaxArguments() {
        return this.maxArguments;
    }

    public int getMinArguments() {
        return this.minArguments;
    }

    public String getName() {
        return this.name;
    }

    public String[] getNotes() {
        return this.notes;
    }

    protected void setNotes(String... notes) {
        this.notes = notes;
    }

    public String getPermission() {
        return this.permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getUsage() {
        return this.usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public boolean isIdentifier(CommandSender executor, String input) {
        for (String identifier : this.identifiers) {
            if (input.equalsIgnoreCase(identifier)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInProgress(CommandSender executor) {
        return false;
    }

    public boolean isInteractive() {
        return false;
    }

    public boolean isShownOnHelpMenu() {
        return true;
    }

    public void setArgumentRange(int min, int max) {
        this.minArguments = min;
        this.maxArguments = max;
    }
}
