package dev.spike.spikeArmorUpgrades.inventoryHolders;

import dev.spike.spikeArmorUpgrades.inventoryManager.CustomMenu;
import dev.spike.spikeArmorUpgrades.inventoryManager.InventoryType;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import dev.spike.spikeArmorUpgrades.utils.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectItem extends CustomMenu {
    private final ItemType itemType;
    private final Map<Integer, ItemStack> playerItems;
    // slot w menu -> slot w ekwipunku gracza
    private final Map<Integer, Integer> menuSlotToPlayerSlot = new HashMap<>();

    public SelectItem(Map<Integer, ItemStack> playerItems, ItemType type, Inventories inventories) {
        super(InventoryType.SELECT_ITEM);

        this.itemType = type;
        this.playerItems = playerItems;

        inventory = Bukkit.createInventory(this, inventories.SELECT_ITEM_SLOTS, inventories.SELECT_ITEM_TITLE);

        // load items
        inventories.SELECT_ITEM_ITEMS.forEach(inventory::setItem);

        // load items to select
        List<Integer> freeSlots = inventories.SELECT_ITEM_FREE_SLOTS;
        int index = 0;

        for (Map.Entry<Integer, ItemStack> entry : playerItems.entrySet()) {
            if (index >= freeSlots.size()) break;

            int menuSlot = freeSlots.get(index);
            inventory.setItem(menuSlot, entry.getValue());
            menuSlotToPlayerSlot.put(menuSlot, entry.getKey());
            index++;
        }
    }

    public ItemType getItemType() {
        return itemType;
    }

    public Map<Integer, ItemStack> getPlayerItems() {
        return playerItems;
    }

    public Map<Integer, Integer> getMenuSlotToPlayerSlot() {
        return menuSlotToPlayerSlot;
    }
}