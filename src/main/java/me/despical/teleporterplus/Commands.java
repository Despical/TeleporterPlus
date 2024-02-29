package me.despical.teleporterplus;

import me.despical.commandframework.Command;
import me.despical.commandframework.CommandArguments;
import me.despical.commandframework.Completer;
import me.despical.commons.ReflectionUtils;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.miscellaneous.MiscUtils;
import me.despical.commons.serializer.LocationSerializer;
import me.despical.commons.util.Strings;
import me.despical.teleporterplus.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Despical
 * <p>
 * Created at 23.02.2024
 */
public class Commands {

    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
        this.plugin.getCommandFramework().addCustomParameter(Player.class, CommandArguments::getSender);
        this.plugin.getCommandFramework().registerCommands(this);
    }

    @Command(
            name = "teleporterplus",
            usage = "/teleporterplus <world>",
            desc = "Main command of the Teleporter Plus plugin.",
            min = 1,
            senderType = Command.SenderType.PLAYER
    )
    public void mainCommand(Player player, CommandArguments arguments) {
        List<String> allowedWorlds = plugin.getConfig().getStringList("allowed-worlds");
        String targetWorld = arguments.getArgument(0);
        World world = plugin.getServer().getWorld(targetWorld);

        if (world == null) {
            Utils.sendMessage(player, "messages.no-world-found");
            return;
        }

        if (!allowedWorlds.contains(targetWorld)) {
            Utils.sendMessage(player, "messages.not-allowed-here");
            return;
        }

        FileConfiguration config = ConfigUtils.getConfig(plugin, "data");
        String path = String.format("%s.last-locations.%s", player.getUniqueId(), targetWorld);

        if (config.isSet(path)) {
            Location location = LocationSerializer.fromString(config.getString(path));

            if (plugin.getConfig().getBoolean("rtp.task-enabled")) {
                new TeleportTask(plugin, player, location);
                return;
            }

            player.teleport(location);
            return;
        }

        Location randomLocation = Utils.pickLocation(player, world);

        if (randomLocation == null) {
            Utils.sendMessage(player, "messages.no-random-location-found");
            return;
        }

        player.teleport(randomLocation.add(0, 1, 0));

        Location location = player.getLocation();
        config.set(player.getUniqueId() + ".last-locations." + location.getWorld().getName(), LocationSerializer.toString(location));
        ConfigUtils.saveConfig(plugin, config, "data");
    }

    @Command(
            name = "teleporterplus.reload",
            usage = "/teleporterplus reload",
            permission = "teleporterplus.reload",
            desc = "Reloads configuration files and options."
    )
    public void reloadCommand(CommandArguments arguments) {
        plugin.reloadConfig();

        Utils.sendMessage(arguments.getSender(), "messages.reloaded-files");
    }

    @SuppressWarnings("deprecation")
    @Command(
            name = "teleporterplus.help",
            permission = "teleporterplus.help"
    )
    public void helpCommand(CommandArguments arguments) {
        final boolean isPlayer = arguments.isSenderPlayer();
        final CommandSender sender = arguments.getSender();
        final String message = Strings.format("&3&l---- Teleporter Plus Admin Commands ----");

        arguments.sendMessage("");
        MiscUtils.sendCenteredMessage(sender, message);
        arguments.sendMessage("");

        if (ReflectionUtils.supports(8)) {
            arguments.sendMessage(Strings.format("&8 • &b/teleporterplus <world> &3- &bMain command of the Teleporter Plus plugin."));
            arguments.sendMessage(Strings.format("&8 • &b/teleporterplus reload &3- &bReloads configuration files and options."));
            return;
        }

        for (final Command command : plugin.getCommandFramework().getCommands()) {
            String usage = command.usage(), desc = command.desc();

            if (usage.isEmpty() || usage.contains("help")) continue;

            if (isPlayer) {
                ((Player) sender).spigot().sendMessage(new ComponentBuilder()
                        .color(ChatColor.DARK_GRAY)
                        .append(" • ")
                        .append(usage)
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, usage))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(desc)))
                        .color(ChatColor.AQUA)
                        .create());
            } else {
                sender.sendMessage(Strings.format(" &8• &b" + usage + " &3- &b" + desc));
            }
        }

        if (isPlayer) {
            final Player player = arguments.getSender();
            player.sendMessage("");
            player.spigot().sendMessage(new ComponentBuilder("TIP:").color(ChatColor.YELLOW).bold(true)
                    .append(" Try to ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY)
                    .append("hover").color(ChatColor.WHITE).underlined(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Hover on the commands to get info about them.")))
                    .append(" or ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY)
                    .append("click").color(ChatColor.WHITE).underlined(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Click on the commands to insert them in the chat.")))
                    .append(" on the commands!", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY)
                    .create());
        }
    }

    @Completer(
            name = "teleporterplus"
    )
    public List<String> tabCompleter() {
        return new ArrayList<>(plugin.getConfig().getConfigurationSection("rtp.worlds").getKeys(false));
    }
}