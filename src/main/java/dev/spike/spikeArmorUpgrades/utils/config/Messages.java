package dev.spike.spikeArmorUpgrades.utils.config;

import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {
    private final SpikeArmorUpgrades plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public String PREFIX;
    public Component HELP;
    public Component RELOADED;
    public Component ONLY_PLAYERS;
    public Component LOADING_SETS_ERROR;

    // without placeholders
    public Component SAVE_FAILED;
    public Component DELETE_FAILED;
    public Component NO_AVAILABLE_SETS;
    public Component AVAILABLE_SETS;
    public Component EMPTY_HANDS;

    // with placeholders
    public String ALREADY_EXISTS;   // <name>
    public String CREATED;          // <name>
    public String DOES_NOT_EXIST;   // <name>
    public String DELETED;          // <name>
    public String SET_IN_LIST;      // <name>, <level>
    public String INVALID_TYPE;     // <type>
    public String SET_ITEMS;        // <type>, <name>, <price>
    public String LEVEL_CHANGED;    // <name>, <level>
    public String PRICE_CHANGED;    // <name>, <type>, <price>

    // from config (messages.*)
    public ConfigMessage NO_ITEM_TO_UPGRADE;
    public ConfigMessage UPGRADED;     // <level>
    public ConfigMessage NO_ENOUGH_MONEY;
    public ConfigMessage NO_ITEM;

    public Messages(SpikeArmorUpgrades plugin) {
        this.plugin = plugin;
    }

    public void init() {
        FileConfiguration config = plugin.getConfig();

        PREFIX = "<#4a9b1d>Spike<#b8ff56>Armor<white>Upgrades <dark_gray>» <#b8ff56>";
        HELP = format(PREFIX + """
            Available commands:
            <dark_gray>» <white>/sau help <dark_gray>- <gray>Shows this list of commands
            <dark_gray>» <white>/sau reload <dark_gray>- <gray>Reloads configuration
            <dark_gray>» <white>/sau list <dark_gray>- <gray>Shows list of all sets
            <dark_gray>» <white>/sau create \\<name> \\<level> <dark_gray>- <gray>Creates set
            <dark_gray>» <white>/sau delete \\<name> <dark_gray>- <gray>Deletes set
            <dark_gray>» <white>/sau changeLevel \\<name> \\<level> <dark_gray>- <gray>Changes level of set
            <dark_gray>» <white>/sau setItem \\<name> \\<type> \\<price> <dark_gray>- <gray>Sets item of given type
            <dark_gray>» <white>/sau changePrice \\<name> \\<type> \\<price> <dark_gray>- <gray>Changes vault price of given type
        
            <gray>(<white><price></white> is price in $ (Vault). Item in off hand is item price
            <gray>setItem saves the item from your main hand and the
            <gray>price item from your off hand. Both hands must be full)""");

        RELOADED = format(PREFIX + "Plugin has been reloaded");
        ONLY_PLAYERS = format(PREFIX + "<red>Only players can execute this command");
        LOADING_SETS_ERROR = format(config.getString("loading-sets-error"));

        // without placeholders
        SAVE_FAILED = format(PREFIX + "<red>Failed to save file. See console");
        DELETE_FAILED = format(PREFIX + "<red>Failed to delete file. See console");
        NO_AVAILABLE_SETS = format(PREFIX + "<gray>No available sets");
        AVAILABLE_SETS = format(PREFIX + "Available sets:");
        EMPTY_HANDS = format(PREFIX + "<red>Hold the armor/item piece in your <dark_red>main hand</dark_red> and the price item in your <dark_red>off hand");

        // with placeholders
        ALREADY_EXISTS = PREFIX + "<red>Set <dark_red><name></dark_red> already exists. Use <dark_red>/sau delete <name></dark_red> to delete it";
        CREATED = PREFIX + "Set <#4a9b1d><name> <#b8ff56>with level <#4a9b1d><level> <#b8ff56>has been created";
        DOES_NOT_EXIST = PREFIX + "<red>Set <dark_red><name></dark_red> doesn't exist";
        DELETED = PREFIX + "Set <#4a9b1d><name> <#b8ff56>has been deleted";
        SET_IN_LIST = " <dark_gray>- <white><name> <yellow>★<level>";
        INVALID_TYPE = PREFIX + "<red>Invalid item type: <dark_red><type>";
        SET_ITEMS = PREFIX + "Items of set <#4a9b1d><name> <#b8ff56>have been saved for <#4a9b1d><type> <#b8ff56>with price <#4a9b1d><price>$";
        LEVEL_CHANGED = PREFIX + "Level <#4a9b1d><level> <#b8ff56>has been set for set <#4a9b1d><name>";
        PRICE_CHANGED = PREFIX + "Vault price of <#4a9b1d><type> <#b8ff56>in set <#4a9b1d><name> <#b8ff56>has been set to <#4a9b1d><price>$";

        NO_ITEM_TO_UPGRADE = loadMessage(config, "no-item-to-upgrade");
        UPGRADED = loadMessage(config, "upgraded");
        NO_ENOUGH_MONEY = loadMessage(config, "no-enough-money");
        NO_ITEM = loadMessage(config, "no-item");
    }

    public Component format(String message, TagResolver... resolvers) {
        return MM.deserialize(message, resolvers);
    }

    private ConfigMessage loadMessage(FileConfiguration config, String key) {
        String path = "messages." + key;

        String chat = config.getString(path + ".chat", "");
        String title = config.getString(path + ".title", "");
        String subtitle = config.getString(path + ".subtitle", "");
        boolean closeInv = config.getBoolean(path + ".close-inv", false);
        String sound = config.getString(path + ".sound", "");

        return new ConfigMessage(chat.isEmpty() ? "" : chat, title, subtitle, sound, closeInv);
    }
}
