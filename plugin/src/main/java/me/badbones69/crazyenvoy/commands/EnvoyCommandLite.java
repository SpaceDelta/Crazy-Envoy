package me.badbones69.crazyenvoy.commands;

import me.badbones69.crazyenvoy.Main;
import me.badbones69.crazyenvoy.sync.MessageType;
import net.spacedelta.lib.Library;
import net.spacedelta.lib.data.DataBuffer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EnvoyCommandLite implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command c, @NotNull String l, @NotNull String[] args) {
        var data = DataBuffer.create()
                .write("uuid", ((OfflinePlayer) sender).getUniqueId().toString());

        Library.get().getMessageBus().fire(Main.INSTANCE, MessageType.TIME_REQUEST, data);
        return true;
    }
}