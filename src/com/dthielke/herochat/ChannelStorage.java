package com.dthielke.herochat;

import java.util.Set;

public interface ChannelStorage {
    void addChannel(Channel paramChannel);

    void flagUpdate(Channel paramChannel);

    Channel load(String paramString);

    Set<Channel> loadChannels();

    void removeChannel(Channel paramChannel);

    void update();

    void update(Channel paramChannel);
}
