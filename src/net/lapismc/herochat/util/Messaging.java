package net.lapismc.herochat.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Messaging {
  private static final Map<String, ChatColor> colors;

  static {
    Map<String, ChatColor> tmpMap = new HashMap<>();
    tmpMap.put("black", ChatColor.BLACK);
    tmpMap.put("0", ChatColor.BLACK);

    tmpMap.put("dark blue", ChatColor.DARK_BLUE);
    tmpMap.put("dark_blue", ChatColor.DARK_BLUE);
    tmpMap.put("1", ChatColor.DARK_BLUE);

    tmpMap.put("dark green", ChatColor.DARK_GREEN);
    tmpMap.put("dark_green", ChatColor.DARK_GREEN);
    tmpMap.put("2", ChatColor.DARK_GREEN);

    tmpMap.put("dark aqua", ChatColor.DARK_AQUA);
    tmpMap.put("dark_aqua", ChatColor.DARK_AQUA);
    tmpMap.put("teal", ChatColor.DARK_AQUA);
    tmpMap.put("3", ChatColor.DARK_AQUA);

    tmpMap.put("dark red", ChatColor.DARK_RED);
    tmpMap.put("dark_red", ChatColor.DARK_RED);
    tmpMap.put("4", ChatColor.DARK_RED);

    tmpMap.put("dark purple", ChatColor.DARK_PURPLE);
    tmpMap.put("dark_purple", ChatColor.DARK_PURPLE);
    tmpMap.put("purple", ChatColor.DARK_PURPLE);
    tmpMap.put("5", ChatColor.DARK_PURPLE);

    tmpMap.put("gold", ChatColor.GOLD);
    tmpMap.put("orange", ChatColor.GOLD);
    tmpMap.put("6", ChatColor.GOLD);

    tmpMap.put("gray", ChatColor.GRAY);
    tmpMap.put("grey", ChatColor.GRAY);
    tmpMap.put("7", ChatColor.GRAY);

    tmpMap.put("dark gray", ChatColor.DARK_GRAY);
    tmpMap.put("dark_gray", ChatColor.DARK_GRAY);
    tmpMap.put("dark grey", ChatColor.DARK_GRAY);
    tmpMap.put("dark_grey", ChatColor.DARK_GRAY);
    tmpMap.put("8", ChatColor.DARK_GRAY);

    tmpMap.put("blue", ChatColor.BLUE);
    tmpMap.put("9", ChatColor.BLUE);

    tmpMap.put("bright green", ChatColor.GREEN);
    tmpMap.put("bright_green", ChatColor.GREEN);
    tmpMap.put("green", ChatColor.GREEN);
    tmpMap.put("a", ChatColor.GREEN);

    tmpMap.put("aqua", ChatColor.AQUA);
    tmpMap.put("b", ChatColor.AQUA);

    tmpMap.put("red", ChatColor.RED);
    tmpMap.put("c", ChatColor.RED);

    tmpMap.put("light purple", ChatColor.LIGHT_PURPLE);
    tmpMap.put("light_purple", ChatColor.LIGHT_PURPLE);
    tmpMap.put("pink", ChatColor.LIGHT_PURPLE);
    tmpMap.put("d", ChatColor.LIGHT_PURPLE);

    tmpMap.put("yellow", ChatColor.YELLOW);
    tmpMap.put("e", ChatColor.YELLOW);

    tmpMap.put("white", ChatColor.WHITE);
    tmpMap.put("f", ChatColor.WHITE);

    tmpMap.put("random", ChatColor.MAGIC);
    tmpMap.put("magic", ChatColor.MAGIC);
    tmpMap.put("k", ChatColor.MAGIC);

    tmpMap.put("bold", ChatColor.BOLD);
    tmpMap.put("l", ChatColor.BOLD);

    tmpMap.put("strike", ChatColor.STRIKETHROUGH);
    tmpMap.put("strikethrough", ChatColor.STRIKETHROUGH);
    tmpMap.put("m", ChatColor.STRIKETHROUGH);

    tmpMap.put("underline", ChatColor.UNDERLINE);
    tmpMap.put("n", ChatColor.UNDERLINE);

    tmpMap.put("italic", ChatColor.ITALIC);
    tmpMap.put("o", ChatColor.ITALIC);

    tmpMap.put("reset", ChatColor.RESET);
    tmpMap.put("r", ChatColor.RESET);

    colors = Collections.unmodifiableMap(tmpMap);
  }

  public static void broadcast(String msg, Object... params) {
    Bukkit.getServer().broadcastMessage(parameterizeMessage(msg, params));
  }

  private static String parameterizeMessage(String msg, Object... params) {
    msg = ChatColor.YELLOW + msg;
    if (params != null) {
      for (int i = 0; i < params.length; i++) {
        msg = msg.replace("$" + (i + 1), ChatColor.WHITE + params[i].toString() + ChatColor.YELLOW);
      }
    }
    return msg;
  }

  public static ChatColor parseColor(String input) {
    return colors.get(input.toLowerCase().replace("&", ""));
  }

  public static void send(CommandSender sender, String msg, Object... params) {
    sender.sendMessage(parameterizeMessage(msg, params));
  }
}
