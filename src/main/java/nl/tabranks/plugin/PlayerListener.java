package nl.tabranks.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final TabRanks plugin;
    private final TabListManager tabListManager;

    public PlayerListener(TabRanks plugin, TabListManager tabListManager) {
        this.plugin = plugin;
        this.tabListManager = tabListManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        // Klein vertraagje zodat de speler volledig is ingelogd
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            tabListManager.updateAll(); // Herlaad voor alle spelers (nieuwe speler erbij)
        }, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        tabListManager.removePlayer(event.getPlayer());
        // Kleine vertraging zodat de speler echt weg is
        plugin.getServer().getScheduler().runTaskLater(plugin, tabListManager::updateAll, 2L);
    }
}
