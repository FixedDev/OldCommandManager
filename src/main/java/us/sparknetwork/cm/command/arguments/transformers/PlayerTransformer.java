package us.sparknetwork.cm.command.arguments.transformers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.sparknetwork.cm.command.arguments.ParameterTransformer;

public class PlayerTransformer implements ParameterTransformer<Player> {
    @Override
    public Player transform(String param) {
        return Bukkit.getPlayer(param);
    }
}
