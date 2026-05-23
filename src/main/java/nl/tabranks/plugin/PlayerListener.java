package nl.tabranks.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final TabRanks plugin;
    private final TabListManager tabListManager;
    private final RankManager rankManager;

    public PlayerListener(TabRanks plugin, TabListManager tabListManager) {
        this.plugin = plugin;
        this.tabListManager = tabListManager;
        this.rankManager = plugin.getRankManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            tabListManager.updateAll();
        }, 5L);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event) {
        Rank rank = rankManager.getRank(event.getPlayer());
        String format = rank.getPrefix() + "%s" + "§r: %s";
        event.setFormat(format);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        tabListManager.removePlayer(event.getPlayer());
        plugin.getServer().getScheduler().runTaskLater(plugin, tabListManager::updateAll, 2L);
    }
}
