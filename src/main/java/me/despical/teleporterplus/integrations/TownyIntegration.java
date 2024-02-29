package me.despical.teleporterplus.integrations;

import com.palmergames.bukkit.towny.TownyAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Despical
 * <p>
 * Created at 24.02.2024
 */
public class TownyIntegration implements Integration {

    @Override
    public boolean checkLocation(Player player, Location location) {
        return TownyAPI.getInstance().isWilderness(location);
    }
}