package net.voxelvoid.antifreerun;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import net.voxelvoid.antifreerun.listeners.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiFreerun extends JavaPlugin {
    
    @Override
    public void onEnable() {
        setupConfig();
        PlayerListener playerListener = new PlayerListener(this);
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
    
    public void setupConfig() {
        this.getConfig().addDefault("enable-anti-freerun", true);
        this.getConfig().addDefault("enable-teleport-to-spawn-after-10-tries" , true);
        this.getConfig().addDefault("message.sendtospawn", "You have been teleported back to the spawn!");
        this.getConfig().addDefault("message.teleportback", "Don't go freerunning!");
        this.getConfig().addDefault("blocks-that-get-freerun-protection", Arrays.asList("LEAVES", "LEAVES_2", "FENCE", "FENCE_GATE", "COBBLE_WALL"));
        this.getConfig().options().copyDefaults(true);
    }

    public void increasePlayersBreachCount(Player p) {
        try {
            if (getConfig().getBoolean("enable-teleport-to-spawn-after-10-tries") && !p.hasPermission("freerun.preventwarpback")) {

                if (p.getMetadata("BreachCount").size() <= 0) {
                    p.setMetadata("BreachCount", new FixedMetadataValue(this, Integer.valueOf(10)));
                }

                int plrBreachCount = p.getMetadata("BreachCount").get(0).asInt();

                if (plrBreachCount > 10) {
                    plrBreachCount = 0;
                    if (!getConfig().getString("message.sendtospawn").isEmpty()) { // If message is in config
                        p.sendMessage(getConfig().getString("message.sendtospawn"));
                    }

                    File f = new File(getDataFolder() + File.separator + "logs" + File.separator);

                    if (!f.exists()) {
                        f.mkdirs();
                    }

                    p.teleport(p.getWorld().getSpawnLocation());
                }
                p.setMetadata("BreachCount", new FixedMetadataValue(this, Integer.valueOf(plrBreachCount + 1)));

            } else {
                p.setMetadata("BreachCount", new FixedMetadataValue(this, Integer.valueOf(0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().log(Level.INFO, "Could not log freerunning from player " + p.getName());
            p.teleport(p.getWorld().getSpawnLocation());
            if (!getConfig().getString("message.sendtospawn").isEmpty()) { // If message is in config
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message.sendtospawn")));
            }
            p.setMetadata("BreachCount", new FixedMetadataValue(this, Integer.valueOf(0)));
        }
    }
}
