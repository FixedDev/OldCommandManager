package us.sparknetwork.cm.command.arguments.transformers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import us.sparknetwork.cm.command.arguments.ParameterTransformer;

import java.util.UUID;

public class OfflinePlayerTransformer implements ParameterTransformer<OfflinePlayer> {
    @Override
    public OfflinePlayer transform(String param) {
        return this.getOfflinePlayerFromUUIDOrNick(param);
    }

    private OfflinePlayer getOfflinePlayerFromUUIDOrNick(String uuidOrNick){
        OfflinePlayer player;
        try{
            UUID uuid = UUID.fromString(uuidOrNick);
            player = Bukkit.getOfflinePlayer(uuid);
        } catch (Exception e){
            player = Bukkit.getOfflinePlayer(uuidOrNick);
        }
        return player;
    }
}
