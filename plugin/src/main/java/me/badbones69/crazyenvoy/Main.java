package me.badbones69.crazyenvoy;

import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent.EnvoyEndReason;
import me.badbones69.crazyenvoy.api.objects.Flare;
import me.badbones69.crazyenvoy.commands.EnvoyCommand;
import me.badbones69.crazyenvoy.commands.EnvoyCommandLite;
import me.badbones69.crazyenvoy.controllers.*;
import me.badbones69.crazyenvoy.multisupport.PlaceholderAPISupport;
import me.badbones69.crazyenvoy.multisupport.Support;
import me.badbones69.crazyenvoy.multisupport.Version;
import me.badbones69.crazyenvoy.multisupport.holograms.HolographicSupport;
import me.badbones69.crazyenvoy.sync.MessageType;
import me.badbones69.crazyenvoy.sync.handler.ChatMessageHandler;
import me.badbones69.crazyenvoy.sync.handler.ClientStateHandler;
import me.badbones69.crazyenvoy.sync.handler.TimeRequestHandler;
import net.spacedelta.lib.network.data.model.server.ServerGroup;
import net.spacedelta.lib.plugin.BukkitPlugin;
import net.spacedelta.lib.plugin.PluginSide;
import net.spacedelta.lib.plugin.annotation.Instance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends BukkitPlugin implements Listener {

    @Instance
    public static Main INSTANCE;

    private FileManager fileManager = FileManager.getInstance();
    private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
    
    @Override
    public void enable() {
        PluginManager pm = Bukkit.getPluginManager();

        if (getSide() == PluginSide.CLIENT) {
            getCommand("envoy").setExecutor(new EnvoyCommandLite());
            getLibrary().getMessageBus().registerHandler(this, MessageType.CHAT_MESSAGE, new ChatMessageHandler());
            getLibrary().getMessageBus().registerHandler(this, MessageType.STATE, new ClientStateHandler());

            fileManager.setup(this);
            Flare.load();
        } else {

            String homeFolder = Version.isNewer(Version.v1_12_R1) ? "/Tiers1.13-Up" : "/Tiers1.12.2-Down";
            fileManager.logInfo(true)
                    .registerCustomFilesFolder("/Tiers")
                    .registerDefaultGenerateFiles("Basic.yml", "/Tiers", homeFolder)
                    .registerDefaultGenerateFiles("Lucky.yml", "/Tiers", homeFolder)
                    .registerDefaultGenerateFiles("Titan.yml", "/Tiers", homeFolder)
                    .setup(this);

            envoy.load();
            Methods.hasUpdate();
            new Metrics(this);

            pm.registerEvents(new EditControl(), this);
            pm.registerEvents(new EnvoyControl(), this);

            try {
                if (Version.isNewer(Version.v1_10_R1)) {
                    pm.registerEvents(new FireworkDamageAPI(this), this);
                }
            } catch (Exception e) {
            }
            if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
                HolographicSupport.registerPlaceHolders();
            }
            if (Support.PLACEHOLDER_API.isPluginLoaded()) {
                new PlaceholderAPISupport(this).register();
            }

            getCommand("envoy").setExecutor(new EnvoyCommand());
            getLibrary().getMessageBus().registerHandler(this, MessageType.TIME_REQUEST, new TimeRequestHandler());
        }

        pm.registerEvents(new FlareControl(), this);
    }
    
    @Override
    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (EditControl.isEditor(player)) {
                EditControl.removeEditor(player);
                EditControl.removeFakeBlocks(player);
            }
        }
        if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
            HolographicSupport.unregisterPlaceHolders();
        }
        if (envoy.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.SHUTDOWN);
            Bukkit.getPluginManager().callEvent(event);
            envoy.endEnvoyEvent();
        }
        envoy.unload();
    }

    public int getOnlinePlayers() {
        return getLibrary().getNetworkManager().getOnlineGroup(ServerGroup.SKYBLOCK);
    }
    
}