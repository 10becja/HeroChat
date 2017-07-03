package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;

public interface Channel {
    boolean addMember(Chatter paramChatter, boolean paramBoolean1, boolean paramBoolean2);

    void announce(String paramString);

    String applyFormat(String paramString1, String paramString2);

    String applyFormat(String paramString1, String paramString2, Player paramPlayer);

    void attachStorage(ChannelStorage paramChannelStorage);

    void banMember(Chatter paramChatter);

    void emote(Chatter paramChatter, String paramString);

    Set<String> getBans();

    void setBans(Set<String> paramSet);

    ChatColor getColor();

    void setColor(ChatColor paramChatColor);

    int getDistance();

    void setDistance(int paramInt);

    String getFormat();

    void setFormat(String paramString);

    Set<String> getWorlds();

    boolean isCrossWorld();

    Set<Chatter> getMembers();

    Set<String> getModerators();

    void setModerators(Set<String> paramSet);

    Set<String> getMutes();

    void setMutes(Set<String> paramSet);

    String getName();

    String getNick();

    void setNick(String paramString);

    String getPassword();

    void setPassword(String paramString);

    ChannelStorage getStorage();


    boolean isBanned(String paramString);

    boolean isHidden();

    boolean isLocal();

    boolean isMember(Chatter paramChatter);

    boolean isModerator(String paramString);

    boolean isMuted(String paramString);

    boolean isShortcutAllowed();

    void setShortcutAllowed(boolean paramBoolean);

    boolean isTransient();

    boolean isVerbose();

    void setVerbose(boolean paramBoolean);

    void kickMember(Chatter paramChatter);

    void onFocusGain(Chatter paramChatter);

    void onFocusLoss(Chatter paramChatter);

    void processChat(ChannelChatEvent paramChannelChatEvent);

    boolean removeMember(Chatter paramChatter, boolean paramBoolean1, boolean paramBoolean2);

    void setBanned(String paramString, boolean paramBoolean);

    void setModerator(String paramString, boolean paramBoolean);

    void setMuted(String paramString, boolean paramBoolean);

    boolean isMuted();

    void setMuted(boolean paramBoolean);

    void sendRawMessage(String paramString);

    MessageFormatSupplier getFormatSupplier();
}
