package dev.spike.spikeArmorUpgrades.utils;

import com.mojang.brigadier.Command;
import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import dev.spike.spikeArmorUpgrades.utils.config.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

public class SetsManager {
    private final SpikeArmorUpgrades plugin;
    private final Messages messages;

    public SetsManager(SpikeArmorUpgrades plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    private File getSetFile(String name) {
        return new File(plugin.getDataFolder(), "sets/" + name + ".yml");
    }

    private boolean isValidType(String type) {
        try {
            ItemType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public int create(String name, int level, CommandSender sender) {
        File setFile = getSetFile(name);

        try {
            if (!setFile.createNewFile()) {
                sender.sendMessage(messages.format(messages.ALREADY_EXISTS, Placeholder.unparsed("name", name)));
                return 0;
            }
        } catch (IOException e) {
            sender.sendMessage(messages.SAVE_FAILED);
            plugin.getLogger().severe("Failed to save set " + name + ".yml: " + e.getMessage());
            return 0;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(setFile);
        yaml.set("level", level);

        try {
            yaml.save(setFile);

        } catch (IOException e) {
            sender.sendMessage(messages.SAVE_FAILED);
            plugin.getLogger().severe("Failed to save set " + name + ".yml: " + e.getMessage());
            return 0;
        }

        sender.sendMessage(messages.format(messages.CREATED,
                Placeholder.unparsed("name", name),
                Placeholder.unparsed("level", String.valueOf(level))));
        return Command.SINGLE_SUCCESS;
    }



    public int delete(String name, CommandSender sender) {
        File setFile = getSetFile(name);

        if (!setFile.exists()) {
            sender.sendMessage(messages.format(messages.DOES_NOT_EXIST, Placeholder.unparsed("name", name)));
            return 0;
        }

        try {
            Files.delete(setFile.toPath());

        } catch (IOException e) {
            sender.sendMessage(messages.DELETE_FAILED);
            plugin.getLogger().severe("Failed to delete set " + name + ".yml: " + e.getMessage());
            return 0;
        }

        sender.sendMessage(messages.format(messages.DELETED, Placeholder.unparsed("name", name)));
        return Command.SINGLE_SUCCESS;
    }



    public int sendList(CommandSender sender) {
        File[] setsFiles = new File(plugin.getDataFolder(), "sets").listFiles();

        if (setsFiles == null || setsFiles.length == 0) {
            sender.sendMessage(messages.NO_AVAILABLE_SETS);
            return 0;
        }

        Arrays.sort(setsFiles, Comparator.comparingInt(set ->
                YamlConfiguration.loadConfiguration(set).getInt("level")
        ));

        sender.sendMessage(messages.AVAILABLE_SETS);
        for (File set : setsFiles) {
            int level = YamlConfiguration.loadConfiguration(set).getInt("level");

            sender.sendMessage(messages.format(messages.SET_IN_LIST,
                    Placeholder.unparsed("name", set.getName()),
                    Placeholder.unparsed("level", String.valueOf(level))));
        }

        return Command.SINGLE_SUCCESS;
    }



    public int setItem(String name, String type, int price, CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.ONLY_PLAYERS);
            return 0;
        }

        File setFile = getSetFile(name);

        if (!setFile.exists()) {
            player.sendMessage(messages.format(messages.DOES_NOT_EXIST, Placeholder.unparsed("name", name)));
            return 0;
        }

        if (!isValidType(type)) {
            player.sendMessage(messages.format(messages.INVALID_TYPE, Placeholder.unparsed("type", type)));
            return 0;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        if (itemInMainHand.getType().isAir() || itemInOffHand.getType().isAir()) {
            player.sendMessage(messages.EMPTY_HANDS);
            return 0;
        }

        ItemStack mainCopy = itemInMainHand.clone();
        ItemStack offCopy = itemInOffHand.clone();
        if (mainCopy.getType().getMaxDurability() > 0) {
            mainCopy.editMeta(Damageable.class, meta -> meta.setDamage(0));
        }
        if (offCopy.getType().getMaxDurability() > 0) {
            offCopy.editMeta(Damageable.class, meta -> meta.setDamage(0));
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(setFile);
        yaml.set("items." + type, mainCopy);
        yaml.set("item-prices." + type, offCopy);
        yaml.set("vault-prices." + type, price);

        try {
            yaml.save(setFile);

        } catch (IOException e) {
            player.sendMessage(messages.SAVE_FAILED);
            plugin.getLogger().severe("Failed to save set " + name + ".yml: " + e.getMessage());
            return 0;
        }

        player.sendMessage(messages.format(messages.SET_ITEMS,
                Placeholder.unparsed("type", type),
                Placeholder.unparsed("name", name),
                Placeholder.unparsed("price", String.valueOf(price))));
        return Command.SINGLE_SUCCESS;
    }



    public int changeLevel(String name, int newLevel, CommandSender sender) {
        File setFile = getSetFile(name);

        if (!setFile.exists()) {
            sender.sendMessage(messages.format(messages.DOES_NOT_EXIST, Placeholder.unparsed("name", name)));
            return 0;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(setFile);
        yaml.set("level", newLevel);

        try {
            yaml.save(setFile);

        } catch (IOException e) {
            sender.sendMessage(messages.SAVE_FAILED);
            plugin.getLogger().severe("Failed to save set " + name + ".yml: " + e.getMessage());
            return 0;
        }

        sender.sendMessage(messages.format(messages.LEVEL_CHANGED,
                Placeholder.unparsed("name", name),
                Placeholder.unparsed("level", String.valueOf(newLevel))));
        return Command.SINGLE_SUCCESS;
    }



    public int changePrice(String name, String type, int newPrice, CommandSender sender) {
        File setFile = getSetFile(name);

        if (!setFile.exists()) {
            sender.sendMessage(messages.format(messages.DOES_NOT_EXIST, Placeholder.unparsed("name", name)));
            return 0;
        }

        if (!isValidType(type)) {
            sender.sendMessage(messages.format(messages.INVALID_TYPE, Placeholder.unparsed("type", type)));
            return 0;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(setFile);
        yaml.set("vault-prices." + type, newPrice);

        try {
            yaml.save(setFile);

        } catch (IOException e) {
            sender.sendMessage(messages.SAVE_FAILED);
            plugin.getLogger().severe("Failed to save set " + name + ".yml: " + e.getMessage());
            return 0;
        }

        sender.sendMessage(messages.format(messages.PRICE_CHANGED,
                Placeholder.unparsed("name", name),
                Placeholder.unparsed("type", type),
                Placeholder.unparsed("price", String.valueOf(newPrice))));
        return Command.SINGLE_SUCCESS;
    }
}
