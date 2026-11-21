package dev.spike.spikeArmorUpgrades.inventoryHolders;

import dev.spike.spikeArmorUpgrades.inventoryManager.CustomMenu;
import dev.spike.spikeArmorUpgrades.inventoryManager.InventoryType;
import dev.spike.spikeArmorUpgrades.utils.Item;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ConfirmUpgrade extends CustomMenu {
    private final CustomMenu previousMenu;
    private final Item higherItem;
    private final boolean payWithMoney;
    private final EquipmentSlot armorSlot;
    private final ItemStack upgradingItem;

    public ConfirmUpgrade(CustomMenu previousMenu, Inventories inventories, Item higherItem, boolean payWithMoney,
                          EquipmentSlot armorSlot, ItemStack upgradingItem) {
        super(InventoryType.CONFIRM_UPGRADE);

        this.previousMenu = previousMenu;
        this.higherItem = higherItem;
        this.payWithMoney = payWithMoney;
        this.armorSlot = armorSlot;
        this.upgradingItem = upgradingItem == null ? null : upgradingItem.clone();

        inventory = Bukkit.createInventory(this, inventories.CONFIRM_UPGRADE_SLOTS, inventories.CONFIRM_UPGRADE_TITLE);

        // load items
        inventories.CONFIRM_UPGRADE_ITEMS.forEach(inventory::setItem);
    }

    public CustomMenu getPreviousMenu() {
        return previousMenu;
    }

    public Item getHigherItem() {
        return higherItem;
    }

    public boolean isPayWithMoney() {
        return payWithMoney;
    }

    public EquipmentSlot getArmorSlot() {
        return armorSlot;
    }

    public ItemStack getUpgradingItem() {
        return upgradingItem;
    }
}