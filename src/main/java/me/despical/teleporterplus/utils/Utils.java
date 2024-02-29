package me.despical.teleporterplus.utils;

import me.despical.commons.util.Strings;
import me.despical.teleporterplus.Main;
import me.despical.teleporterplus.integrations.Integration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

/**
 * @author Despical
 * <p>
 * Created at 23.02.2024
 */
public class Utils {

    private static final Main plugin = JavaPlugin.getPlugin(Main.class);
    private static final Random random = new Random();

    public static Location pickLocation(Player player, World world) {
        String worldName = world.getName();
        List<String> unsafeMaterials = plugin.getConfig().getStringList("unsafe-blocks");

        boolean worldBorder = plugin.getConfig().getBoolean("rtp.world-border");

        int xMin = worldBorder ? (int) world.getWorldBorder().getSize() / 2 : plugin.getConfig().getInt(String.format("rtp.worlds.%s.min-x", worldName));
        int zMin = worldBorder ? (int) world.getWorldBorder().getSize() / 2 : plugin.getConfig().getInt(String.format("rtp.worlds.%s.min-z", worldName));
        int yMin = plugin.getConfig().getInt(String.format("rtp.worlds.%s.min-y", worldName));

        int xMax = worldBorder ? (int) world.getWorldBorder().getSize() / 2 : plugin.getConfig().getInt(String.format("rtp.worlds.%s.max-x", worldName));
        int zMax = worldBorder ? (int) world.getWorldBorder().getSize() / 2 : plugin.getConfig().getInt(String.format("rtp.worlds.%s.max-z", worldName));
        int yMax = plugin.getConfig().getInt(String.format("rtp.worlds.%s.max-y", worldName));

        int maxTry = plugin.getConfig().getInt("rtp.max-try");
        int i = 0;

        int x = random.nextInt(xMax- xMin) + xMin;
        int z = random.nextInt(zMax - zMin) + zMin;
        int y = world.getHighestBlockYAt(x, z);

        List<Integration> integrations = plugin.getIntegrations();

        while (true) {
            if (++i == maxTry) return null;

            if (y >= yMax) {
                for (int curY = yMax - 2; curY >= yMin; curY = curY - 1) {
                    String higherBlock = world.getBlockAt(x, curY + 2, z).getType().name();
                    String generatedBlock = world.getBlockAt(x, curY, z).getType().name();

                    if (unsafeMaterials.contains(generatedBlock) || unsafeMaterials.contains(higherBlock))
                        continue;

                    Location location = new Location(world, x + .5, curY, z + 0.5);

                    if (integrations.stream().anyMatch(integration -> !integration.checkLocation(player, location)))
                        return null;
                    return location;
                }
            }

            Location location  = new Location(world, x + .5, y, z + .5);
            String higherBlock = location.getBlock().getType().name();
            String generatedBlock = location.clone().add(0, 2, 0).getBlock().getType().name();

            if (unsafeMaterials.contains(generatedBlock) || unsafeMaterials.contains(higherBlock))
                continue;

            if (integrations.stream().anyMatch(integration -> !integration.checkLocation(player, location)))
                return null;
            return location;
        }
    }

    public static void sendMessage(CommandSender sender, String path) {
        String message = plugin.getConfig().getString(path);
        sender.sendMessage(Strings.format(message));
    }
}