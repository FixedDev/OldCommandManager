package us.sparknetwork.cm.exceptions;

/**
 * This exception is throwed when a command fails to execute or when a exception is throwed
 */
public class CommandException extends RuntimeException {

    String commandName;

    public CommandException(String commandName, String message) {
        super(message);
        this.commandName = commandName;
    }

    public CommandException(String commandName, String message, Throwable cause) {
        super(message, cause);
        this.commandName = commandName;
    }

    public CommandException(String commandName, Throwable cause) {
        super(cause);
        this.commandName = commandName;
    }
}
