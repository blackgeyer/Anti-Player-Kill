package org.antipk;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class MessageUtil {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final PlainComponentSerializer PLAIN = PlainComponentSerializer.plain();

    public static void sendMessage(CommandSender sender, String text) {
        if (sender == null || text == null || text.isEmpty()) return;

        Component component = MM.deserialize(text);

        if (sender instanceof ConsoleCommandSender console) {
            String plainText = PLAIN.serialize(component);
            console.sendMessage(plainText);
        } else if (sender instanceof Audience audience) {
            audience.sendMessage(component);
        } else {
            sender.sendMessage(component);
        }
    }
}