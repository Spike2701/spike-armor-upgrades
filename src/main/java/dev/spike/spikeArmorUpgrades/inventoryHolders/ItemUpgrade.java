package dev.spike.spikeArmorUpgrades.inventoryHolders;

import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import dev.spike.spikeArmorUpgrades.inventoryManager.CustomMenu;
import dev.spike.spikeArmorUpgrades.inventoryManager.InventoryType;
import dev.spike.spikeArmorUpgrades.utils.Item;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import dev.spike.spikeArmorUpgrades.utils.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

public class ItemUpgrade extends CustomMenu {
    private final ItemType itemType;
    private final ItemStack currentItem;
    private Item higherItem;
    private double higherItemVaultPrice;
    private ItemStack higherItemItemPrice;

    public ItemUpgrade(Player player, Inventories inventories, ItemType type, ItemStack upgradingItem, SpikeArmorUpgrades plugin) {
        super(InventoryType.ITEM_UPGRADE);
        this.itemType = type;
        this.currentItem = upgradingItem.clone();

        openSound = inventories.ITEM_UPGRADE_OPEN_SOUND;

        inventory = Bukkit.createInventory(this, inventories.ITEM_UPGRADE_SLOTS, inventories.ITEM_UPGRADE_TITLE);

        // load items (inventories/item-upgrade.yml)
        inventories.ITEM_UPGRADE_ITEMS.forEach(inventory::setItem);


        List<Item> items;

        ItemStack itemClone = upgradingItem.clone();
        itemClone.editMeta(Damageable.class, meta -> meta.setDamage(0));
        upgradingItem = itemClone;
        inventory.setItem(inventories.ITEM_UPGRADE_CURRENT_SLOT, upgradingItem);

        boolean nextIsHigher = false;

        items = plugin.getItems().get(type.ordinal());
        for (Item item : items) {
            if (nextIsHigher) {
                inventory.setItem(inventories.ITEM_UPGRADE_HIGHER_SLOT, item.item());
                inventory.setItem(inventories.ITEM_UPGRADE_PRICE_SLOT, item.itemPrice());
                this.higherItem = item;
                this.higherItemVaultPrice = item.vaultPrice();
                this.higherItemItemPrice = item.itemPrice();

                inventories.loadUpgradeButtons(item, player, inventory, inventories.ITEM_UPGRADE_UPGRADE_ICON_SLOT,
                        inventories.ITEM_UPGRADE_UPGRADE_ITEM_SLOT, inventories.ITEM_UPGRADE_UPGRADE_CASH_SLOT);
                return;
            }

            if (item.item().isSimilar(upgradingItem)) {
                nextIsHigher = true;
            }
        }

        if (nextIsHigher) {
            inventory.setItem(inventories.ITEM_UPGRADE_HIGHER_SLOT, inventories.MAX_LEVEL);
        } else {
            inventory.setItem(inventories.ITEM_UPGRADE_HIGHER_SLOT, inventories.INVALID_ARMOR);
        }
        inventory.setItem(inventories.ITEM_UPGRADE_UPGRADE_ICON_SLOT, inventories.CANNOT_UPGRADE);
    }

    public ItemType getItemType() {
        return itemType;
    }

    public ItemStack getCurrentItem() {
        return currentItem;
    }

    public Item getHigherItem() {
        return higherItem;
    }

    public double getHigherItemVaultPrice() {
        return higherItemVaultPrice;
    }

    public ItemStack getHigherItemItemPrice() {
        return higherItemItemPrice;
    }
}