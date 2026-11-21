package dev.spike.spikeArmorUpgrades.inventoryHolders;

import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import dev.spike.spikeArmorUpgrades.inventoryManager.CustomMenu;
import dev.spike.spikeArmorUpgrades.inventoryManager.InventoryType;
import dev.spike.spikeArmorUpgrades.utils.Item;
import dev.spike.spikeArmorUpgrades.utils.ItemType;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemsList extends CustomMenu {
    private final ItemType itemType;

    public ItemsList(ItemType type, Inventories inventories, SpikeArmorUpgrades plugin) {
        super(InventoryType.ITEM_LIST);
        this.itemType = type;

        inventory = Bukkit.createInventory(this, inventories.ITEMS_LIST_SLOTS, inventories.ITEMS_LIST_TITLE);

        // load items
        inventories.ITEMS_LIST_ITEMS.forEach(inventory::setItem);

        // load items list
        List<Integer> freeSlots = inventories.ITEMS_LIST_FREE_SLOTS;
        List<Item> items = plugin.getItems().get(type.ordinal());

        int count = Math.min(items.size(), freeSlots.size());
        for (int i = 0; i < count; i++) {
            inventory.setItem(freeSlots.get(i), items.get(i).item());
        }
    }

    public ItemType getItemType() {
        return itemType;
    }
}
