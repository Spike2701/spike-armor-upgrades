package dev.spike.spikeArmorUpgrades.utils.config;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import dev.spike.spikeArmorUpgrades.inventoryHolders.ItemsList;
import dev.spike.spikeArmorUpgrades.inventoryHolders.UpgradeMenu;
import dev.spike.spikeArmorUpgrades.utils.Item;
import dev.spike.spikeArmorUpgrades.utils.ItemType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Inventories {
    private static final UUID HEAD_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final SpikeArmorUpgrades plugin;
    private final Messages messages;
    private final Economy economy;
    private FileConfiguration config;

    public Inventories(SpikeArmorUpgrades plugin, Messages messages, Economy economy) {
        this.plugin = plugin;
        this.messages = messages;
        this.economy = economy;
    }

    // ==================== titles ====================
    public Component UPGRADE_MENU_TITLE;
    public Component ARMOR_UPGRADE_TITLE;
    public Component ITEM_UPGRADE_TITLE;
    public Component CONFIRM_UPGRADE_TITLE;
    public Component SELECT_ITEM_TITLE;
    public Component ITEMS_LIST_TITLE;

    // ==================== sizes ====================
    public int UPGRADE_MENU_SLOTS;
    public int ARMOR_UPGRADE_SLOTS;
    public int ITEM_UPGRADE_SLOTS;
    public int CONFIRM_UPGRADE_SLOTS;
    public int SELECT_ITEM_SLOTS;
    public int ITEMS_LIST_SLOTS;

    // ==================== sounds ====================
    public String UPGRADE_MENU_OPEN_SOUND;
    public String ARMOR_UPGRADE_OPEN_SOUND;
    public String ITEM_UPGRADE_OPEN_SOUND;
    public String SELECT_ITEM_OPEN_SOUND;
    public String ITEM_LIST_OPEN_SOUND;
    public String CONFIRM_UPGRADE_OPEN_SOUND;

    // ==================== items (static items from inventories/*.yml) ====================
    public Map<Integer, ItemStack> UPGRADE_MENU_ITEMS = new HashMap<>();
    public Map<Integer, ItemStack> ARMOR_UPGRADE_ITEMS = new HashMap<>();
    public Map<Integer, ItemStack> ITEM_UPGRADE_ITEMS = new HashMap<>();
    public Map<Integer, ItemStack> CONFIRM_UPGRADE_ITEMS = new HashMap<>();
    public Map<Integer, ItemStack> SELECT_ITEM_ITEMS = new HashMap<>();
    public Map<Integer, ItemStack> ITEMS_LIST_ITEMS = new HashMap<>();

    // ==================== slots ====================
    // upgrade-menu
    public int UPGRADE_MENU_ARMOR_SLOT;
    public int UPGRADE_MENU_ELYTRA_SLOT;
    public int UPGRADE_MENU_WEAPON_SLOT;
    public int UPGRADE_MENU_TOOL_SLOT;

    // armor-upgrade
    public int ARMOR_UPGRADE_BACK_SLOT;
    public List<Integer> ARMOR_UPGRADE_PRICE_SLOTS;
    public List<Integer> ARMOR_UPGRADE_CURRENT_SLOTS;
    public List<Integer> ARMOR_UPGRADE_ICON_SLOTS;
    public List<Integer> ARMOR_UPGRADE_HIGHER_SLOTS;
    public Map<Integer, Integer> ARMOR_UPGRADE_UPGRADE_ITEM_SLOTS = new HashMap<>();
    public Map<Integer, Integer> ARMOR_UPGRADE_UPGRADE_CASH_SLOTS = new HashMap<>();

    // item-upgrade
    public int ITEM_UPGRADE_BACK_SLOT;
    public int ITEM_UPGRADE_LIST_SLOT;
    public int ITEM_UPGRADE_UPGRADE_ITEM_SLOT;
    public int ITEM_UPGRADE_UPGRADE_CASH_SLOT;
    public int ITEM_UPGRADE_PRICE_SLOT;
    public int ITEM_UPGRADE_CURRENT_SLOT;
    public int ITEM_UPGRADE_HIGHER_SLOT;
    public int ITEM_UPGRADE_UPGRADE_ICON_SLOT;

    // select-item
    public int SELECT_ITEM_BACK_SLOT;
    public List<Integer> SELECT_ITEM_BLACKLIST;
    public List<Integer> SELECT_ITEM_FREE_SLOTS = new ArrayList<>();

    // items-list
    public int ITEMS_LIST_BACK_SLOT;
    public List<Integer> ITEMS_LIST_BLACKLIST;
    public List<Integer> ITEMS_LIST_FREE_SLOTS = new ArrayList<>();

    // confirm upgrade
    public int CONFIRM_UPGRADE_CANCEL_SLOT;
    public int CONFIRM_UPGRADE_CONFIRM_SLOT;

    // ==================== items lists ====================
    public Map<Integer, ItemsList> ARMOR_UPGRADE_ITEMS_LISTS = new HashMap<>();
    public List<ItemsList> ITEM_UPGRADE_ITEMS_LIST = new ArrayList<>();

    // ==================== other items (config.yml) ====================
    public ItemStack MAX_LEVEL;
    public ItemStack INVALID_ARMOR;
    public ItemStack CAN_UPGRADE;
    public ItemStack CANNOT_UPGRADE;

    // ==================== menu instance ====================
    public UpgradeMenu UPGRADE_MENU_INSTANCE;

    public void init() {
        this.config = plugin.getConfig();
        FileConfiguration upgradeMenu = loadYamlInventory("upgrade-menu");
        FileConfiguration armorUpgrade = loadYamlInventory("armor-upgrade");
        FileConfiguration itemUpgrade = loadYamlInventory("item-upgrade");
        FileConfiguration confirmUpgrade = loadYamlInventory("confirm-upgrade");
        FileConfiguration selectItem = loadYamlInventory("select-item");
        FileConfiguration itemsList = loadYamlInventory("items-list");

        // titles
        UPGRADE_MENU_TITLE = messages.format(upgradeMenu.getString("title"));
        ARMOR_UPGRADE_TITLE = messages.format(armorUpgrade.getString("title"));
        ITEM_UPGRADE_TITLE = messages.format(itemUpgrade.getString("title"));
        CONFIRM_UPGRADE_TITLE = messages.format(confirmUpgrade.getString("title"));
        SELECT_ITEM_TITLE = messages.format(selectItem.getString("title"));
        ITEMS_LIST_TITLE = messages.format(itemsList.getString("title"));

        // sizes
        UPGRADE_MENU_SLOTS = upgradeMenu.getInt("rows") * 9;
        ARMOR_UPGRADE_SLOTS = armorUpgrade.getInt("rows") * 9;
        ITEM_UPGRADE_SLOTS = itemUpgrade.getInt("rows") * 9;
        CONFIRM_UPGRADE_SLOTS = confirmUpgrade.getInt("rows") * 9;
        SELECT_ITEM_SLOTS = selectItem.getInt("rows") * 9;
        ITEMS_LIST_SLOTS = itemsList.getInt("rows") * 9;

        // sounds
        UPGRADE_MENU_OPEN_SOUND = upgradeMenu.getString("sounds.opened");
        ARMOR_UPGRADE_OPEN_SOUND = armorUpgrade.getString("sounds.opened");
        ITEM_UPGRADE_OPEN_SOUND = itemUpgrade.getString("sounds.opened");
        SELECT_ITEM_OPEN_SOUND = selectItem.getString("sounds.opened");
        ITEM_LIST_OPEN_SOUND = itemsList.getString("sounds.opened");
        CONFIRM_UPGRADE_OPEN_SOUND = confirmUpgrade.getString("sounds.opened");

        // items
        loadItems(UPGRADE_MENU_ITEMS, upgradeMenu);
        loadItems(ARMOR_UPGRADE_ITEMS, armorUpgrade);
        loadItems(ITEM_UPGRADE_ITEMS, itemUpgrade);
        loadItems(CONFIRM_UPGRADE_ITEMS, confirmUpgrade);
        loadItems(SELECT_ITEM_ITEMS, selectItem);
        loadItems(ITEMS_LIST_ITEMS, itemsList);

        // upgrade-menu slots
        UPGRADE_MENU_ARMOR_SLOT = upgradeMenu.getInt("slots.armor");
        UPGRADE_MENU_ELYTRA_SLOT = upgradeMenu.getInt("slots.elytra");
        UPGRADE_MENU_WEAPON_SLOT = upgradeMenu.getInt("slots.weapon");
        UPGRADE_MENU_TOOL_SLOT = upgradeMenu.getInt("slots.tool");

        // armor-upgrade slots
        ARMOR_UPGRADE_BACK_SLOT = armorUpgrade.getInt("slots.back-button");
        ARMOR_UPGRADE_PRICE_SLOTS = armorUpgrade.getIntegerList("slots.prices");
        ARMOR_UPGRADE_CURRENT_SLOTS = armorUpgrade.getIntegerList("slots.current-armors");
        ARMOR_UPGRADE_ICON_SLOTS = armorUpgrade.getIntegerList("slots.upgrade-icons");
        ARMOR_UPGRADE_HIGHER_SLOTS = armorUpgrade.getIntegerList("slots.higher-armors");
        List<Integer> upgradesItemSlots = armorUpgrade.getIntegerList("slots.upgrades-item");
        List<Integer> upgradesCashSlots = armorUpgrade.getIntegerList("slots.upgrades-cash");
        ARMOR_UPGRADE_UPGRADE_CASH_SLOTS.clear();
        ARMOR_UPGRADE_UPGRADE_ITEM_SLOTS.clear();
        for (int i = 0; i < 4; i++) {
            ARMOR_UPGRADE_UPGRADE_ITEM_SLOTS.put(i, upgradesItemSlots.get(i));
            ARMOR_UPGRADE_UPGRADE_CASH_SLOTS.put(i, upgradesCashSlots.get(i));
        }

        // item-upgrade slots
        ITEM_UPGRADE_BACK_SLOT = itemUpgrade.getInt("slots.back-button");
        ITEM_UPGRADE_LIST_SLOT = itemUpgrade.getInt("slots.items-list");
        ITEM_UPGRADE_UPGRADE_ITEM_SLOT = itemUpgrade.getInt("slots.upgrade-item");
        ITEM_UPGRADE_UPGRADE_CASH_SLOT = itemUpgrade.getInt("slots.upgrade-cash");
        ITEM_UPGRADE_PRICE_SLOT = itemUpgrade.getInt("slots.price");
        ITEM_UPGRADE_CURRENT_SLOT = itemUpgrade.getInt("slots.current");
        ITEM_UPGRADE_HIGHER_SLOT = itemUpgrade.getInt("slots.higher");
        ITEM_UPGRADE_UPGRADE_ICON_SLOT = itemUpgrade.getInt("slots.upgrade-icon");

        // select-item slots
        SELECT_ITEM_BACK_SLOT = selectItem.getInt("slots.back-button", -1);
        SELECT_ITEM_BLACKLIST = selectItem.getIntegerList("slot-blacklist");
        loadFreeSlots(SELECT_ITEM_FREE_SLOTS, SELECT_ITEM_SLOTS, SELECT_ITEM_BLACKLIST, SELECT_ITEM_ITEMS);

        // items-list slots
        ITEMS_LIST_BACK_SLOT = itemsList.getInt("slots.back-button", -1);
        ITEMS_LIST_BLACKLIST = itemsList.getIntegerList("slot-blacklist");
        loadFreeSlots(ITEMS_LIST_FREE_SLOTS, ITEMS_LIST_SLOTS, ITEMS_LIST_BLACKLIST, ITEMS_LIST_ITEMS);

        // confirm upgrade slots
        CONFIRM_UPGRADE_CANCEL_SLOT = confirmUpgrade.getInt("slots.cancel", -1);
        CONFIRM_UPGRADE_CONFIRM_SLOT = confirmUpgrade.getInt("slots.confirm", -1);

        // items lists
        List<Integer> armorLists = armorUpgrade.getIntegerList("slots.lists");
        ARMOR_UPGRADE_ITEMS_LISTS.clear();
        ARMOR_UPGRADE_ITEMS_LISTS.put(armorLists.get(0), new ItemsList(ItemType.HELMET, this, plugin));
        ARMOR_UPGRADE_ITEMS_LISTS.put(armorLists.get(1), new ItemsList(ItemType.CHESTPLATE, this, plugin));
        ARMOR_UPGRADE_ITEMS_LISTS.put(armorLists.get(2), new ItemsList(ItemType.LEGGINGS, this, plugin));
        ARMOR_UPGRADE_ITEMS_LISTS.put(armorLists.get(3), new ItemsList(ItemType.BOOTS, this, plugin));

        ITEM_UPGRADE_ITEMS_LIST.clear();
        ITEM_UPGRADE_ITEMS_LIST.add(new ItemsList(ItemType.WEAPON, this, plugin));
        ITEM_UPGRADE_ITEMS_LIST.add(new ItemsList(ItemType.TOOL, this, plugin));
        ITEM_UPGRADE_ITEMS_LIST.add(new ItemsList(ItemType.ELYTRA, this, plugin));

        // other items
        MAX_LEVEL = loadItem("upgrades-menus.max-level", config);
        INVALID_ARMOR = loadItem("upgrades-menus.invalid-armor", config);
        CAN_UPGRADE = loadItem("upgrades-menus.can-upgrade", config);
        CANNOT_UPGRADE = loadItem("upgrades-menus.cannot-upgrade", config);

        // menu instance
        UPGRADE_MENU_INSTANCE = new UpgradeMenu(this);
    }

    private FileConfiguration loadYamlInventory(String type) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "inventories/" + type + ".yml"));
    }

    private void loadFreeSlots(List<Integer> freeSlots, int size, List<Integer> blacklist, Map<Integer, ItemStack> items) {
        freeSlots.clear();

        for (int i = 0; i < size; i++) {
            if (blacklist.contains(i)) continue;
            if (items.containsKey(i)) continue;
            freeSlots.add(i);
        }
    }

    public void playSound(Player player, String sound) {
        if (sound != null && !sound.isEmpty()) player.playSound(player, sound, 1.0f, 1.0f);
    }

    public void loadUpgradeButtons(Item item, Player player, Inventory inventory, int iconSlot, int upgradeItemSlot, int upgradeCashSlot) {
        double price = item.vaultPrice();
        double balance = economy.getBalance(player);

        boolean canUpgradeForMoney = economy.has(player, price);
        boolean canUpgradeForItem = player.getInventory().containsAtLeast(item.itemPrice(), item.itemPrice().getAmount());

        ItemStack upgradeCashButton = loadUpgradeCashButton(price, balance, canUpgradeForMoney);
        ItemStack upgradeItemButton = loadUpgradeItemButton(canUpgradeForItem, item);


        if (canUpgradeForMoney || canUpgradeForItem) {
            inventory.setItem(iconSlot, CAN_UPGRADE);
        } else {
            inventory.setItem(iconSlot, CANNOT_UPGRADE);
        }

        inventory.setItem(upgradeItemSlot, upgradeItemButton);
        inventory.setItem(upgradeCashSlot, upgradeCashButton);
    }

    private ItemStack loadUpgradeCashButton(double price, double balance, boolean canUpgrade) {
        String path = canUpgrade ? "upgrades-menus.upgrade-cash.can-upgrade" : "upgrades-menus.upgrade-cash.cannot-upgrade";

        return loadItem(path, config,
                Placeholder.unparsed("price", economy.format(price)),
                Placeholder.unparsed("balance", economy.format(balance)),
                Placeholder.unparsed("after-purchasing", economy.format(balance - price)),
                Placeholder.unparsed("missing", economy.format(price - balance)));
    }

    private ItemStack loadUpgradeItemButton(boolean canUpgrade, Item item) {
        String path = canUpgrade ? "upgrades-menus.upgrade-item.can-upgrade" : "upgrades-menus.upgrade-item.cannot-upgrade";

        String materialName = item.itemPrice().getType().name();
        int amount = item.itemPrice().getAmount();

        ItemStack icon = item.itemPrice().clone();

        icon.editMeta(meta -> {
            meta.setHideTooltip(false);
            meta.customName(messages.format(config.getString(path + ".name")).decoration(TextDecoration.ITALIC, false));
            List<Component> formattedLore = new ArrayList<>();
            for (String line : config.getStringList(path + ".lore")) {
                formattedLore.add(messages.format(line,
                        Placeholder.unparsed("material-name", materialName),
                        Placeholder.unparsed("amount", String.valueOf(amount)))
                        .decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(formattedLore);
        });

        return icon;
    }

    private void loadItems(Map<Integer, ItemStack> itemsList, FileConfiguration config) {
        // delete previous items
        itemsList.clear();

        ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null) return;

        for (String itemName : section.getKeys(false)) {
            String path = "items." + itemName;

            // single slot
            if (config.contains(path + ".slot")) {
                itemsList.put(config.getInt(path + ".slot"), loadItem(path, config));

            // multiple slots
            } else if (config.contains(path + ".slots")) {
                List<Integer> slots = config.getIntegerList(path + ".slots");
                for (int slot : slots) {
                    itemsList.put(slot, loadItem(path, config));
                }
            }
        }
    }

    private ItemStack loadItem(String startingPath, FileConfiguration config, TagResolver... resolvers) {
        Material material = Material.matchMaterial(config.getString(startingPath + ".material", "barrier"));
        if (material == null) material = Material.BARRIER;

        ItemStack item;
        String texture = config.getString(startingPath + ".texture");

        if (material == Material.PLAYER_HEAD && texture != null && !texture.isBlank()) {
            item = createHead(texture);
        } else {
            item = new ItemStack(material, config.getInt(startingPath + ".amount", 1));
        }

        item.editMeta(meta -> {
            // custom name
            String name = config.getString(startingPath + ".name");
            if (name != null) meta.customName(messages.format(name, resolvers).decoration(TextDecoration.ITALIC, false));

            // hide tooltip
            meta.setHideTooltip(config.getBoolean(startingPath + ".hide-tooltip", false));

            // lore
            List<Component> formattedLore = new ArrayList<>();
            for (String line : config.getStringList(startingPath + ".lore")) {
                formattedLore.add(messages.format(line, resolvers).decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(formattedLore);

            // enchantments
            ConfigurationSection section = config.getConfigurationSection(startingPath + ".enchantments");
            if (section != null) {
                Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
                for (String key : section.getKeys(false)) {
                    Enchantment enchantment = registry.get(NamespacedKey.minecraft(key.toLowerCase(Locale.ROOT)));
                    if (enchantment == null) {
                        plugin.getLogger().warning("Invalid enchantment in config: " + key);
                        continue;
                    }

                    meta.addEnchant(enchantment, section.getInt(key), true);
                }
            }

            // item flags
            for (String flag : config.getStringList(startingPath + ".flags")) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid item flag: " + flag);
                }
            }
        });

        return item;
    }


    private static ItemStack createHead(String texture) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        String value = texture;
        if (texture.startsWith("http")) {
            String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + texture + "\"}}}";
            value = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        }

        String finalValue = value;
        item.editMeta(SkullMeta.class, meta -> {
            PlayerProfile profile = Bukkit.createProfile(HEAD_UUID);
            profile.setProperty(new ProfileProperty("textures", finalValue));
            meta.setPlayerProfile(profile);
        });
        return item;
    }
}