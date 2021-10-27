package me.badbones69.crazyenvoy.sync.handler;

import me.badbones69.crazyenvoy.Main;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.sync.MessageType;
import net.spacedelta.lib.Library;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.message.MessageHandler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class TimeHandler implements MessageHandler {

    private static final CrazyEnvoy ENVOY = CrazyEnvoy.getInstance();
    private static long timeTillEnvoy;

    @Override
    public void handle(@NotNull DataBuffer data) {
        timeTillEnvoy = data.readNumber("envoy-time").longValue();
    }

    public static void startTask() {
        Bukkit.getScheduler().runTaskTimer(ENVOY.getPlugin(), () -> {
            if (ENVOY.isEnvoyActive()) {
                sendTimeUpdate(-330);
            } else {
                sendTimeUpdate(ENVOY.getNextEnvoy().getTimeInMillis() - System.currentTimeMillis());
            }
        }, 0, 10);
    }

    private static void sendTimeUpdate(long time) {
        var data = DataBuffer.create()
                .write("envoy-time", time);

        Library.get().getMessageBus().fire(Main.INSTANCE, MessageType.TIME, data);
    }

    public static long getTimeTillEnvoy() {
        return timeTillEnvoy;
    }
}
