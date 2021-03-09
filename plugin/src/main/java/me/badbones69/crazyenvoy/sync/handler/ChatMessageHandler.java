package me.badbones69.crazyenvoy.sync.handler;

import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.message.MessageHandler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChatMessageHandler implements MessageHandler {

    @Override
    public void handle(@NotNull DataBuffer data) {
        var player = Bukkit.getPlayer(UUID.fromString(data.readString("uuid")));

        if (player != null) {
            player.sendMessage(data.readString("message"));
        }
    }
}
