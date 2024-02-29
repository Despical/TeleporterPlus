package me.despical.teleporterplus.integrations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Despical
 * <p>
 * Created at 24.02.2024
 */
public interface Integration {

    boolean checkLocation(Player player, Location location);
}