package com.dthielke.herochat;

import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler {
    private List<String> censors = new ArrayList<>();
    private boolean twitterStyleMsgs = true;

    public static ChannelChatEvent throwChannelEvent(Chatter sender, Channel channel, Chatter.Result result, String msg, String bukkitFormat, String format) {
        ChannelChatEvent event = new ChannelChatEvent(sender, channel, result, msg, bukkitFormat, format);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    void setTwitterStyleMsgs(boolean twitterStyleMsgs) {
        this.twitterStyleMsgs = twitterStyleMsgs;
    }

    private String censor(String msg) {
        for (String censor : this.censors) {
            String[] split = censor.split(";", 2);
            if (split.length == 1) {
                msg = censor(msg, censor, false, "");
            } else {
                msg = censor(msg, split[0], true, split[1]);
            }
        }
        return msg;
    }

    private String censor(String msg, String censor, boolean customReplacement, String replacement) {
        Pattern pattern = Pattern.compile(censor, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);
        StringBuilder censoredMsg = new StringBuilder();
        while (matcher.find()) {
            String match = matcher.group();
            if (!customReplacement) {
                char[] replaceChars = new char[match.length()];
                Arrays.fill(replaceChars, '*');
                replacement = new String(replaceChars);
            }
            censoredMsg.append(msg.substring(0, matcher.start())).append(replacement);
            msg = msg.substring(matcher.end());
            matcher = pattern.matcher(msg);
        }
        censoredMsg.append(msg);

        return censoredMsg.toString();
    }

    public void handle(Player player, String msg, String format) {
        if (!player.isOnline()) {
            return;
        }
        if ((this.twitterStyleMsgs) && (msg.startsWith("@")) && (msg.length() > 1) && (msg.charAt(1) != ' ')) {
            msg = "msg " + msg.substring(1);
            Herochat.getCommandHandler().dispatch(player, "ch", msg.split(" "));
            return;
        }
        ChatterManager chatterManager = Herochat.getChatterManager();
        if (!chatterManager.hasChatter(player)) {
            chatterManager.addChatter(player);
        }
        Chatter sender = chatterManager.getChatter(player);
        if (sender == null) {
            throw new RuntimeException("Chatter (" + player.getName() + ") not found.");
        }
        Chatter.Result result;
        Channel channel = sender.getActiveChannel();
        ChannelChatEvent channelEvent = null;
        if (channel == null) {
            result = Chatter.Result.NO_CHANNEL;
        } else {
            result = sender.canSpeak(channel);


            channelEvent = throwChannelEvent(sender, channel, result, msg, format, channel.getFormat());
            result = channelEvent.getResult();
            channel = channelEvent.getChannel();
        }
        switch (result.ordinal()) {
            case 1:
                try {
                    Messaging.send(player, Herochat.getMessage("messageHandler_noChannel"));
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_noChannel");
                }
            case 2:
                try {
                    Messaging.send(player, Herochat.getMessage("messageHandler_notInChannel"));
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_notInChannel");
                }
            case 3:
                try {
                    Messaging.send(player, Herochat.getMessage("messageHandler_muted"));
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_muted");
                }
            case 4:
                try {
                    assert channel != null;
                    Messaging.send(player, Herochat.getMessage("messageHandler_noPermission"), channel.getColor() + channel.getName());
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_noPermission");
                }
            case 5:
                try {
                    assert channel != null;
                    Messaging.send(player, Herochat.getMessage("messageHandler_badWorld"), channel.getColor() + channel.getName());
                } catch (MessageNotFoundException e) {
                    Herochat.severe("Messages.properties is missing: messageHandler_badWorld");
                }
        }
        if (result != Chatter.Result.ALLOWED) {
            return;
        }
        Pattern pattern = Pattern.compile("(?i)(&)([0-9a-fk-or])");
        Matcher match = pattern.matcher(channelEvent.getMessage());
        StringBuffer sb = new StringBuffer();
        while (match.find()) {
            ChatColor color = ChatColor.getByChar(match.group(2).toLowerCase());
            if (sender.canColorMessages(channel, color) == Chatter.Result.ALLOWED) {
                match.appendReplacement(sb, color.toString());
            } else {
                match.appendReplacement(sb, "");
            }
        }
        channelEvent.setMessage(censor(match.appendTail(sb).toString()));


        channel.processChat(channelEvent);
    }

    void setCensors(List<String> censors) {
        this.censors = censors;
    }
}
