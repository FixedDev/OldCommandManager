package us.sparknetwork.cm.exceptions;

import lombok.Getter;

/**
 * This is a exception called when a command don't have the sufficient arguments or have more than the necessary
 */
@Getter
public class InsufficientArgumentsException extends CommandException {

    private int expectedArgs;
    private int actualArgs;

    public InsufficientArgumentsException(String commandName, int expectedArguments, int actualArguments, String usage) {
        super(commandName, usage);
        this.expectedArgs = expectedArguments;
        this.actualArgs = actualArguments;
    }
}
