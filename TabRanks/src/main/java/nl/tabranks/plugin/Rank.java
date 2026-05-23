package nl.tabranks.plugin;

/**
 * Representeert een rang met alle bijbehorende instellingen.
 */
public class Rank {

    private final String id;
    private final String prefix;
    private final String suffix;
    private final String tabColor;
    private final int priority;
    private final String displayName;

    public Rank(String id, String prefix, String suffix, String tabColor, int priority, String displayName) {
        this.id = id;
        this.prefix = prefix;
        this.suffix = suffix;
        this.tabColor = tabColor;
        this.priority = priority;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    /** Prefix inclusief kleurcode, bijv. "&4[OWNER] &c" */
    public String getPrefix() {
        return prefix;
    }

    /** Suffix achter de naam (optioneel) */
    public String getSuffix() {
        return suffix;
    }

    /** Kleur van de naam in de TABlist, bijv. "&c" */
    public String getTabColor() {
        return tabColor;
    }

    /** Hoe hoger de prioriteit, hoe hoger de rang in de TABlist (1 = hoogste) */
    public int getPriority() {
        return priority;
    }

    public String getDisplayName() {
        return displayName;
    }
}
