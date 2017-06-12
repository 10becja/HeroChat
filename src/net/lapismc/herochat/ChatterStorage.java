package net.lapismc.herochat;

public interface ChatterStorage {
    void flagUpdate(Chatter paramChatter);

    Chatter load(String paramString);

    void removeChatter(Chatter paramChatter);

    void update();

    void update(Chatter paramChatter);
}
