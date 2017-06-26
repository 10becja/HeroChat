package com.dthielke.herochat;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChatterManager {
    private Map<String, Chatter> chatters = new HashMap<>();
    private ChatterStorage storage;

    public void addChatter(Player player) {
        String name = player.getName().toLowerCase();
        Chatter chatter = this.chatters.get(name);
        if (chatter != null) {
            return;
        }
        chatter = this.storage.load(player.getName());
        if (chatter == null) {
            Herochat.severe("Null chatter for: " + player.getName() + " was detected, wiping all player info and attempting to load bogus chatter.");
            chatter = new StandardChatter(this.storage, player);
            this.chatters.put(name, chatter);
            this.storage.flagUpdate(chatter);
        } else {
            this.chatters.put(name, chatter);
        }
    }

    public void clear() {
        this.chatters.clear();
        this.storage = null;
    }

    boolean hasChatter(Player player) {
        return this.chatters.containsKey(player.getName().toLowerCase());
    }

    public Chatter getChatter(Player player) {
        return this.chatters.get(player.getName().toLowerCase());
    }

    public Chatter getChatter(String name) {
        return this.chatters.get(name.toLowerCase());
    }

    public Collection<Chatter> getChatters() {
        return this.chatters.values();
    }

    public ChatterStorage getStorage() {
        return this.storage;
    }

    void setStorage(ChatterStorage storage) {
        this.storage = storage;
    }

    private void removeChatter(Chatter chatter) {
        this.storage.removeChatter(chatter);
        chatter.disconnect();
        String name = chatter.getPlayer().getName().toLowerCase();
        this.chatters.remove(name);
    }

    void removeChatter(Player player) {
        removeChatter(getChatter(player));
    }

    public void reset() {
        this.chatters.clear();
    }
}
