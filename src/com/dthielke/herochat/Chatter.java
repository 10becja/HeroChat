package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface Chatter {
    boolean addChannel(Channel paramChannel, boolean paramBoolean1, boolean paramBoolean2);

    void attachStorage(ChatterStorage paramChatterStorage);

    Result canBan(Channel paramChannel);

    Result canColorMessages(Channel paramChannel, ChatColor paramChatColor);

    Result canEmote(Channel paramChannel);

    Result canFocus(Channel paramChannel);

    Result canJoin(Channel paramChannel, String paramString);

    Result canKick(Channel paramChannel);

    Result canLeave(Channel paramChannel);

    Result canModify(String paramString, Channel paramChannel);

    Result canMute(Channel paramChannel);

    Result canRemove(Channel paramChannel);

    Result canSpeak(Channel paramChannel);

    Result canViewInfo(Channel paramChannel);

    Result canIgnore(Chatter paramChatter);

    Channel getActiveChannel();

    String getAFKMessage();

    void setAFKMessage(String paramString);

    Set<Channel> getChannels();

    Set<String> getIgnores();

    Channel getLastActiveChannel();

    Channel getLastFocusableChannel();

    Chatter getLastPrivateMessageSource();

    void setLastPrivateMessageSource(Chatter paramChatter);

    String getName();

    UUID getuuid();

    Player getPlayer();

    OfflinePlayer getOfflinePlayer();

    ChatterStorage getStorage();

    boolean hasChannel(Channel paramChannel);

    boolean isAFK();

    void setAFK(boolean paramBoolean);

    boolean isIgnoring(Chatter paramChatter);

    boolean isIgnoring(String paramString);

    boolean isInRange(Chatter paramChatter, int paramInt);

    boolean isMuted();

    void removeChannel(Channel paramChannel, boolean paramBoolean1, boolean paramBoolean2);

    void setActiveChannel(Channel paramChannel, boolean paramBoolean1, boolean paramBoolean2);

    void setIgnore(String paramString, boolean paramBoolean1, boolean paramBoolean2);

    void setMuted(boolean paramBoolean1, boolean paramBoolean2);

    boolean shouldAutoJoin(Channel paramChannel);

    boolean shouldForceJoin(Channel paramChannel);

    boolean shouldForceLeave(Channel paramChannel);

    void refocus();

    void disconnect();

    enum Permission {
        JOIN("join"), LEAVE("leave"), SPEAK("speak"), EMOTE("emote"), KICK("kick"), BAN("ban"), MUTE("mute"), REMOVE("remove"), COLOR("color.all"), INFO("info"), FOCUS("focus"), AUTOJOIN("autojoin"), FORCE_JOIN("force.join"), FORCE_LEAVE("force.leave"), MODIFY_NICK("modify.nick"), MODIFY_COLOR("modify.color"), MODIFY_DISTANCE("modify.distance"), MODIFY_FORMAT("modify.format"), MODIFY_SHORTCUT("modify.shortcut"), MODIFY_PASSWORD("modify.password"), MODIFY_VERBOSE("modify.verbose"), MODIFY_FOCUSABLE("modify.focusable"), MODIFY_CROSSWORLD("modify.crossworld"), MODIFY_CHATCOST("modify.chatcost"), BLACK("color.black"), DARK_BLUE("color.dark_blue"), DARK_GREEN("color.dark_green"), DARK_AQUA("color.dark_aqua"), DARK_RED("color.dark_red"), DARK_PURPLE("color.dark_purple"), GOLD("color.gold"), GRAY("color.gray"), DARK_GRAY("color.dark_gray"), BLUE("color.blue"), GREEN("color.green"), AQUA("color.aqua"), RED("color.red"), LIGHT_PURPLE("color.light_purple"), YELLOW("color.yellow"), WHITE("color.white"), MAGIC("color.magic"), BOLD("color.bold"), STRIKETHROUGH("color.strikethrough"), UNDERLINE("color.underline"), ITALIC("color.italic"), RESET("color.reset");

        private String name;

        Permission(String name) {
            this.name = name;
        }

        public String form(Channel channel) {
            return "herochat." + this.name + "." + channel.getName();
        }

        public String formAll() {
            return "herochat." + this.name + ".all";
        }

        public String formWildcard() {
            return "herochat." + this.name + ".*";
        }

        public String toString() {
            return this.name;
        }
    }

    enum Result {
        NO_PERMISSION, NO_CHANNEL, INVALID, BANNED, MUTED, ALLOWED, BAD_PASSWORD;

        Result() {
        }
    }
}
