package net.lapismc.herochat.command;

public abstract class BasicInteractiveCommandState
        implements InteractiveCommandState {
    private String[] identifiers;
    private int minArguments = 0;
    private int maxArguments = 0;

    public BasicInteractiveCommandState(String... identifiers) {
        this.identifiers = identifiers;
    }

    public int getMaxArguments() {
        return this.maxArguments;
    }

    public int getMinArguments() {
        return this.minArguments;
    }

    public boolean isIdentifier(String input) {
        for (String ident : this.identifiers) {
            if (input.equalsIgnoreCase(ident)) {
                return true;
            }
        }
        return false;
    }

    public void setArgumentRange(int min, int max) {
        this.minArguments = min;
        this.maxArguments = max;
    }
}
