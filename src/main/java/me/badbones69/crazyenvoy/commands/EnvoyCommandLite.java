package me.badbones69.crazyenvoy.commands;

import me.badbones69.crazyenvoy.Main;
import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.enums.Messages;
import me.badbones69.crazyenvoy.api.objects.Flare;
import me.badbones69.crazyenvoy.sync.MessageType;
import net.spacedelta.lib.Library;
import net.spacedelta.lib.data.DataBuffer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EnvoyCommandLite implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command c, @NotNull String l, @NotNull String[] args) {
        if (sender.hasPermission("envoy.flare.give")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("time")) {
                    requestTime(sender);
                }
                return true;
            }

            int amount = 1;
            Player player;
            if (args.length >= 2) {
                if (Methods.isInt(args[1])) {
                    amount = Integer.parseInt(args[1]);
                } else {
                    Messages.NOT_A_NUMBER.sendMessage(sender);
                    return true;
                }
            }
            if (args.length >= 3) {
                if (Methods.isOnline(args[2])) {
                    player = Methods.getPlayer(args[2]);
                } else {
                    Messages.NOT_ONLINE.sendMessage(sender);
                    return true;
                }
            } else {
                if (!(sender instanceof Player)) {
                    Messages.PLAYERS_ONLY.sendMessage(sender);
                    return true;
                } else {
                    player = (Player) sender;
                }
            }

            sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "+" + amount + " Envoy Flare");
            if (!sender.getName().equalsIgnoreCase(player.getName())) {
                sender.sendMessage(ChatColor.GREEN + "Given " + player.getName() + " x" + amount + " envoy flare(s).");
            }

            Flare.giveFlare(player, amount);
            return true;
        }
        else {
            requestTime(sender);
        }
        return true;
    }

    private void requestTime(CommandSender sender) {
        var data = DataBuffer.create()
                .write("uuid", ((OfflinePlayer) sender).getUniqueId().toString());

        Library.get().getMessageBus().fire(Main.INSTANCE, MessageType.TIME_REQUEST, data);
    }
}