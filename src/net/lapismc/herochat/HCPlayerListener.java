package net.lapismc.herochat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class HCPlayerListener
        implements Listener {
    private final Herochat plugin;

    HCPlayerListener(Herochat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String msg = event.getMessage();
        final String format = event.getFormat();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Herochat.getMessageHandler().handle(player, msg, format));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String input = event.getMessage().substring(1);
        String[] args = input.split(" ");
        Channel channel = Herochat.getChannelManager().getChannel(args[0]);
        if ((channel != null) && (channel.isShortcutAllowed())) {
            event.setCancelled(true);
            Herochat.getCommandHandler().dispatch(event.getPlayer(), "ch qm", args);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Herochat.getChatterManager().addChatter(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Herochat.getChatterManager().removeChatter(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Herochat.getChatterManager().getChatter(event.getPlayer()).refocus();
    }
}
