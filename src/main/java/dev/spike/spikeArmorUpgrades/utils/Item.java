package dev.spike.spikeArmorUpgrades.utils;

import org.bukkit.inventory.ItemStack;

public record Item(ItemStack item, ItemStack itemPrice, double vaultPrice, int level) {
}
