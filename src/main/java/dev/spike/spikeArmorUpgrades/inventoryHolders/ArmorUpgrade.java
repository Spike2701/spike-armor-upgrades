package dev.spike.spikeArmorUpgrades.inventoryHolders;

import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import dev.spike.spikeArmorUpgrades.inventoryManager.CustomMenu;
import dev.spike.spikeArmorUpgrades.inventoryManager.InventoryType;
import dev.spike.spikeArmorUpgrades.utils.Item;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class ArmorUpgrade extends CustomMenu {
    private final List<Item> armor = new ArrayList<>();

    public ArmorUpgrade(Player player, Inventories inventories, SpikeArmorUpgrades plugin) {
        super(InventoryType.ARMOR_UPGRADE);

        openSound = inventories.ARMOR_UPGRADE_OPEN_SOUND;

        inventory = Bukkit.createInventory(this, inventories.ARMOR_UPGRADE_SLOTS, inventories.ARMOR_UPGRADE_TITLE);

        // load items (inventories/armor-upgrade.yml)
        inventories.ARMOR_UPGRADE_ITEMS.forEach(inventory::setItem);

        ItemStack[] playerArmor = { player.getInventory().getHelmet(),
                player.getInventory().getChestplate(),
                player.getInventory().getLeggings(),
                player.getInventory().getBoots() };

        List<Item> items;

        armorPartsLoop:
        for (int i = 0; i < 4; i++) {

            boolean nextIsHigher = playerArmor[i].getType().isAir();
            if (!nextIsHigher) {
                ItemStack itemClone = playerArmor[i].clone();
                itemClone.editMeta(Damageable.class, meta -> meta.setDamage(0));
                playerArmor[i] = itemClone;
            }
            inventory.setItem(inventories.ARMOR_UPGRADE_CURRENT_SLOTS.get(i), playerArmor[i]);

            items = plugin.getItems().get(i);
            for (Item item : items) {
                if (nextIsHigher) {
                    this.armor.add(item);

                    inventory.setItem(inventories.ARMOR_UPGRADE_HIGHER_SLOTS.get(i), item.item());
                    inventory.setItem(inventories.ARMOR_UPGRADE_PRICE_SLOTS.get(i), item.itemPrice());

                    inventories.loadUpgradeButtons(item, player, inventory, inventories.ARMOR_UPGRADE_ICON_SLOTS.get(i),
                            inventories.ARMOR_UPGRADE_UPGRADE_ITEM_SLOTS.get(i), inventories.ARMOR_UPGRADE_UPGRADE_CASH_SLOTS.get(i));
                    continue armorPartsLoop;
                }

                if (item.item().isSimilar(playerArmor[i])) {
                    nextIsHigher = true;
                }
            }

            this.armor.add(null);

            if (nextIsHigher) {
                inventory.setItem(inventories.ARMOR_UPGRADE_HIGHER_SLOTS.get(i), inventories.MAX_LEVEL);
            } else {
                inventory.setItem(inventories.ARMOR_UPGRADE_HIGHER_SLOTS.get(i), inventories.INVALID_ARMOR);
            }
            inventory.setItem(inventories.ARMOR_UPGRADE_ICON_SLOTS.get(i), inventories.CANNOT_UPGRADE);
        }
    }

    public List<Item> getArmor() {
        return armor;
    }
}