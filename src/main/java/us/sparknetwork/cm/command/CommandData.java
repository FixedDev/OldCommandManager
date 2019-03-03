package us.sparknetwork.cm.command;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.sparknetwork.cm.CommandClass;
import us.sparknetwork.cm.annotation.Command;
import us.sparknetwork.cm.command.arguments.CommandContext;
import us.sparknetwork.cm.exceptions.InternalException;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class CommandData {

    @Getter
    private String[] names;
    @Getter
    private String usage;
    @Getter
    private String description;
    @Getter
    private String permission;
    @Getter
    private String permissionMessage;
    @Getter
    private boolean onlyPlayer = false;
    @Getter
    private int minArgs = 0;
    @Getter
    private int maxArgs = -1;
    @Getter
    private List<Character> noValueFlags;
    @Getter
    private final Method method;
    @Getter
    // ReflectASM
    private final int methodIndex;

    @Nullable
    private final CommandClass classInstance;

    private org.bukkit.command.Command bukkitCommand;

    public CommandData(final Method method, Command command) {
        this(method, command, null);
        Bukkit.getLogger().warning("NOTE: Registering static command methods is deprecated and you shouldn't using this");
    }

    public CommandData(final Method method, Command command, @Nullable CommandClass classInstance) {
        this.method = method;
        this.methodIndex = MethodAccess.get(method.getDeclaringClass()).getIndex(method.getName());
        this.classInstance = classInstance;

        this.names = command.names();
        this.usage = command.usage();
        this.description = command.desc();
        this.minArgs = command.min();
        this.maxArgs = command.max();
        this.permission = command.permission();
        this.permissionMessage = command.permissionMessage();
        this.onlyPlayer = command.onlyPlayer();
        Character[] flags = new Character[command.flags().length];
        int i = 0;
        for (char flag : command.flags()) {
            flags[i] = flag;
            i++;
        }
        this.noValueFlags = new ArrayList<>(Arrays.asList(flags));
    }

    public boolean hasPermission(final CommandSender sender) {
        boolean permission = true;
        if (this.permission.equals("op")) {
            if (!sender.isOp()) {
                permission = false;
            }
        } else if (!this.permission.equals("") && !sender.hasPermission(this.permission)) {
            permission = false;
        }
        return permission;
    }

    public ResultTypes execute(CommandSender sender, String label, String[] args) {
        if (method.getReturnType() != Boolean.TYPE && method.getReturnType() != Boolean.class) {
            throw new InternalException(this.names[0], InternalException.ErrorCodes.RTN);
        }
        Class<?> senderType = method.getParameters()[0].getType();
        if ((senderType != CommandSender.class && senderType != Player.class) || method.getParameters()[1].getType() != CommandContext.class) {
            throw new InternalException(this.names[0], InternalException.ErrorCodes.ARGA);
        }
        if (senderType == Player.class && !this.onlyPlayer) {
            throw new InternalException(this.names[0], InternalException.ErrorCodes.ARGA);
        }
        if (!hasPermission(sender)) {
            return ResultTypes.NOPERMISSION;
        }
        if (onlyPlayer && !(sender instanceof Player)) {
            return ResultTypes.ONLYPLAYER;
        }
        if (args.length < this.minArgs) {
            return ResultTypes.INSUFFICIENTARGUMENTS;
        }
        if (this.maxArgs >= 0 && args.length > maxArgs) {
            return ResultTypes.INSUFFICIENTARGUMENTS;
        }
        CommandContext arguments = new CommandContext(this.names[0], label, args, noValueFlags);

        boolean result;

        if (classInstance != null) {
            result = (boolean) MethodAccess.get(method.getDeclaringClass()).invoke(classInstance, methodIndex, sender, arguments);
        } else {
            result = (boolean) MethodAccess.get(method.getDeclaringClass()).invoke(null, methodIndex, sender, arguments);
        }

        return !result ? ResultTypes.UNSUCESSFUL : ResultTypes.SUCESSFUL;
    }

    public org.bukkit.command.Command toBukkitCommand() {
        if (bukkitCommand == null) {
            bukkitCommand = new BaseCommandExecutor(this);
        }
        return bukkitCommand;
    }

    public enum ResultTypes {
        SUCESSFUL, NOPERMISSION, INSUFFICIENTARGUMENTS, ONLYPLAYER, UNSUCESSFUL
    }
}
