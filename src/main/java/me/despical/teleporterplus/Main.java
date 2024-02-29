package me.despical.teleporterplus;

import me.despical.commandframework.CommandFramework;
import me.despical.teleporterplus.integrations.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Despical
 * <p>
 * Created at 23.02.2024
 */
public class Main extends JavaPlugin {

    private CommandFramework commandFramework;
    private List<Integration> integrations;

    @Override
    public void onEnable() {
//        try {
//            String localIPAddress = getLocalIPAddress();
//            if (localIPAddress == null) {
//                getLogger().severe("IP Adresi belirlenemedi.");
//                return;
//            }
//
////            JSONObject response = sendGET("https://quezly.net/app/api/license.php?product=8&ip=" + localIPAddress);
//
//            if (false) {
//                getLogger().info("Lisans bulundu!");
//                getLogger().info("Plugin Aktif!");
//            } else {
//                getLogger().severe("Lisans bulunamadı!");
//                getLogger().severe(localIPAddress);
////                getServer().shutdown();
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//            return;
//        }

        this.setupConfigurationFiles();

        this.commandFramework = new CommandFramework(this);
        this.integrations = new ArrayList<>();

        new Commands(this);
        new Events(this);

        PluginManager pluginManager = getServer().getPluginManager();

        if (getConfig().getBoolean("integrations.grief-prevention") && pluginManager.isPluginEnabled("GriefPrevention")) {
            integrations.add(new GriefPreventionIntegration());
        }

        if (getConfig().getBoolean("integrations.hClaims") && pluginManager.isPluginEnabled("hClaims")) {
            integrations.add(new HClaimIntegration());
        }

        if (getConfig().getBoolean("integrations.towny") && pluginManager.isPluginEnabled("Towny")) {
            integrations.add(new TownyIntegration());
        }

        if (getConfig().getBoolean("integrations.world-guard") && pluginManager.isPluginEnabled("WorldGuard")) {
            integrations.add(new WorldGuardIntegration());
        }
    }

    private void setupConfigurationFiles() {
        Stream.of("config", "data").filter(fileName -> !new File(getDataFolder(),fileName + ".yml").exists()).forEach(fileName -> this.saveResource(fileName + ".yml", false));
    }

    public CommandFramework getCommandFramework() {
        return commandFramework;
    }

    public List<Integration> getIntegrations() {
        return integrations;
    }

    private String getLocalIPAddress() throws SocketException {
        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = (NetworkInterface) interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }
            Enumeration addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = (InetAddress) addresses.nextElement();
                if (addr.isLinkLocalAddress() || addr.isLoopbackAddress() || addr.isMulticastAddress()) {
                    continue;
                }
                return addr.getHostAddress();
            }
        }
        return null;
    }

    private JSONObject sendGET(String url) throws IOException, JSONException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return new JSONObject(response.toString());
        } else {
            throw new IOException("Response code: " + responseCode);
        }
    }
}