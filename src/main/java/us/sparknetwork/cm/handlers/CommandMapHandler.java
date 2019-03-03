package us.sparknetwork.cm.handlers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import us.sparknetwork.cm.CommandHandler;
import us.sparknetwork.cm.command.CommandData;

import java.lang.reflect.Field;

@Singleton
public class CommandMapHandler extends CommandHandler {

    private CommandMap cmdMap;
    private JavaPlugin plugin;

    @Inject
    public CommandMapHandler(JavaPlugin plugin) {
        super(plugin);
        this.plugin = plugin;

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            cmdMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            Bukkit.getLogger().severe("Failed to get command map: " + ex.getMessage());
        }
    }

    @Override
    public void registerCommandData(CommandData data) {
        if (cmdMap == null) {
            throw new RuntimeException("Invalid command map");
        }
        org.bukkit.command.Command cmd = data.toBukkitCommand();

        cmdMap.register(cmd.getName(), cmd);
        this.commands.add(data);
    }
}
