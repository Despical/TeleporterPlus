package me.despical.ipdetector;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;

/**
 * @author Despical
 * <p>
 * Created at 29.02.2024
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            String ipAddress = this.getLocalIPAddress();

            getLogger().info("Your IP address is: " + ipAddress);
        } catch (Exception exception) {
            getLogger().log(Level.SEVERE, "An exception occurred while we were getting the IP address!", exception);
        }
    }

    private String getLocalIPAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();

                if (addr.isLinkLocalAddress() || addr.isLoopbackAddress() || addr.isMulticastAddress()) {
                    continue;
                }

                if (addr.isSiteLocalAddress()) {
                    return addr.getHostAddress();
                }
            }

            return null;
        }

        return null;
    }
}