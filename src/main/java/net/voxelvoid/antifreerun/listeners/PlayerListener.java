package net.voxelvoid.antifreerun.listeners;

import net.voxelvoid.antifreerun.AntiFreerun;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Matthijs
 */
public class PlayerListener implements Listener {

    private AntiFreerun plugin;

    public PlayerListener(AntiFreerun plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getConfig().getBoolean("enable-anti-freerun")) { // If enabled in the config
            Block defaultBlock = event.getTo().getBlock().getRelative(0, -1, 0);
            if ((!event.getPlayer().isInsideVehicle()) && (!event.getPlayer().hasPermission("freerun.allow"))) {
                for (String s : plugin.getConfig().getStringList("blocks-that-get-freerun-protection")) {
                    if (defaultBlock.getType() == Material.getMaterial(s)) {
                        if (!plugin.getConfig().getString("message.teleportback").isEmpty()) { // If config contains error message
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("message.teleportback")));
                        }
                        event.getPlayer().teleport(event.getFrom());
                        plugin.increasePlayersBreachCount(event.getPlayer());
                    }
                }
            }
        }
    }
}
