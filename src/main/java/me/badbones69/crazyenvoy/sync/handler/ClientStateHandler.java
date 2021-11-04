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
    private Task<?> notifyTask;
    private int times;

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

        times = 30;
        notifyTask = Task.builder()
                .repeat(30, TimeUnit.SECONDS)
                .execute(() -> {
                    BossBar bossBar = Bukkit.getBossBar(NamespacedKey.minecraft("crazyenvoy"));
                    if (bossBar == null) {
                        bossBar = Bukkit.createBossBar(NamespacedKey.minecraft("crazyenvoy"), BAR_MESSAGE, BarColor.PURPLE, BarStyle.SOLID);
                    }

                    Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
                    bossBar.setVisible(true);
                    bossBar.setProgress(1.0);

                    if (times <= 1) {
                        Bukkit.removeBossBar(NamespacedKey.minecraft("crazyenvoy"));
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
