package com.dthielke.herochat;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandardChannel
        implements Channel {
    private static final Pattern msgPattern = Pattern.compile("(.*)<(.*)%1\\$s(.*)> %2\\$s");
    private final String name;
    private final MessageFormatSupplier formatSupplier;
    private String nick;
    private String format;
    private String password;
    private ChatColor color;
    private int distance;
    private boolean shortcutAllowed;
    private boolean verbose;
    private boolean muted;
    private Set<Chatter> members = new HashSet<>();
    private Set<String> bans = new HashSet<>();
    private Set<String> mutes = new HashSet<>();
    private Set<String> moderators = new HashSet<>();
    private ChannelStorage storage;

    public StandardChannel(ChannelStorage storage, String name, String nick, MessageFormatSupplier formatSupplier) {
        this.storage = storage;
        this.name = name;
        this.nick = nick;
        this.color = ChatColor.WHITE;
        this.distance = 0;
        this.shortcutAllowed = false;
        this.verbose = true;
        this.format = "{default}";
        this.password = "";
        this.formatSupplier = formatSupplier;
        this.muted = false;
    }

    public boolean addMember(Chatter chatter, boolean announce, boolean flagUpdate) {
        if (this.members.contains(chatter)) {
            return false;
        }
        if ((announce) && (this.verbose)) {
            try {
                announce(Herochat.getMessage("channel_join").replace("$1", chatter.getPlayer().getDisplayName()));
            } catch (MessageNotFoundException e) {
                Herochat.severe("Messages.properties is missing: channel_join");
            }
        }
        this.members.add(chatter);
        if (!chatter.hasChannel(this)) {
            chatter.addChannel(this, announce, flagUpdate);
        }
        return true;
    }

    public void announce(String message) {
        String colorized = message.replaceAll("(?i)&([a-fklmno0-9])", "§$1");
        message = applyFormat(this.formatSupplier.getAnnounceFormat(), "").replace("%2$s", colorized);
        for (Chatter member : this.members) {
            member.getPlayer().sendMessage(message);
        }
        Herochat.logChat(ChatColor.stripColor(message));
    }

    public void sendRawMessage(String message) {
        for (Chatter member : this.members) {
            member.getPlayer().sendMessage(message);
        }
    }

    public String applyFormat(String format, String originalFormat) {
        format = format.replace("{default}", this.formatSupplier.getStandardFormat());
        format = format.replace("{name}", this.name);
        format = format.replace("{nick}", this.nick);
        format = format.replace("{color}", this.color.toString());
        format = format.replace("{msg}", "%2$s");


        Matcher matcher = msgPattern.matcher(originalFormat);
        if ((matcher.matches()) && (matcher.groupCount() == 3)) {
            format = format.replace("{sender}", matcher.group(1) + matcher.group(2) + "%1$s" + matcher.group(3));
        } else {
            format = format.replace("{sender}", "%1$s");
        }
        format = format.replaceAll("(?i)&([a-fklmnor0-9])", "§$1");
        return format;
    }

    public String applyFormat(String format, String originalFormat, Player sender) {
        format = applyFormat(format, originalFormat);
        format = format.replace("{plainsender}", sender.getName());
        Chat chat = Herochat.getChatService();
        if (chat != null) {
            try {
                String prefix = chat.getPlayerPrefix(sender);
                if ((prefix == null) || prefix.equals("")) {
                    prefix = chat.getPlayerPrefix((String) null, sender.getName());
                }
                String suffix = chat.getPlayerSuffix(sender);
                if ((suffix == null) || suffix.equals("")) {
                    suffix = chat.getPlayerSuffix((String) null, sender.getName());
                }
                String group = chat.getPrimaryGroup(sender);
                String groupPrefix = group == null ? "" : chat.getGroupPrefix(sender.getWorld(), group);
                if ((group != null) && ((groupPrefix == null) || (groupPrefix.equals("")))) {
                    groupPrefix = chat.getGroupPrefix((String) null, group);
                }
                String groupSuffix = group == null ? "" : chat.getGroupSuffix(sender.getWorld(), group);
                if ((group != null) && ((groupSuffix == null) || (groupSuffix.equals("")))) {
                    groupSuffix = chat.getGroupSuffix((String) null, group);
                }
                format = format.replace("{prefix}", prefix == null ? "" : prefix.replace("%", "%%"));
                format = format.replace("{suffix}", suffix == null ? "" : suffix.replace("%", "%%"));
                format = format.replace("{group}", group == null ? "" : group.replace("%", "%%"));
                format = format.replace("{groupprefix}", groupPrefix == null ? "" : groupPrefix.replace("%", "%%"));
                format = format.replace("{groupsuffix}", groupSuffix == null ? "" : groupSuffix.replace("%", "%%"));
            } catch (UnsupportedOperationException ignored) {
            }
        } else {
            format = format.replace("{prefix}", "");
            format = format.replace("{suffix}", "");
            format = format.replace("{group}", "");
            format = format.replace("{groupprefix}", "");
            format = format.replace("{groupsuffix}", "");
        }
        format = format.replaceAll("(?i)&([a-fklmno0-9])", "§$1");
        return format;
    }

    public void attachStorage(ChannelStorage storage) {
        this.storage = storage;
    }

    public void banMember(Chatter chatter) {
        if (!this.members.contains(chatter)) {
            return;
        }
        removeMember(chatter, false, true);
        setBanned(chatter.getPlayer().getName(), true);
        try {
            announce(Herochat.getMessage("channel_ban").replace("$1", chatter.getPlayer().getDisplayName()));
        } catch (MessageNotFoundException e) {
            Herochat.severe("Messages.properties is missing: channel_ban");
        }
    }

    public void emote(Chatter sender, String message) {
        message = applyFormat(this.formatSupplier.getEmoteFormat(), "").replace("%2$s", message);
        Set<Chatter> recipients = new HashSet<>(this.members);
        trimRecipients(recipients, sender);
        for (Chatter p : recipients) {
            p.getPlayer().sendMessage(message);
        }
        Bukkit.getPluginManager().callEvent(new ChatCompleteEvent(sender, this, message));
        Herochat.logChat(message);
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Channel)) {
            return false;
        }
        Channel channel = (Channel) other;
        return (this.name.equalsIgnoreCase(channel.getName())) || (this.name.equalsIgnoreCase(channel.getNick()));
    }

    public Set<String> getBans() {
        return new HashSet<>(this.bans);
    }

    public void setBans(Set<String> bans) {
        this.bans = bans;
        this.storage.flagUpdate(this);
    }

    public Set<String> getWorlds() {
        List<World> worlds = Bukkit.getServer().getWorlds();
        Set<String> worldNames = new HashSet<>();
        for (World w : worlds) {
            worldNames.add(w.getName());
        }
        return worldNames;
    }

    public boolean isCrossWorld() {
        return true;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
        this.storage.flagUpdate(this);
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int distance) {
        this.distance = (distance < 0 ? 0 : distance);
        this.storage.flagUpdate(this);
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
        this.storage.flagUpdate(this);
    }

    public Set<Chatter> getMembers() {
        return new HashSet<>(this.members);
    }

    public Set<String> getModerators() {
        return new HashSet<>(this.moderators);
    }

    public void setModerators(Set<String> moderators) {
        this.moderators = moderators;
        this.storage.flagUpdate(this);
    }

    public Set<String> getMutes() {
        return new HashSet<>(this.mutes);
    }

    public void setMutes(Set<String> mutes) {
        this.mutes = mutes;
        this.storage.flagUpdate(this);
    }

    public String getName() {
        return this.name;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        this.storage.flagUpdate(this);
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        if (password == null) {
            this.password = "";
        } else {
            this.password = password;
        }
        this.storage.flagUpdate(this);
    }

    public ChannelStorage getStorage() {
        return this.storage;
    }


    public int hashCode() {
        int result = 1;
        result = 31 * result + (this.name == null ? 0 : this.name.toLowerCase().hashCode());
        result = 31 * result + (this.nick == null ? 0 : this.nick.toLowerCase().hashCode());
        return result;
    }

    public boolean isBanned(String name) {
        return this.bans.contains(name.toLowerCase());
    }

    public boolean isHidden() {
        return false;
    }

    public boolean isLocal() {
        return this.distance != 0;
    }

    public boolean isMember(Chatter chatter) {
        return this.members.contains(chatter);
    }

    public boolean isModerator(String name) {
        return this.moderators.contains(name.toLowerCase());
    }

    public boolean isMuted(String name) {
        return this.muted || this.mutes.contains(name.toLowerCase());
    }

    public boolean isShortcutAllowed() {
        return this.shortcutAllowed;
    }

    public void setShortcutAllowed(boolean shortcutAllowed) {
        this.shortcutAllowed = shortcutAllowed;
        this.storage.flagUpdate(this);
    }

    public boolean isTransient() {
        return false;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
        this.storage.flagUpdate(this);
    }

    public void kickMember(Chatter chatter) {
        if (!this.members.contains(chatter)) {
            return;
        }
        removeMember(chatter, false, true);
        try {
            announce(Herochat.getMessage("channel_kick").replace("$1", chatter.getPlayer().getDisplayName()));
        } catch (MessageNotFoundException e) {
            Herochat.severe("Messages.properties is missing: channel_kick");
        }
    }

    public void onFocusGain(Chatter chatter) {
    }

    public void onFocusLoss(Chatter chatter) {
    }

    public void processChat(ChannelChatEvent event) {
        Player player = event.getSender().getPlayer();

        String format = applyFormat(event.getFormat(), event.getBukkitFormat(), player);

        Chatter sender = Herochat.getChatterManager().getChatter(player);
        Set<Chatter> recipients = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            recipients.add(Herochat.getChatterManager().getChatter(p));
        }
        trimRecipients(recipients, sender);
        String msg = String.format(format, player.getDisplayName(), event.getMessage());
        for (Chatter pl : recipients) {
            pl.getPlayer().sendMessage(msg);
        }
        Bukkit.getPluginManager().callEvent(new ChatCompleteEvent(sender, this, msg));
        Herochat.logChat(msg);
    }

    public boolean removeMember(Chatter chatter, boolean announce, boolean flagUpdate) {
        if (!this.members.contains(chatter)) {
            return false;
        }
        this.members.remove(chatter);
        if (chatter.hasChannel(this)) {
            chatter.removeChannel(this, announce, flagUpdate);
        }
        if ((announce) && (this.verbose)) {
            try {
                announce(Herochat.getMessage("channel_leave").replace("$1", chatter.getPlayer().getDisplayName()));
            } catch (MessageNotFoundException e) {
                Herochat.severe("Messages.properties is missing: channel_leave");
            }
        }
        return true;
    }

    public void setBanned(String name, boolean banned) {
        if (banned) {
            this.bans.add(name.toLowerCase());
        } else {
            this.bans.remove(name.toLowerCase());
        }
        this.storage.flagUpdate(this);
    }

    public void setModerator(String name, boolean moderator) {
        if (moderator) {
            this.moderators.add(name.toLowerCase());
        } else {
            this.moderators.remove(name.toLowerCase());
        }
        this.storage.flagUpdate(this);
    }

    public boolean isMuted() {
        return this.muted;
    }

    public void setMuted(boolean value) {
        this.muted = value;
    }

    public void setMuted(String name, boolean muted) {
        if (muted) {
            this.mutes.add(name.toLowerCase());
        } else {
            this.mutes.remove(name.toLowerCase());
        }
        this.storage.flagUpdate(this);
    }

    private boolean isMessageHeard(Set<Player> recipients, Chatter sender) {
        if (!isLocal()) {
            return true;
        }
        Player senderPlayer = sender.getPlayer();
        int visibleRecipients = 0;
        for (Player recipient : recipients) {
            if ((!recipient.hasPermission("herochat.admin.stealth")) && (!recipient.equals(senderPlayer))) {
                visibleRecipients++;
            }
        }
        return visibleRecipients > 0;
    }

    private void trimRecipients(Set<Chatter> recipients, Chatter sender) {
        for (Iterator<Chatter> iterator = recipients.iterator(); iterator.hasNext(); ) {
            Chatter recipient = iterator.next();
            if (recipient != null) {
                if (!this.members.contains(recipient)) {
                    iterator.remove();
                } else if ((isLocal()) && (!sender.isInRange(recipient, this.distance))) {
                    iterator.remove();
                } else if (recipient.isIgnoring(sender)) {
                    iterator.remove();
                }
            }
        }
    }

    public MessageFormatSupplier getFormatSupplier() {
        return this.formatSupplier;
    }
}
