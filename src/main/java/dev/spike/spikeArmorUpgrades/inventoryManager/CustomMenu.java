package dev.spike.spikeArmorUpgrades.inventoryManager;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public abstract class CustomMenu implements InventoryHolder {
    private final InventoryType type;
    protected Inventory inventory;
    protected String openSound;

    protected CustomMenu(InventoryType type) {
        this.type = type;
    }

    public InventoryType getType() {
        return type;
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }

    public String getOpenSound() {
        return openSound;
    }
}
