package me.despical.teleporterplus.integrations;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Despical
 * <p>
 * Created at 24.02.2024
 */
public class GriefPreventionIntegration implements Integration {

    @Override
    public boolean checkLocation(Player player, Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, true, null) == null;
    }
}