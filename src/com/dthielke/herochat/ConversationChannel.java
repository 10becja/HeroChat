package com.dthielke.herochat;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConversationChannel
        extends StandardChannel {
    public ConversationChannel(Chatter memberOne, Chatter memberTwo, MessageFormatSupplier formatSupplier) {
        super(Herochat.getChannelManager().getStorage(), "convo" + memberOne.getName() + memberTwo.getName(), "convo" + memberTwo.getName() + memberOne.getName(), formatSupplier);


        super.addMember(memberOne, false, false);
        super.addMember(memberTwo, false, false);
        setFormat(formatSupplier.getConversationFormat());
    }

    public boolean addMember(Chatter chatter, boolean announce, boolean flagUpdate) {
        return (getMembers().size() < 2) && (super.addMember(chatter, false, false));
    }

    public void addWorld(String world) {
    }

    private String applyFormat(String format, Player sender, Player recipient) {
        if (sender.equals(recipient)) {
            Player target = null;
            for (Chatter chatter : getMembers()) {
                if (!chatter.getPlayer().equals(sender)) {
                    target = chatter.getPlayer();
                    break;
                }
            }
            if (target != null) {
                format = format.replace("{convoaddress}", "To");
                format = format.replace("{convopartner}", target.getDisplayName());
            }
        } else {
            format = format.replace("{convoaddress}", "From");
            format = format.replace("{convopartner}", sender.getDisplayName());
        }
        format = format.replaceAll("(?i)&([0-9a-fk-or])", "§$1");
        return format;
    }

    public void banMember(Chatter chatter) {
    }

    public Set<String> getBans() {
        return new HashSet<>();
    }

    public void setBans(Set<String> bans) {
    }

    public int getDistance() {
        return 0;
    }

    public Set<String> getModerators() {
        return new HashSet<>();
    }

    public void setModerators(Set<String> moderators) {
    }

    public Set<String> getMutes() {
        return new HashSet<>();
    }

    public void setMutes(Set<String> mutes) {
    }

    public String getPassword() {
        return "";
    }

    public void setPassword(String password) {
    }

    public Set<String> getWorlds() {
        return new HashSet<>();
    }

    public void setWorlds(Set<String> worlds) {
    }

    public boolean hasWorld(World world) {
        return true;
    }

    public boolean isBanned(String name) {
        return false;
    }

    public boolean isHidden() {
        return true;
    }

    public boolean isLocal() {
        return false;
    }

    public boolean isModerator(String name) {
        return false;
    }

    public boolean isMuted(String name) {
        return false;
    }

    public boolean isShortcutAllowed() {
        return false;
    }

    public void setShortcutAllowed(boolean shortcutAllowed) {
    }

    public boolean isTransient() {
        return true;
    }

    public void kickMember(Chatter chatter) {
    }

    public void onFocusLoss(Chatter chatter) {
        for (Chatter member : getMembers()) {
            if ((member.getActiveChannel() != null) && (member.getActiveChannel().equals(this))) {
                return;
            }
        }
        for (Iterator<Chatter> iter = getMembers().iterator(); iter.hasNext(); ) {
            Chatter member = iter.next();
            iter.remove();
            member.removeChannel(this, false, true);
        }
        Herochat.getChannelManager().removeChannel(this);
    }

    public void processChat(ChannelChatEvent event) {
        Player player = event.getSender().getPlayer();
        String senderName = player.getName();
        Chatter sender = Herochat.getChatterManager().getChatter(player);

        String format = event.getFormat();
        for (Chatter member : getMembers()) {
            Player memberPlayer = member.getPlayer();
            if ((!member.isIgnoring(senderName)) || (member.canIgnore(Herochat.getChatterManager().getChatter(senderName)) == Chatter.Result.NO_PERMISSION)) {
                String appliedFormat = applyFormat(format, player, memberPlayer);
                memberPlayer.sendMessage(appliedFormat.replace("{msg}", event.getMessage()));
            } else {
                return;
            }
            if ((!sender.equals(member)) && (member.isAFK())) {
                String afkMsg = member.getAFKMessage();
                afkMsg = "<AFK> " + afkMsg;
                player.sendMessage(applyFormat(format, memberPlayer, player).replace("{msg}", afkMsg));
            }
            if (!sender.equals(member)) {
                member.setLastPrivateMessageSource(sender);

                Herochat.logChat(senderName + " -> " + member.getName() + ": " + event.getMessage());
            }
        }
        Bukkit.getPluginManager().callEvent(new ChatCompleteEvent(sender, this, event.getMessage()));
    }

    public boolean removeMember(Chatter chatter, boolean announce, boolean flagUpdate) {
        if (super.removeMember(chatter, false, flagUpdate)) {
            int count = getMembers().size();
            if (count == 1) {
                Chatter otherMember = getMembers().iterator().next();
                removeMember(otherMember, false, flagUpdate);
                if ((otherMember.getActiveChannel() != null) && (otherMember.getActiveChannel().equals(this))) {
                    otherMember.setActiveChannel(null, true, flagUpdate);
                }
                Herochat.getChannelManager().removeChannel(this);
            }
            return true;
        }
        return false;
    }

    public void removeWorld(String world) {
    }

    public void setBanned(String name, boolean banned) {
    }

    public void setModerator(String name, boolean moderator) {
    }

    public void setMuted(String name, boolean muted) {
    }

    public void setNick(String nick) {
    }
}
