package dev.spike.spikeArmorUpgrades.utils.config;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

public class ConfigMessage {
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final String chat;
    private final String title;
    private final String subtitle;
    private final String sound;
    private final boolean closeInventory;

    public ConfigMessage(String chat, String title, String subtitle, String sound, boolean closeInventory) {
        this.chat = chat;
        this.title = title;
        this.subtitle = subtitle;
        this.sound = sound;
        this.closeInventory = closeInventory;
    }

    public boolean closeInventory() {
        return closeInventory;
    }

    public void send(Player player, TagResolver... resolvers) {
        if (!chat.isEmpty()) {
            player.sendMessage(MM.deserialize(chat, resolvers));
        }
        if (!title.isEmpty() || !subtitle.isEmpty()) {
            player.showTitle(Title.title(
                    MM.deserialize(title, resolvers),
                    MM.deserialize(subtitle, resolvers)));
        }
        if (sound != null) {
            player.playSound(player, sound, 1.0f, 1.0f);
        }
    }
}