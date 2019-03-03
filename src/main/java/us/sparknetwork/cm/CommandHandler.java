package us.sparknetwork.cm;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.sparknetwork.cm.annotation.Command;
import us.sparknetwork.cm.command.CommandData;
import us.sparknetwork.cm.command.arguments.CommandContext;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class CommandHandler {

    protected List<CommandData> commands;

    private JavaPlugin plugin;

    public CommandHandler(JavaPlugin plugin) {
        commands = new LinkedList<>();
        this.plugin = plugin;
    }

    public void registerCommandData(CommandData data) {
        this.commands.add(data);
    }

    public void registerCommandMethod(@Nullable CommandClass commandClass, Method method) {

        if (!method.isAnnotationPresent(Command.class)) {
            Bukkit.getLogger().severe("Failed to register command " + method.getName() + " not command annotation present");
            return;
        }
        if (commandClass == null && !Modifier.isStatic(method.getModifiers())) {
            Bukkit.getLogger().severe("Failed to register command " + method.getName() + " not static");
            return;
        }
        if (method.getReturnType() != Boolean.TYPE && method.getReturnType() != Boolean.class) {
            Bukkit.getLogger().severe("Failed to register command " + method.getName() + " not command annotation present");
            return;
        }

        Class<?> senderType = method.getParameters()[0].getType();
        if ((senderType != CommandSender.class && senderType != Player.class) || method.getParameters()[1].getType() != CommandContext.class) {
            Bukkit.getLogger().severe("Failed to register command " + method.getName() + " incorrect parameters type");
            return;
        }
        Command anot = method.getDeclaredAnnotation(Command.class);
        if (senderType == Player.class && !anot.onlyPlayer()) {
            Bukkit.getLogger().severe("Failed to register command " + method.getName() + " incorrect parameters type");
            return;
        }

        CommandData data;

        if (commandClass != null) {
            data = new CommandData(method, anot, commandClass);
        } else {
            data = new CommandData(method, anot);
        }

        registerCommandData(data);
    }

    public void registerCommandMethod(Method method) {
        registerCommandMethod(null, method);
    }

    public void registerCommandClass(CommandClass commandClass) {
        Preconditions.checkNotNull(commandClass);

        for (Method method : commandClass.getClass().getMethods()) {
            if (!method.isAnnotationPresent(Command.class)) continue;

            this.registerCommandMethod(commandClass, method);
        }
        Bukkit.getLogger().info("Registered command class " + commandClass.getClass().getCanonicalName());
    }

    public void registerCommandClass(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (!method.isAnnotationPresent(Command.class)) continue;

            this.registerCommandMethod(method);
        }
        Bukkit.getLogger().info("Registered command class " + clazz.getCanonicalName());
    }

    public Optional<CommandData> getCommand(String nameOrAlias) {
        Optional<CommandData> commandData = Optional.empty();

        for (CommandData data : commands) {
            for (String alias : data.getNames()) {
                if (alias.equalsIgnoreCase(nameOrAlias)) {
                    commandData = Optional.of(data);
                    break;
                }
            }
        }

        return commandData;
    }

    public boolean isCommandRegistered(String nameOrAlias) {
        return getCommand(nameOrAlias).isPresent();
    }

    public boolean dispatchCommand(CommandSender commandSender, String commandLine) {
        String[] args = new String[0];
        Optional<CommandData> found = Optional.empty();
        String label = "";

        for (CommandData commandData : this.commands) {

            for (String alias : commandData.getNames()) {
                if (commandLine.toLowerCase().startsWith(alias.toLowerCase() + " ")) {
                    found = Optional.of(commandData);
                    label = alias;
                    if (commandLine.length() > alias.length() + 1) {
                        args = commandLine.substring(alias.length() + 1).split(" ");
                    }
                    break;
                }
            }
        }

        if (!found.isPresent()) {
            return false;
        }

        CommandData data = found.get();

        CommandData.ResultTypes result = data.execute(commandSender, label, args);

        switch (result) {
            case SUCESSFUL:
                return true;
            case ONLYPLAYER:
                commandSender.sendMessage(ChatColor.RED + "Only players can execute this command.");
                break;
            case NOPERMISSION:
                commandSender.sendMessage(ChatColor.RED + data.getPermissionMessage());
                break;
            case UNSUCESSFUL:
            case INSUFFICIENTARGUMENTS:
                String[] usage = ChatColor.translateAlternateColorCodes('&', data.getUsage()).split("\n");
                commandSender.sendMessage(usage);
                break;
            default:
                commandSender.sendMessage(ChatColor.RED + "The command returned an unknown type, report this to a developer!");
                Bukkit.getLogger().severe("The command " + label + " returned " + result.toString());
                return false;
        }

        return true;
    }
}
