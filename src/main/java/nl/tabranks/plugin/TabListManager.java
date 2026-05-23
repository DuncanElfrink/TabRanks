package nl.tabranks.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Beheert de TABlist weergave via Scoreboards en Teams.
 */
public class TabListManager {

    private final TabRanks plugin;
    private final RankManager rankManager;

    /** Bijhoudt welk team elke speler in zit */
    private final Map<UUID, String> playerTeams = new HashMap<>();

    public TabListManager(TabRanks plugin, RankManager rankManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
    }

    // ─────────────────────────────────────────────────────────────
    //  TABlist bijwerken voor één speler
    // ─────────────────────────────────────────────────────────────

    public void updatePlayer(Player target) {
        Rank rank = rankManager.getRank(target);

        // Zorg dat elke online speler de scoreboard van de server gebruikt
        Scoreboard board = getOrCreateScoreboard(target);

        // Verwijder uit het oude team
        removeFromTeam(board, target);

        // Voeg toe aan het juiste team
        String teamName = getTeamName(rank);
        Team team = board.getTeam(teamName);
        if (team == null) {
            team = board.registerNewTeam(teamName);
        }

        // Prefix & suffix instellen
        team.setPrefix(rank.getPrefix());
        team.setSuffix(rank.getSuffix());

        // Naambord kleur (via display name in de tab)
        team.addEntry(target.getName());
        playerTeams.put(target.getUniqueId(), teamName);

        // Pas de weergavenaam aan in de TABlist
        String tabName = rank.getTabColor() + target.getName();
        target.setPlayerListName(tabName);

        // Stuur het scoreboard naar alle online spelers zodat zij ook de prefix zien
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            updateScoreboardForViewer(viewer, target, rank);
        }
    }

    /**
     * Zorgt dat de viewer de rang van 'target' ziet in diens scoreboard.
     */
    private void updateScoreboardForViewer(Player viewer, Player target, Rank rank) {
        Scoreboard board = getOrCreateScoreboard(viewer);

        String teamName = getTeamName(rank);
        Team team = board.getTeam(teamName);
        if (team == null) {
            team = board.registerNewTeam(teamName);
        }
        team.setPrefix(rank.getPrefix());
        team.setSuffix(rank.getSuffix());

        // Verwijder target uit alle andere teams op dit bord
        for (Team t : board.getTeams()) {
            if (!t.getName().equals(teamName)) {
                t.removeEntry(target.getName());
            }
        }
        team.addEntry(target.getName());
        viewer.setScoreboard(board);
    }

    // ─────────────────────────────────────────────────────────────
    //  TABlist bijwerken voor alle spelers
    // ─────────────────────────────────────────────────────────────

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
        updateHeaderFooter();
    }

    // ─────────────────────────────────────────────────────────────
    //  Header & Footer
    // ─────────────────────────────────────────────────────────────

    public void updateHeaderFooter() {
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();

        String rawHeader = plugin.getConfig().getString("tablist.header", "&eMijnServer");
        String rawFooter = plugin.getConfig().getString("tablist.footer", "&7Online: {online}/{max}");

        String header = RankManager.colorize(rawHeader)
                .replace("{online}", String.valueOf(online))
                .replace("{max}", String.valueOf(max));

        String footer = RankManager.colorize(rawFooter)
                .replace("{online}", String.valueOf(online))
                .replace("{max}", String.valueOf(max));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeaderFooter(header, footer);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Speler verwijderen (bij uitloggen)
    // ─────────────────────────────────────────────────────────────

    public void removePlayer(Player player) {
        playerTeams.remove(player.getUniqueId());
        // Verwijder naam uit alle teams op alle borden
        for (Player other : Bukkit.getOnlinePlayers()) {
            Scoreboard board = other.getScoreboard();
            for (Team team : board.getTeams()) {
                team.removeEntry(player.getName());
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Hulpmethodes
    // ─────────────────────────────────────────────────────────────

    private Scoreboard getOrCreateScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        // Gebruik nooit het main scoreboard — maak een eigen board per speler
        if (board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }
        return board;
    }

    private void removeFromTeam(Scoreboard board, Player player) {
        String oldTeam = playerTeams.get(player.getUniqueId());
        if (oldTeam != null) {
            Team team = board.getTeam(oldTeam);
            if (team != null) team.removeEntry(player.getName());
        }
    }

    /**
     * Teamnamen zijn gesorteerd op prioriteit zodat de TABlist automatisch
     * de goede volgorde heeft (scoreboard sorteert teams alfabetisch).
     * We zetten de prioriteit als 3-cijferig prefix: "001_owner", "010_vip", etc.
     */
    private String getTeamName(Rank rank) {
        // Scoreboard teamnamen zijn max 16 tekens
        String prio = String.format("%03d", rank.getPriority());
        String id = rank.getId().length() > 12 ? rank.getId().substring(0, 12) : rank.getId();
        return prio + "_" + id;
    }
}
