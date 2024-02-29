package me.despical.teleporterplus;

import me.despical.commons.compat.Titles;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.number.NumberUtils;
import me.despical.commons.serializer.LocationSerializer;
import me.despical.commons.util.Strings;
import me.despical.teleporterplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * @author Despical
 * <p>
 * Created at 24.02.2024
 */
public class TeleportTask {

    private final Main plugin;

    public TeleportTask(Main plugin, Player player, Location location) {
        this.plugin = plugin;

        Utils.sendMessage(player, "rtp.messages.wait-to-tp");
        sendTitle(player, "rtp.messages.titles.wait-to-tp-title", "rtp.messages.titles.wait-to-tp-subtitle");

        new BukkitRunnable() {

            int time = plugin.getConfig().getInt("rtp.time");
            Location lastLocation = player.getLocation();
            List<String> messages = plugin.getConfig().getStringList("rtp.messages.teleporting-in");

            @Override
            public void run() {
                if (!checkLocation(lastLocation, player.getLocation())) {
                    Utils.sendMessage(player, "rtp.messages.cancelled");
                    sendTitle(player, "rtp.messages.titles.cancelled-title", "rtp.messages.titles.cancelled-subtitle");

                    cancel();
                    return;
                }

                if (--time == 0) {
                    player.teleport(location);

                    cancel();

                    FileConfiguration config = ConfigUtils.getConfig(plugin, "data");
                    config.set(player.getUniqueId() + ".last-locations." + location.getWorld().getName(), LocationSerializer.toString(lastLocation));
                    ConfigUtils.saveConfig(plugin, config, "data");

                    Utils.sendMessage(player, "rtp.messages.teleported");
                    sendTitle(player, "rtp.messages.titles.teleported-title", "rtp.messages.titles.teleported-subtitle");
                    return;
                }

                for (String message : messages) {
                    String[] split = message.split(":");
                    int seconds = NumberUtils.getInt(split[0]);

                    if (time == seconds) {
                        player.sendMessage(Strings.format(split[1]));

                        sendTitles(player, split);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private boolean checkLocation(Location lastLocation, Location currentLocation) {
        return lastLocation.getX() == currentLocation.getX() && lastLocation.getY() == currentLocation.getY() && lastLocation.getZ() == currentLocation.getZ();
    }

    private void sendTitles(Player player, String[] split) {
        if ("title".equals(split[2]) && "subtitle".equals(split[4])) {
            Titles.sendTitle(player, 10, 40, 10, Strings.format(split[3]), Strings.format(split[5]));
        }
    }

    private void sendTitle(Player player, String titlePath, String subtitlePath) {
        Titles.sendTitle(player, 10, 40, 10, Strings.format(plugin.getConfig().getString(titlePath)), Strings.format(plugin.getConfig().getString(subtitlePath)));
    }
}