package dev.spike.spikeArmorUpgrades.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import dev.spike.spikeArmorUpgrades.inventoryHolders.UpgradeMenu;
import dev.spike.spikeArmorUpgrades.utils.config.Inventories;
import dev.spike.spikeArmorUpgrades.utils.config.Messages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class Upgrade {
    private static final String PERMISSION = "spike-armor-upgrades.command.upgrade";

    public static LiteralCommandNode<CommandSourceStack> create(SpikeArmorUpgrades plugin, Messages messages, Inventories inventories) {
        return Commands.literal("upgrade")
                .requires(src -> src.getSender().hasPermission(PERMISSION))

                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage(messages.ONLY_PLAYERS);
                        return 0;
                    }

                    // when on enable or on reload sets don't load correctly
                    // (items and item-prices in /sets.<set>.yml can't be null,
                    // levels can't be duplicated or not consecutive and must start from 1)
                    if (!plugin.areSetsCorrect()) {
                        player.sendMessage(messages.LOADING_SETS_ERROR);
                        return 0;
                    }

                    player.openInventory(inventories.UPGRADE_MENU_INSTANCE.getInventory());
                    inventories.playSound(player, inventories.UPGRADE_MENU_OPEN_SOUND);
                    return Command.SINGLE_SUCCESS;

                }).build();
    }
}
