package us.sparknetwork.cm.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import us.sparknetwork.cm.exceptions.CommandException;
import us.sparknetwork.cm.exceptions.InsufficientArgumentsException;
import us.sparknetwork.cm.exceptions.InternalException;
import us.sparknetwork.cm.exceptions.NoPermissionsException;

import java.util.Arrays;
import java.util.logging.Level;

public class BaseCommandExecutor extends Command {

    private final CommandData data;

    public BaseCommandExecutor(CommandData data) {
        super(data.getNames()[0]);
        this.data = data;
        setDescription(data.getDescription());
        setUsage(data.getUsage());
        setPermission(data.getPermission());
        setPermissionMessage(data.getPermissionMessage());
        setAliases(Arrays.asList(Arrays.copyOfRange(data.getNames(), 1, data.getNames().length)));
    }

    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        try {
            CommandData.ResultTypes result = data.execute(commandSender, s, strings);
            switch (result) {
                case SUCESSFUL:
                    break;
                case ONLYPLAYER:
                    commandSender.sendMessage(ChatColor.RED + "Only players can execute this command!");
                    break;
                case UNSUCESSFUL:
                case INSUFFICIENTARGUMENTS:
                    String[] usageLines = this.getUsage().split("\n");
                    for (String str : usageLines) {
                        commandSender.sendMessage(ChatColor.RED + str.replace("<command>", s).replace("(command)", s));
                    }
                    break;
                case NOPERMISSION:
                    commandSender.sendMessage(ChatColor.RED + this.getPermissionMessage());
                    break;
            }
        } catch (CommandException e) {
            if (e instanceof InternalException) {
                commandSender.sendMessage(ChatColor.RED + "Internal exception occurred when executing the command.");
                Bukkit.getLogger().log(Level.SEVERE, "Internal exception occurred when executing the command, caused by: ", e);
                return false;
            }
        }
        return true;
    }
}
