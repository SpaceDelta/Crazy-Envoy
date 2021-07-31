package me.badbones69.crazyenvoy.sync.handler;

import me.badbones69.crazyenvoy.Main;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.enums.Messages;
import me.badbones69.crazyenvoy.sync.MessageType;
import net.spacedelta.lib.Library;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.message.MessageHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TimeRequestHandler implements MessageHandler {

    private static final CrazyEnvoy ENVOY = CrazyEnvoy.getInstance();

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = data.readString("uuid");
        var placeholders = new HashMap<String, String>();

        if (ENVOY.isEnvoyActive()) {
            placeholders.put("%time%", ENVOY.getEnvoyRunTimeLeft());
            placeholders.put("%Time%", ENVOY.getEnvoyRunTimeLeft());
            sendMessage(uuid, Messages.TIME_LEFT.getMessage(placeholders, true));
        } else {
            placeholders.put("%time%", ENVOY.getNextEnvoyTime());
            sendMessage(uuid, Messages.TIME_TILL_EVENT.getMessage(placeholders, true));
        }
    }

    private void sendMessage(@NotNull String uuid, @NotNull String message) {
        var data = DataBuffer.create()
                .write("uuid", uuid)
                .write("message", message);

        Library.get().getMessageBus().fire(Main.INSTANCE, MessageType.CHAT_MESSAGE, data);
    }
}
