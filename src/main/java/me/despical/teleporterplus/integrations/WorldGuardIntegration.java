package me.despical.teleporterplus.integrations;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Despical
 * <p>
 * Created at 24.02.2024
 */
public class WorldGuardIntegration implements Integration {

    private StateFlag rtpFlag;

    public WorldGuardIntegration(){
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            StateFlag flag = new StateFlag("RtpEnabled", true);
            registry.register(flag);

            rtpFlag = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("RtpEnabled");

            if (existing instanceof StateFlag) {
                rtpFlag = (StateFlag) existing;
            }
        }
    }

    @Override
    public boolean checkLocation(Player player, Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        com.sk89q.worldedit.util.Location wgLoc = BukkitAdapter.adapt(location);
        return query.testState(wgLoc, localPlayer, rtpFlag);
    }
}