package us.sparknetwork.cm.exceptions;


import lombok.Getter;

/**
 * This is a exception that are throwed when a sender doesn't have permission to execute a command
 */
@Getter
public class NoPermissionsException extends CommandException {

    String permission;

    public NoPermissionsException(String commandName, String permissionMessage, String permission) {
        super(commandName, permissionMessage);
        this.permission = permission;
    }

}
