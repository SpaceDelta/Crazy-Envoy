package me.badbones69.crazyenvoy.bossbar;

import me.badbones69.crazyenvoy.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Boss bar to track crates to pickup.
 *
 * Listens and adds people entering the world.
 */
public class EnvoyBossbarTracker implements Listener {

    private BossBar bossBar;
    private World world;
    private int initialCrates;

    /**
     * Starts a new boss bar.
     *
     * Adds all world players and registers as listener.
     *
     * @param world world
     * @param crates crates to pick up
     */
    public void init(@NotNull World world, int crates) {
        bossBar = Bukkit.createBossBar(
                ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString()
                        + "ON-GOING ENVOY " + ChatColor.WHITE + crates + " Crates Left",
                BarColor.PURPLE,
                BarStyle.SOLID
        );
        this.world = world;

        world.getPlayers().forEach(player -> bossBar.addPlayer(player));
        this.initialCrates = crates;
        Main.INSTANCE.registerListeners(this);
    }

    /**
     * Trigger an update on the boss bar
     *
     * Is null-safe.
     *
     * @param crates crates left to pick up
     */
    public void triggerUpdate(Player player, int crates) {
        if (bossBar != null) {
            bossBar.setProgress((float) crates / initialCrates);
            bossBar.setTitle(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString()
                    + "ON-GOING ENVOY " + ChatColor.WHITE + " A Crate Got Picked Up By " + ChatColor.LIGHT_PURPLE + player.getName());

            Bukkit.getScheduler().runTaskLater(Main.INSTANCE, () -> bossBar.setTitle(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString()
                    + "ON-GOING ENVOY " + ChatColor.WHITE + crates + " Crates Left"), 45);
        }
    }

    /**
     * Removes from all players
     */
    public void destroy() {
        bossBar.removeAll();
        bossBar = null;
        world = null;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (bossBar != null) {
            bossBar.addPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent event) {
        if (bossBar != null && event.getPlayer().getWorld().equals(world)) {
            bossBar.addPlayer(event.getPlayer());
        } else if (bossBar != null && event.getFrom().equals(world))
            bossBar.removePlayer(event.getPlayer());
    }

    @Nullable
    public BossBar getBossBar() {
        return bossBar;
    }

    @Nullable
    public World getWorld() {
        return world;
    }
}
