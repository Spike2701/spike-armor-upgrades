package dev.spike.spikeArmorUpgrades.inventoryHolders;

import dev.spike.spikeArmorUpgrades.inventoryManager.CustomMenu;
import dev.spike.spikeArmorUpgrades.inventoryManager.InventoryType;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import org.bukkit.Bukkit;

public class UpgradeMenu extends CustomMenu {
    public UpgradeMenu(Inventories inventories) {
        super(InventoryType.UPGRADE_MENU);

        inventory = Bukkit.createInventory(this, inventories.UPGRADE_MENU_SLOTS, inventories.UPGRADE_MENU_TITLE);

        // load items
        inventories.UPGRADE_MENU_ITEMS.forEach((slot, item) -> {
            if (slot >= 0) {
                inventory.setItem(slot, item);
            }
        });
    }
}
