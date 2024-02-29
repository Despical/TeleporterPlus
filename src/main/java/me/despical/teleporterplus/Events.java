package me.despical.teleporterplus;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.serializer.LocationSerializer;
import me.despical.teleporterplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author Despical
 * <p>
 * Created at 23.02.2024
 */
public class Events implements Listener {

    private final Main plugin;

    public Events(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = ConfigUtils.getConfig(plugin, "data");

        config.set(player.getUniqueId() + ".last-location." + player.getWorld().getName(), LocationSerializer.toString(player.getLocation()));
        ConfigUtils.saveConfig(plugin, config, "data");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = ConfigUtils.getConfig(plugin, "data");
        String path = player.getUniqueId() + ".last-location." + player.getWorld().getName();

        if (config.isSet(path)) {
            Location location = LocationSerializer.fromString(config.getString(path));

            if (location != null)
                player.teleport(location);
        }
    }

    @EventHandler
    public void teleportEvent(PlayerTeleportEvent event) {
        FileConfiguration config = ConfigUtils.getConfig(plugin, "data");
        String path = event.getPlayer().getUniqueId() + ".last-locations." + event.getTo().getWorld().getName();

        if (config.isSet(path)) {
            Location location = LocationSerializer.fromString(config.getString(path));

            if (location == null) return;

            event.setTo(location);
        }
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent event) {
        FileConfiguration config = ConfigUtils.getConfig(plugin, "data");
        Player player = event.getPlayer();
        String path = player.getUniqueId() + ".last-locations." + player.getWorld().getName();

        if (plugin.getConfig().getBoolean("random-tp-on-death")) {
            Location randomLocation = Utils.pickLocation(player, player.getWorld());

            if (randomLocation == null) {
                Location fallbackLocation = LocationSerializer.fromString(plugin.getConfig().getString("fallback-location", LocationSerializer.SERIALIZED_LOCATION));

                if (!fallbackLocation.equals(LocationSerializer.DEFAULT_LOCATION)) {
                    player.teleport(fallbackLocation);
                    return;
                }

                return;
            }

            event.setRespawnLocation(randomLocation);
            return;
        }

        if (config.isSet(path)) {
            Location location = LocationSerializer.fromString(config.getString(path));

            if (location == null) return;

            event.setRespawnLocation(location);
        }
    }
}