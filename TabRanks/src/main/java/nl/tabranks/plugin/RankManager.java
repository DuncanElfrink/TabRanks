package nl.tabranks.plugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Beheert alle rangen en spelerdata.
 */
public class RankManager {

    private final TabRanks plugin;

    /** Alle geconfigureerde rangen, gesorteerd op prioriteit */
    private final Map<String, Rank> ranks = new LinkedHashMap<>();

    /** UUID -> rang-id */
    private final Map<UUID, String> playerRanks = new HashMap<>();

    private File dataFile;
    private FileConfiguration dataConfig;

    public RankManager(TabRanks plugin) {
        this.plugin = plugin;
        loadRanks();
        loadPlayerData();
    }

    // ─────────────────────────────────────────────────────────────
    //  Rangen laden uit config.yml
    // ─────────────────────────────────────────────────────────────

    public void loadRanks() {
        ranks.clear();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection ranksSection = config.getConfigurationSection("ranks");

        if (ranksSection == null) {
            plugin.getLogger().warning("Geen 'ranks' sectie gevonden in config.yml!");
            return;
        }

        List<Rank> loaded = new ArrayList<>();
        for (String key : ranksSection.getKeys(false)) {
            ConfigurationSection sec = ranksSection.getConfigurationSection(key);
            if (sec == null) continue;

            String prefix      = colorize(sec.getString("prefix", ""));
            String suffix      = colorize(sec.getString("suffix", ""));
            String tabColor    = colorize(sec.getString("tabcolor", "&f"));
            int priority       = sec.getInt("priority", 100);
            String displayName = sec.getString("display-name", key);

            loaded.add(new Rank(key, prefix, suffix, tabColor, priority, displayName));
        }

        // Sorteer op prioriteit (laagste getal = hoogste rang)
        loaded.sort(Comparator.comparingInt(Rank::getPriority));
        for (Rank r : loaded) {
            ranks.put(r.getId(), r);
        }

        plugin.getLogger().info(ranks.size() + " rangen geladen.");
    }

    // ─────────────────────────────────────────────────────────────
    //  Spelerdata laden/opslaan
    // ─────────────────────────────────────────────────────────────

    private void loadPlayerData() {
        if (!plugin.getConfig().getBoolean("save-data", true)) return;

        dataFile = new File(plugin.getDataFolder(), "players.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Kon players.yml niet aanmaken!", e);
                return;
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection section = dataConfig.getConfigurationSection("players");
        if (section != null) {
            for (String uuidStr : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    String rankId = section.getString(uuidStr);
                    if (rankId != null) playerRanks.put(uuid, rankId);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        plugin.getLogger().info(playerRanks.size() + " spelerrangs geladen.");
    }

    public void savePlayerData() {
        if (!plugin.getConfig().getBoolean("save-data", true)) return;
        if (dataConfig == null || dataFile == null) return;

        for (Map.Entry<UUID, String> entry : playerRanks.entrySet()) {
            dataConfig.set("players." + entry.getKey().toString(), entry.getValue());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Kon players.yml niet opslaan!", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Rang ophalen & instellen
    // ─────────────────────────────────────────────────────────────

    public Rank getRank(Player player) {
        String rankId = playerRanks.getOrDefault(player.getUniqueId(), getDefaultRankId());

        // Check permissions: hoogste permission-rang wint
        for (Rank rank : ranks.values()) {
            if (player.hasPermission("tabranks.rank." + rank.getId())) {
                // Alleen als de speler GEEN handmatige rang heeft, gebruik dan permissions
                if (!playerRanks.containsKey(player.getUniqueId())) {
                    return rank;
                }
            }
        }

        return ranks.getOrDefault(rankId, getDefaultRank());
    }

    public boolean setRank(Player player, String rankId) {
        if (!ranks.containsKey(rankId.toLowerCase())) return false;
        playerRanks.put(player.getUniqueId(), rankId.toLowerCase());
        savePlayerData();
        return true;
    }

    public void removeRank(Player player) {
        playerRanks.remove(player.getUniqueId());
        savePlayerData();
    }

    public Collection<Rank> getAllRanks() {
        return Collections.unmodifiableCollection(ranks.values());
    }

    public Rank getRankById(String id) {
        return ranks.get(id.toLowerCase());
    }

    private String getDefaultRankId() {
        return plugin.getConfig().getString("default-rank", "player");
    }

    private Rank getDefaultRank() {
        Rank def = ranks.get(getDefaultRankId());
        if (def == null && !ranks.isEmpty()) {
            return ranks.values().iterator().next();
        }
        return def;
    }

    // ─────────────────────────────────────────────────────────────
    //  Hulp
    // ─────────────────────────────────────────────────────────────

    public static String colorize(String text) {
        if (text == null) return "";
        return text.replace("&", "§");
    }
}
