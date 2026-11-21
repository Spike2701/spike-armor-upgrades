package dev.spike.spikeArmorUpgrades.listeners;

import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import dev.spike.spikeArmorUpgrades.inventoryHolders.*;
import dev.spike.spikeArmorUpgrades.inventoryManager.CustomMenu;
import dev.spike.spikeArmorUpgrades.utils.Item;
import dev.spike.spikeArmorUpgrades.utils.config.ConfigMessage;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import dev.spike.spikeArmorUpgrades.utils.ItemType;
import dev.spike.spikeArmorUpgrades.utils.config.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

import java.util.*;
import java.util.function.Supplier;

public class InventoryClick implements Listener {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private final SpikeArmorUpgrades plugin;
    private final Inventories inventories;
    private final Messages messages;
    private final Economy economy;

    public InventoryClick(SpikeArmorUpgrades plugin, Inventories inventories, Messages messages, Economy economy) {
        this.plugin = plugin;
        this.inventories = inventories;
        this.messages = messages;
        this.economy = economy;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof CustomMenu customMenu)) {
            return;
        }

        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        int slot = e.getRawSlot();
        Player player = (Player) e.getWhoClicked();

        switch (customMenu.getType()) {
            case UPGRADE_MENU -> {
                if (slot == inventories.UPGRADE_MENU_ARMOR_SLOT) {
                    openInv(player, new ArmorUpgrade(player, inventories, plugin));
                    inventories.playSound(player, inventories.ARMOR_UPGRADE_OPEN_SOUND);

                } else if (slot == inventories.UPGRADE_MENU_ELYTRA_SLOT) {
                    openCategory(player, ItemType.ELYTRA);

                } else if (slot == inventories.UPGRADE_MENU_WEAPON_SLOT) {
                    openCategory(player, ItemType.WEAPON);

                } else if (slot == inventories.UPGRADE_MENU_TOOL_SLOT) {
                    openCategory(player, ItemType.TOOL);
                }
            }

            case ARMOR_UPGRADE -> {
                ArmorUpgrade armorUpgrade = (ArmorUpgrade) e.getInventory().getHolder();
                Item item;

                if (slot == inventories.ARMOR_UPGRADE_BACK_SLOT) {
                    openInv(player, inventories.UPGRADE_MENU_INSTANCE);
                    inventories.playSound(player, inventories.UPGRADE_MENU_OPEN_SOUND);


                } else if (inventories.ARMOR_UPGRADE_ITEMS_LISTS.containsKey(slot)) {
                    openInv(player, inventories.ARMOR_UPGRADE_ITEMS_LISTS.get(slot));
                    inventories.playSound(player, inventories.ITEM_LIST_OPEN_SOUND);


                } else if (inventories.ARMOR_UPGRADE_UPGRADE_CASH_SLOTS.containsValue(slot)) {
                    for (int i = 0; i < 4; i++) {
                        if (inventories.ARMOR_UPGRADE_UPGRADE_CASH_SLOTS.get(i) == slot) {
                            item = armorUpgrade.getArmor().get(i);
                            if (item == null) break;

                            double price = item.vaultPrice();
                            if (price == 0) break;

                            if (economy.has(player, price)) {
                                openInv(player, new ConfirmUpgrade(armorUpgrade, inventories, item, true,
                                        ARMOR_SLOTS[i], player.getInventory().getItem(ARMOR_SLOTS[i])));
                                inventories.playSound(player, inventories.CONFIRM_UPGRADE_OPEN_SOUND);
                            } else {
                                sendAndClose(player, messages.NO_ENOUGH_MONEY);
                            }
                            break;
                        }
                    }


                } else if (inventories.ARMOR_UPGRADE_UPGRADE_ITEM_SLOTS.containsValue(slot)) {
                    for (int i = 0; i < 4; i++) {
                        if (inventories.ARMOR_UPGRADE_UPGRADE_ITEM_SLOTS.get(i) == slot) {
                            item = armorUpgrade.getArmor().get(i);
                            if (item == null) break;

                            ItemStack price = item.itemPrice();
                            if (player.getInventory().containsAtLeast(price, price.getAmount())) {
                                openInv(player, new ConfirmUpgrade(armorUpgrade, inventories, item, false,
                                        ARMOR_SLOTS[i], player.getInventory().getItem(ARMOR_SLOTS[i])));
                                inventories.playSound(player, inventories.CONFIRM_UPGRADE_OPEN_SOUND);
                            } else {
                                sendAndClose(player, messages.NO_ITEM);
                            }
                            break;
                        }
                    }
                }
            }

            case ITEM_UPGRADE -> {
                ItemUpgrade itemUpgrade = (ItemUpgrade) e.getInventory().getHolder();

                if (slot == inventories.ITEM_UPGRADE_BACK_SLOT) {
                    openInv(player, inventories.UPGRADE_MENU_INSTANCE);
                    inventories.playSound(player, inventories.UPGRADE_MENU_OPEN_SOUND);


                } else if (slot == inventories.ITEM_UPGRADE_LIST_SLOT) {
                    openInv(player, inventories.ITEM_UPGRADE_ITEMS_LIST.get(itemUpgrade.getItemType().ordinal() - 4));
                    inventories.playSound(player, inventories.ITEM_LIST_OPEN_SOUND);


                } else if (slot == inventories.ITEM_UPGRADE_UPGRADE_CASH_SLOT) {
                    double price = itemUpgrade.getHigherItemVaultPrice();
                    if (price != 0) {
                        if (economy.has(player, price)) {
                            openInv(player, new ConfirmUpgrade(itemUpgrade, inventories, itemUpgrade.getHigherItem(), true,
                                    null, itemUpgrade.getCurrentItem()));
                            inventories.playSound(player, inventories.CONFIRM_UPGRADE_OPEN_SOUND);
                        } else {
                            sendAndClose(player, messages.NO_ENOUGH_MONEY);
                        }
                    }


                } else if (slot == inventories.ITEM_UPGRADE_UPGRADE_ITEM_SLOT) {
                    ItemStack price = itemUpgrade.getHigherItemItemPrice();
                    if (player.getInventory().containsAtLeast(price, price.getAmount())) {
                        openInv(player, new ConfirmUpgrade(itemUpgrade, inventories, itemUpgrade.getHigherItem(), false,
                                null, itemUpgrade.getCurrentItem()));
                        inventories.playSound(player, inventories.CONFIRM_UPGRADE_OPEN_SOUND);
                    } else {
                        sendAndClose(player, messages.NO_ITEM);
                    }
                }
            }

            case SELECT_ITEM -> {
                if (slot == inventories.SELECT_ITEM_BACK_SLOT) {
                    openInv(player, inventories.UPGRADE_MENU_INSTANCE);
                    inventories.playSound(player, inventories.UPGRADE_MENU_OPEN_SOUND);
                    return;
                }

                SelectItem selectItem = (SelectItem) e.getInventory().getHolder();

                Integer playerSlot = selectItem.getMenuSlotToPlayerSlot().get(slot);
                if (playerSlot == null) return;

                ItemStack real = player.getInventory().getItem(playerSlot);
                if (real == null || !real.isSimilar(clicked)) {
                    plugin.getLogger().warning("Player " + player.getName() + " tried to select item that doesn't exists in his inv");
                    plugin.getServer().getScheduler().runTask(plugin, () -> player.closeInventory());
                    return;
                }

                openInv(player, new ItemUpgrade(player, inventories, selectItem.getItemType(), real, plugin));
                inventories.playSound(player, inventories.ITEM_UPGRADE_OPEN_SOUND);
            }

            case ITEM_LIST -> {
                if (slot == inventories.ITEMS_LIST_BACK_SLOT) {
                    ItemsList itemsList = (ItemsList) e.getInventory().getHolder();
                    ItemType itemType = itemsList.getItemType();

                    switch (itemType) {
                        case HELMET, CHESTPLATE, LEGGINGS, BOOTS -> {
                            openInv(player, new ArmorUpgrade(player, inventories, plugin));
                            inventories.playSound(player, inventories.ARMOR_UPGRADE_OPEN_SOUND);
                        }
                        default -> openCategory(player, itemType);
                    }
                }
            }

            case CONFIRM_UPGRADE -> {
                ConfirmUpgrade confirmUpgrade = (ConfirmUpgrade) customMenu;

                if (slot == inventories.CONFIRM_UPGRADE_CANCEL_SLOT) {
                    openInv(player, confirmUpgrade.getPreviousMenu());
                    inventories.playSound(player, confirmUpgrade.getPreviousMenu().getOpenSound());

                } else if (slot == inventories.CONFIRM_UPGRADE_CONFIRM_SLOT) {
                    performUpgrade(player, confirmUpgrade);
                }
            }
        }
    }

    private void performUpgrade(Player player, ConfirmUpgrade confirm) {
        PlayerInventory inv = player.getInventory();
        Item higher = confirm.getHigherItem();
        EquipmentSlot armorSlot = confirm.getArmorSlot();

        int itemSlot = -1;
        if (armorSlot != null) {
            if (!sameBase(inv.getItem(armorSlot), confirm.getUpgradingItem())) {
                openInv(player, inventories.UPGRADE_MENU_INSTANCE);
                inventories.playSound(player, inventories.UPGRADE_MENU_OPEN_SOUND);
                return;
            }
        } else {
            itemSlot = findSlot(player, confirm.getUpgradingItem());
            if (itemSlot == -1) {
                openInv(player, inventories.UPGRADE_MENU_INSTANCE);
                inventories.playSound(player, inventories.UPGRADE_MENU_OPEN_SOUND);
                return;
            }
        }

        if (!takePayment(player, higher, confirm.isPayWithMoney())) {
            ConfigMessage notEnough = confirm.isPayWithMoney() ? messages.NO_ENOUGH_MONEY : messages.NO_ITEM;
            sendAndClose(player, notEnough, () -> rebuildPreviousMenu(player, confirm, confirm.getUpgradingItem()));
            return;
        }

        ItemStack current = armorSlot != null ? inv.getItem(armorSlot) : inv.getItem(itemSlot);
        ItemStack upgraded = higher.item().clone();
        if (current != null && !current.getType().isAir()) {
            copyWear(current, upgraded);
        }

        if (armorSlot != null) {
            inv.setItem(armorSlot, upgraded);
        } else {
            inv.setItem(itemSlot, upgraded);
        }

        sendAndClose(player, messages.UPGRADED, () -> rebuildPreviousMenu(player, confirm, upgraded),
                Placeholder.unparsed("level", String.valueOf(higher.level())));
    }

    private boolean takePayment(Player player, Item higher, boolean money) {
        if (money) {
            double price = higher.vaultPrice();
            return price != 0 && economy.has(player, price) && economy.withdrawPlayer(player, price).transactionSuccess();
        }

        ItemStack price = higher.itemPrice();
        PlayerInventory inv = player.getInventory();
        if (!inv.containsAtLeast(price, price.getAmount())) return false;

        Map<Integer, ItemStack> notRemoved = inv.removeItem(price.clone());
        if (notRemoved.isEmpty()) return true;

        int removed = price.getAmount() - notRemoved.values().iterator().next().getAmount();
        if (removed > 0) {
            ItemStack back = price.clone();
            back.setAmount(removed);
            inv.addItem(back);
        }
        return false;
    }

    private CustomMenu rebuildPreviousMenu(Player player, ConfirmUpgrade confirm, ItemStack currentItem) {
        if (confirm.getPreviousMenu() instanceof ItemUpgrade itemUpgrade) {
            return new ItemUpgrade(player, inventories, itemUpgrade.getItemType(), currentItem, plugin);
        }
        return new ArmorUpgrade(player, inventories, plugin);
    }

    private void copyWear(ItemStack from, ItemStack to) {
        if (!(from.getItemMeta() instanceof Damageable fromMeta)) return;
        int damage = fromMeta.getDamage();

        to.editMeta(Damageable.class, meta -> {
            int max = meta.hasMaxDamage() ? meta.getMaxDamage() : to.getType().getMaxDurability();
            meta.setDamage(max > 0 ? Math.min(damage, max - 1) : damage);
        });
    }

    private boolean sameBase(ItemStack a, ItemStack b) {
        boolean aEmpty = a == null || a.getType().isAir();
        boolean bEmpty = b == null || b.getType().isAir();
        if (aEmpty || bEmpty) return aEmpty && bEmpty;

        ItemStack aClone = a.clone();
        aClone.editMeta(Damageable.class, meta -> meta.setDamage(0));
        ItemStack bClone = b.clone();
        bClone.editMeta(Damageable.class, meta -> meta.setDamage(0));
        return aClone.isSimilar(bClone);
    }

    private int findSlot(Player player, ItemStack target) {
        PlayerInventory inv = player.getInventory();

        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && stack.isSimilar(target)) return i;
        }
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && sameBase(stack, target)) return i;
        }
        return -1;
    }

    private void openCategory(Player player, ItemType type) {
        Map<Integer, ItemStack> itemsToSelect = getItemsToSelect(player, type);

        if (itemsToSelect.isEmpty()) {
            sendAndClose(player, messages.NO_ITEM_TO_UPGRADE);

        } else if (itemsToSelect.size() == 1) {
            for (Integer slot : itemsToSelect.keySet()) {
                openInv(player, new ItemUpgrade(player, inventories, type, itemsToSelect.get(slot), plugin));
                inventories.playSound(player, inventories.ITEM_UPGRADE_OPEN_SOUND);
            }

        } else {
            openInv(player, new SelectItem(itemsToSelect, type, inventories));
            inventories.playSound(player, inventories.SELECT_ITEM_OPEN_SOUND);
        }
    }


    private Map<Integer, ItemStack> getItemsToSelect(Player player, ItemType type) {
        List<Item> itemsOfType = plugin.getItems().get(type.ordinal());

        Map<Integer, ItemStack> items = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            ItemStack playerItem = player.getInventory().getItem(i);
            if (playerItem == null) continue;

            ItemStack playerItemClone = playerItem.clone();
            playerItemClone.editMeta(Damageable.class, meta -> meta.setDamage(0));

            for (Item item : itemsOfType) {
                if (item.item().isSimilar(playerItemClone)) {
                    items.put(i, playerItem);
                    break;
                }
            }
        }
        return items;
    }


    private void sendAndClose(Player player, ConfigMessage configMessage) {
        configMessage.send(player);
        if (configMessage.closeInventory()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> player.closeInventory());
        }
    }

    private void sendAndClose(Player player, ConfigMessage configMessage, Supplier<CustomMenu> menuIfNotClosing,
                              TagResolver... resolvers) {
        configMessage.send(player, resolvers);
        if (configMessage.closeInventory()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> player.closeInventory());
        } else {
            openInv(player, menuIfNotClosing.get());
        }
    }


    private void openInv(Player player, CustomMenu inv) {
        plugin.getServer().getScheduler().runTask(plugin, () -> player.openInventory(inv.getInventory()));
    }
}