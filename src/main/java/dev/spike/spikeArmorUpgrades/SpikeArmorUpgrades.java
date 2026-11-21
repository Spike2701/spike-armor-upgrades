package dev.spike.spikeArmorUpgrades;

import dev.spike.spikeArmorUpgrades.commands.Sau;
import dev.spike.spikeArmorUpgrades.commands.Upgrade;
import dev.spike.spikeArmorUpgrades.listeners.InventoryClick;
import dev.spike.spikeArmorUpgrades.utils.Item;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import dev.spike.spikeArmorUpgrades.utils.ItemType;
import dev.spike.spikeArmorUpgrades.utils.config.Messages;
import dev.spike.spikeArmorUpgrades.utils.SetsManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class SpikeArmorUpgrades extends JavaPlugin {
    private static Economy econ = null;
    private final File setsFolder = new File(getDataFolder(), "sets");
    private final File inventoriesFolder = new File(getDataFolder(), "inventories");
    private Messages messages;
    private Inventories inventories;
    private SetsManager setsManager;
    private final List<List<Item>> items = new ArrayList<>();
    private boolean areSetsCorrect = true;

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().severe("Disabled due to no Vault/economy plugin dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultSets();
        if (!isEnabled()) return;

        loadSets();
        initFields();
        registerCommands();
        registerEvents();
        getLogger().info("Enabled spike-armor-upgrades by Spike2701");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled spike-armor-upgrades by Spike2701");
    }

    public void reloadPlugin() {
        saveDefaultSets();
        reloadConfig();
        loadSets();
        messages.init();
        inventories.init();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void saveDefaultSets() {
        saveDefaultConfig();

        if (setsFolder.mkdir()) {
            getLogger().info("Folder 'sets' has been created");
        } else if (!setsFolder.exists()) {
            getLogger().severe("Failed to create folder 'sets'");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (inventoriesFolder.mkdir()) {
            getLogger().info("Folder 'inventories' has been created");
        } else if (!inventoriesFolder.exists()) {
            getLogger().severe("Failed to create folder 'inventories'");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (!isEnabled()) return;

        File[] files = setsFolder.listFiles();
        if (files == null || files.length == 0) {
            saveResource("sets/leather.yml", false);
            saveResource("sets/copper.yml", false);
            saveResource("sets/iron.yml", false);
            saveResource("sets/diamond.yml", false);
            getLogger().info("Default sets has been saved");
        }

        saveInvIfMissing("upgrade-menu");
        saveInvIfMissing("armor-upgrade");
        saveInvIfMissing("item-upgrade");
        saveInvIfMissing("confirm-upgrade");
        saveInvIfMissing("select-item");
        saveInvIfMissing("items-list");
    }

    private void initFields() {
        messages = new Messages(this);
        messages.init();

        inventories = new Inventories(this, messages, econ);
        inventories.init();

        setsManager = new SetsManager(this, messages);
    }

    private void loadSets() {
        items.clear();

        for (int i = 0; i <= 6; i++) {
            items.add(new ArrayList<>());
        }

        List<Integer> levels = new ArrayList<>();
        boolean foundIncorrectSet = false;

        File[] files = setsFolder.listFiles((_, name) -> name.endsWith(".yml"));
        for (File set : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(set);

            int level = yaml.getInt("level");
            if (levels.contains(level)) {
                getLogger().warning("level " + level + " is duplicated");
                foundIncorrectSet = true;
            }
            levels.add(level);

            for (ItemType type : ItemType.values()) {
                ItemStack item = yaml.getItemStack("items." + type.name().toLowerCase());
                if (item == null) {
                    getLogger().warning(type + " in " + set.getName() + " is null");
                    foundIncorrectSet = true;
                    continue;
                }

                ItemStack itemPrice = yaml.getItemStack("item-prices." + type.name().toLowerCase());
                if (itemPrice == null) {
                    getLogger().warning(type + "-price in " + set.getName() + " is null");
                    foundIncorrectSet = true;
                    continue;
                }

                int vaultPrice = yaml.getInt("vault-prices." + type.name().toLowerCase());

                items.get(type.ordinal()).add(new Item(item, itemPrice, vaultPrice, level));
                items.get(type.ordinal()).sort(Comparator.comparingInt(Item::level));
            }
        }

        if (levels.isEmpty()) {
            getLogger().warning("No sets found in sets folder");
            foundIncorrectSet = true;
        }

        levels.sort(Comparator.comparingInt(o -> o));
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i) != i + 1) {
                getLogger().warning("Set levels must be consecutive starting from 1");
                foundIncorrectSet = true;
                break;
            }
        }

        if (!foundIncorrectSet) {
            getLogger().info("Successfully loaded " + levels.size() + " sets");
        }

        areSetsCorrect = !foundIncorrectSet;
    }

    private void saveInvIfMissing(String path) {
        if (!new File(getDataFolder(), "inventories/" + path + ".yml").exists()) {
            saveResource("inventories/" + path + ".yml", false);
        }
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(Sau.create(this, messages, setsManager));
            event.registrar().register(Upgrade.create(this, messages, inventories));
        });
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new InventoryClick(this, inventories, messages, getEcon()), this);
    }

    public boolean areSetsCorrect() {
        return areSetsCorrect;
    }

    public List<List<Item>> getItems() {
        return items;
    }

    public static Economy getEcon() {
        return econ;
    }
}
