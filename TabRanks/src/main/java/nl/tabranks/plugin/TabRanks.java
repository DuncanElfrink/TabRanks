package nl.tabranks.plugin;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * TabRanks - Mooie TABlist met aanpasbare rangen
 *
 * Commando's:
 *   /rank <speler> <rang>   - Stel de rang van een speler in (tabranks.admin)
 *   /rankinfo               - Bekijk jouw eigen rang
 *   /ranklist               - Bekijk alle beschikbare rangen
 */
public class TabRanks extends JavaPlugin {

    private RankManager rankManager;
    private TabListManager tabListManager;

    @Override
    public void onEnable() {
        // Config opslaan als die nog niet bestaat
        saveDefaultConfig();

        // Managers aanmaken
        rankManager = new RankManager(this);
        tabListManager = new TabListManager(this, rankManager);

        // Events registreren
        PlayerListener listener = new PlayerListener(this, tabListManager);
        getServer().getPluginManager().registerEvents(listener, this);

        // Commando's registreren
        RankCommand rankCommand = new RankCommand(this, rankManager, tabListManager);
        getCommand("rank").setExecutor(rankCommand);
        getCommand("rank").setTabCompleter(rankCommand);
        getCommand("rankinfo").setExecutor(rankCommand);
        getCommand("ranklist").setExecutor(rankCommand);

        // Periodieke TABlist update starten
        int interval = getConfig().getInt("tablist.update-interval", 40);
        getServer().getScheduler().runTaskTimer(this, () -> {
            tabListManager.updateAll();
        }, 20L, interval);

        // Alle huidige online spelers bijwerken (bij reload)
        tabListManager.updateAll();

        getLogger().info("§aTabRanks is succesvol gestart! §7(" + rankManager.getAllRanks().size() + " rangen geladen)");
    }

    @Override
    public void onDisable() {
        if (rankManager != null) {
            rankManager.savePlayerData();
        }
        getLogger().info("TabRanks uitgeschakeld. Spelerdata opgeslagen.");
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public TabListManager getTabListManager() {
        return tabListManager;
    }
}
