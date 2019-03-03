package us.sparknetwork.cm.exceptions;

import java.text.MessageFormat;

public class InternalException extends CommandException {

    public InternalException(String commandName, String message) {
        super(commandName, message);
    }

    public InternalException(String commandName, String message, Throwable cause) {
        super(commandName, message, cause);
    }

    public InternalException(String commandName, Throwable cause) {
        super(commandName, cause);
    }

    public InternalException(String commandName, ErrorCodes code) {
        super(commandName, MessageFormat.format("The command {0} failed to execute due an internal error, report this to developers. ERROR CODE {1}.", commandName, code.toString()));
    }

    public enum ErrorCodes {

        STC("Method not static"),
        ARGA("Method doesn't have certain args"),
        RTN("Method doesn't return a boolean");

        private String errorMessage;

        ErrorCodes(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
