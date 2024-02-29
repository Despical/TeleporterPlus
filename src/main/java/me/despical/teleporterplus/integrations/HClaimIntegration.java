package me.despical.teleporterplus.integrations;

import com.hakan.claim.ClaimPlugin;
import com.hakan.claim.service.ClaimService;
import com.hakan.claim.shadow.com.hakan.basicdi.Injector;
import com.hakan.claim.shadow.com.hakan.spinjection.SpigotBootstrap;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Despical
 * <p>
 * Created at 24.02.2024
 */
public class HClaimIntegration implements Integration {

    @Override
    public boolean checkLocation(Player player, Location location) {
        Injector claimInjector = SpigotBootstrap.of(ClaimPlugin.class).getInjector();
        ClaimService claimService = claimInjector.getInstance(ClaimService.class);
        return claimService.getByLocation(location) == null;
    }
}