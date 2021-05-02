package me.badbones69.crazyenvoy.sync.handler;

import me.badbones69.crazyenvoy.Main;
import me.badbones69.crazyenvoy.sync.MessageType;
import net.spacedelta.lib.Library;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.message.MessageHandler;
import net.spacedelta.lib.schedule.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * State handler.
 *
 * Only run on client side.
 */
public class ClientStateHandler implements MessageHandler {

    private static final String BAR_MESSAGE = ChatColor.RED + ChatColor.BOLD.toString()
            + "ON-GOING ENVOY" + ChatColor.WHITE + " Join the event at " + ChatColor.RED + ChatColor.BOLD.toString() +  "/pvp";
    private Task<?> notifyTask;

    @Override
    public void handle(@NotNull DataBuffer dataBuffer) {
        System.out.println("input "+ dataBuffer.serialize());
        // game start
        if (dataBuffer.readNumber("state").intValue() == 1) {
            startTask();
        } else {
            // game ends
            if (notifyTask != null)
                notifyTask.cancel();
        }
    }

    private void startTask() {
        if (notifyTask != null)
            return;

        notifyTask = Task.builder()
                .repeat(30)
                .execute(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(BAR_MESSAGE)))
                .schedule();
    }

    public static void submitStateChange(boolean running) {
        var data = DataBuffer.create()
                .write("state", (running ? 1 : 0));

        Library.get().getMessageBus().fire(Main.INSTANCE, MessageType.STATE, data);
    }

}
