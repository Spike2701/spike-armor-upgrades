package dev.spike.spikeArmorUpgrades.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.spike.spikeArmorUpgrades.SpikeArmorUpgrades;
import dev.spike.spikeArmorUpgrades.utils.ItemType;
import dev.spike.spikeArmorUpgrades.utils.config.Messages;
import dev.spike.spikeArmorUpgrades.utils.SetsManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

import java.io.File;

public class Sau {
    private static final String PERMISSION = "spike-armor-upgrades.command.sau";

    public static LiteralCommandNode<CommandSourceStack> create(SpikeArmorUpgrades plugin, Messages messages, SetsManager setsManager) {
        // files in 'sets' folder
        SuggestionProvider<CommandSourceStack> setsSuggestion = (ctx, builder) -> {
            File[] sets = new File(plugin.getDataFolder(), "sets").listFiles();
            if (sets != null) {
                if (sets.length == 0) {
                    builder.suggest("No available sets!");
                } else {
                    for (File set : sets) {
                        builder.suggest(set.getName().replace(".yml", ""));
                    }
                }
            }
            return builder.buildFuture();
        };

        // Item types, e.g. helmet, chestplate, leggings...
        SuggestionProvider<CommandSourceStack> typesSuggestion = (ctx, builder) -> {
            for (ItemType type : ItemType.values()) {
                builder.suggest(type.toString().toLowerCase());
            }
            return builder.buildFuture();
        };


        return Commands.literal("sau")
                .requires(src -> src.getSender().hasPermission(PERMISSION))

                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage(messages.HELP);
                    return Command.SINGLE_SUCCESS;
                })


                .then(Commands.literal("help")
                        .executes(ctx -> {
                            ctx.getSource().getSender().sendMessage(messages.HELP);
                            return Command.SINGLE_SUCCESS;
                        }))


                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            plugin.reloadPlugin();
                            ctx.getSource().getSender().sendMessage(messages.RELOADED);
                            return Command.SINGLE_SUCCESS;
                        }))


                .then(Commands.literal("list")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();

                            return setsManager.sendList(sender);
                        }))


                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            int level = IntegerArgumentType.getInteger(ctx, "level");
                                            CommandSender sender = ctx.getSource().getSender();

                                            return setsManager.create(name, level, sender);
                                        }))))


                    .then(Commands.literal("delete")
                            .then(Commands.argument("name", StringArgumentType.word())
                                    .suggests(setsSuggestion)

                                    .executes(ctx -> {
                                        String name = StringArgumentType.getString(ctx, "name");
                                        CommandSender sender = ctx.getSource().getSender();

                                        return setsManager.delete(name, sender);
                                    })))


                    .then(Commands.literal("changeLevel")
                            .then(Commands.argument("name", StringArgumentType.word())
                                    .suggests(setsSuggestion)

                                    .then(Commands.argument("newLevel", IntegerArgumentType.integer(1))
                                            .executes(ctx -> {
                                                String name = StringArgumentType.getString(ctx, "name");
                                                int newLevel = IntegerArgumentType.getInteger(ctx, "newLevel");
                                                CommandSender sender = ctx.getSource().getSender();

                                                return setsManager.changeLevel(name, newLevel, sender);
                                            }))))


                    .then(Commands.literal("setItem")
                            .then(Commands.argument("name", StringArgumentType.word())
                                    .suggests(setsSuggestion)

                                    .then(Commands.argument("type", StringArgumentType.word())
                                            .suggests(typesSuggestion)

                                            .then(Commands.argument("price", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> {
                                                        String name = StringArgumentType.getString(ctx, "name");
                                                        String type = StringArgumentType.getString(ctx, "type").toLowerCase();
                                                        int price = IntegerArgumentType.getInteger(ctx, "price");
                                                        CommandSender sender = ctx.getSource().getSender();

                                                        return setsManager.setItem(name, type, price, sender);
                                                    })))))


                .then(Commands.literal("changePrice")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests(setsSuggestion)

                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests(typesSuggestion)

                                        .then(Commands.argument("newPrice", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {
                                                    String name = StringArgumentType.getString(ctx, "name");
                                                    String type = StringArgumentType.getString(ctx, "type").toLowerCase();
                                                    int newPrice = IntegerArgumentType.getInteger(ctx, "newPrice");
                                                    CommandSender sender = ctx.getSource().getSender();

                                                    return setsManager.changePrice(name, type, newPrice, sender);
                                                })))))


                .build();
    }
}