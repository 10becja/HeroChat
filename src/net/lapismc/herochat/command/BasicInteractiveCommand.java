package net.lapismc.herochat.command;

import net.lapismc.herochat.util.Messaging;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public abstract class BasicInteractiveCommand
        extends BasicCommand
        implements InteractiveCommand {
    private InteractiveCommandState[] states = new InteractiveCommandState[0];
    private Map<CommandSender, Integer> userStates = new HashMap<>();

    public BasicInteractiveCommand(String name) {
        super(name);
    }

    public final void cancelInteraction(CommandSender executor) {
        this.userStates.remove(executor);
        onCommandCancelled(executor);
    }

    public final boolean execute(CommandSender executor, String identifier, String[] args) {
        if (this.states.length == 0) {
            throw new IllegalArgumentException("An interactive command must have at least one state.");
        }
        int stateIndex = 0;
        if (this.userStates.containsKey(executor)) {
            stateIndex = this.userStates.get(executor);
        }
        InteractiveCommandState state = this.states[stateIndex];
        if ((stateIndex > 0) &&
                (getCancelIdentifier().equalsIgnoreCase(identifier))) {
            Messaging.send(executor, "Exiting command.");
            this.userStates.remove(executor);
            onCommandCancelled(executor);
            return true;
        }
        if ((args.length < state.getMinArguments()) || (args.length > state.getMaxArguments()) || (!state.execute(executor, identifier, args))) {
            if (stateIndex > 0) {
                Messaging.send(executor, "Invalid input - try again or type $1 to exit.", "/" + getCancelIdentifier());
            }
        } else {
            stateIndex++;
            if (this.states.length > stateIndex) {
                this.userStates.put(executor, stateIndex++);
            } else {
                this.userStates.remove(executor);
            }
        }
        return true;
    }

    public final boolean isIdentifier(CommandSender executor, String input) {
        int stateIndex = 0;
        if (this.userStates.containsKey(executor)) {
            stateIndex = this.userStates.get(executor);
        }
        if ((stateIndex > 0) &&
                (getCancelIdentifier().equalsIgnoreCase(input))) {
            return true;
        }
        InteractiveCommandState state = this.states[stateIndex];
        return state.isIdentifier(input);
    }

    public final boolean isInProgress(CommandSender executor) {
        return this.userStates.containsKey(executor);
    }

    public final boolean isInteractive() {
        return true;
    }

    public final void setArgumentRange(int min, int max) {
    }

    public final void setIdentifiers(String... identifiers) {
    }

    public final void setStates(InteractiveCommandState... states) {
        if (states.length == 0) {
            throw new IllegalArgumentException("An interactive command must have at least one state.");
        }
        this.states = states;
        super.setArgumentRange(states[0].getMinArguments(), states[0].getMaxArguments());
    }
}
