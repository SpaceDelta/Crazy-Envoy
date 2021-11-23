package me.badbones69.crazyenvoy.sync.handler;

import me.badbones69.crazyenvoy.Main;
import me.badbones69.crazyenvoy.sync.MessageType;
import net.spacedelta.lib.Library;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.message.MessageHandler;
import net.spacedelta.lib.schedule.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * State handler.
 *
 * Only run on client side.
 */
public class ClientStateHandler implements MessageHandler {

    private static final String BAR_MESSAGE = ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString()
            + "ON-GOING ENVOY" + ChatColor.WHITE + " Join the event at " + ChatColor.LIGHT_PURPLE +  "/pvp";
    private static final NamespacedKey ENVOY_KEY = NamespacedKey.minecraft("crazyenvoy");

    private Task<?> notifyTask;
    private int times;

    @Override
    public void handle(@NotNull DataBuffer dataBuffer) {
        // game start
        if (dataBuffer.readNumber("state").intValue() == 1) {
            startTask();
        } else {
            // game ends
            if (notifyTask != null) {
                notifyTask.cancel();
                notifyTask = null;
            }

            final KeyedBossBar bossBar = Bukkit.getBossBar(ENVOY_KEY);
            if (bossBar != null)
                bossBar.removeAll();
        }
    }

    private void startTask() {
        if (notifyTask != null)
            return;

        times = 30;
        notifyTask = Task.builder()
                .repeat(30, TimeUnit.SECONDS)
                .execute(() -> {
                    BossBar bossBar = Bukkit.getBossBar(ENVOY_KEY);
                    if (bossBar == null) {
                        bossBar = Bukkit.createBossBar(ENVOY_KEY, BAR_MESSAGE, BarColor.PURPLE, BarStyle.SOLID);
                    }

                    Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
                    bossBar.setVisible(true);
                    bossBar.setProgress(1.0);

                    if (times <= 1) {
                        Bukkit.removeBossBar(ENVOY_KEY);
                        bossBar.setVisible(false);
                    }
                    else {
                        times--;
                    }
                })
                .schedule();
    }

    public static void submitStateChange(boolean running) {
        var data = DataBuffer.create()
                .write("state", (running ? 1 : 0));

        Library.get().getMessageBus().fire(Main.INSTANCE, MessageType.STATE, data);
    }

}
