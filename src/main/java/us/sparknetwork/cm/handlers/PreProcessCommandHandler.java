

package us.sparknetwork.cm.handlers;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import us.sparknetwork.cm.command.CommandData;

import java.util.Map;

@Singleton
public class PreProcessCommandHandler extends CommandMapHandler implements Listener {

    @Inject
    public PreProcessCommandHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerPreProcess(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage();

        if (this.dispatchCommand(e.getPlayer(), command.substring(1))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void consolePreProcess(ServerCommandEvent e) {
        if (this.dispatchCommand(e.getSender(), e.getCommand())){
            e.setCommand("");
        }
    }
}
