package nl.tabranks.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankCommand implements CommandExecutor, TabCompleter {

    private final TabRanks plugin;
    private final RankManager rankManager;
    private final TabListManager tabListManager;

    // Kleurcodes als constanten
    private static final String PREFIX   = "§8[§6TabRanks§8] §r";
    private static final String SUCCESS  = "§a";
    private static final String ERROR    = "§c";
    private static final String INFO     = "§e";
    private static final String GRAY     = "§7";

    public RankCommand(TabRanks plugin, RankManager rankManager, TabListManager tabListManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
        this.tabListManager = tabListManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "rank":
                return handleRank(sender, args);
            case "rankinfo":
                return handleRankInfo(sender);
            case "ranklist":
                return handleRankList(sender);
            default:
                return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  /rank <speler> <rang>
    // ─────────────────────────────────────────────────────────────

    private boolean handleRank(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tabranks.admin")) {
            sender.sendMessage(PREFIX + ERROR + "Je hebt geen toestemming voor dit commando.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + INFO + "Gebruik: §f/rank <speler> <rang>");
            sender.sendMessage(PREFIX + GRAY + "Gebruik §f/ranklist §7voor alle beschikbare rangen.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(PREFIX + ERROR + "Speler '§f" + args[0] + "§c' is niet online.");
            return true;
        }

        String rankId = args[1].toLowerCase();
        Rank rank = rankManager.getRankById(rankId);

        if (rank == null) {
            sender.sendMessage(PREFIX + ERROR + "Rang '§f" + rankId + "§c' bestaat niet.");
            sender.sendMessage(PREFIX + GRAY + "Gebruik §f/ranklist §7voor alle beschikbare rangen.");
            return true;
        }

        boolean success = rankManager.setRank(target, rankId);
        if (success) {
            tabListManager.updatePlayer(target);
            sender.sendMessage(PREFIX + SUCCESS + "Rang van §f" + target.getName()
                    + SUCCESS + " ingesteld op §f" + rank.getPrefix() + rank.getDisplayName() + SUCCESS + ".");
            target.sendMessage(PREFIX + INFO + "Jouw rang is ingesteld op §f"
                    + rank.getPrefix() + rank.getDisplayName() + INFO + ".");
        } else {
            sender.sendMessage(PREFIX + ERROR + "Er is iets misgegaan bij het instellen van de rang.");
        }

        return true;
    }

    // ─────────────────────────────────────────────────────────────
    //  /rankinfo
    // ─────────────────────────────────────────────────────────────

    private boolean handleRankInfo(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ERROR + "Dit commando is alleen voor spelers.");
            return true;
        }
        Player player = (Player) sender;
        Rank rank = rankManager.getRank(player);

        player.sendMessage("");
        player.sendMessage(PREFIX + INFO + "Jouw rang-informatie:");
        player.sendMessage(GRAY + "  Rang:    §f" + rank.getPrefix() + rank.getDisplayName());
        player.sendMessage(GRAY + "  ID:      §f" + rank.getId());
        player.sendMessage(GRAY + "  Kleur:   " + rank.getTabColor() + rank.getTabColor().replace("§", "&"));
        player.sendMessage("");
        return true;
    }

    // ─────────────────────────────────────────────────────────────
    //  /ranklist
    // ─────────────────────────────────────────────────────────────

    private boolean handleRankList(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§6§l✦ §e§lBeschikbare rangen §6§l✦");
        sender.sendMessage("§8§m────────────────────────────");

        for (Rank rank : rankManager.getAllRanks()) {
            sender.sendMessage("  " + rank.getPrefix() + rank.getDisplayName()
                    + GRAY + " §8(§7ID: §f" + rank.getId() + "§8)");
        }

        sender.sendMessage("§8§m────────────────────────────");
        sender.sendMessage(GRAY + "Gebruik: §f/rank <speler> <rang-id>");
        sender.sendMessage("");
        return true;
    }

    // ─────────────────────────────────────────────────────────────
    //  Tab-completie
    // ─────────────────────────────────────────────────────────────

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("rank")) {
            if (args.length == 1) {
                // Suggereer spelersnamen
                String input = args[0].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player.getName());
                    }
                }
            } else if (args.length == 2) {
                // Suggereer rang-IDs
                String input = args[1].toLowerCase();
                for (Rank rank : rankManager.getAllRanks()) {
                    if (rank.getId().startsWith(input)) {
                        completions.add(rank.getId());
                    }
                }
            }
        }

        return completions;
    }
}
