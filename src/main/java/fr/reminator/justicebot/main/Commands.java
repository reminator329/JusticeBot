package fr.reminator.justicebot.main;

import fr.reminator.justicebot.commands.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Commands {

    PING(new CommandPing()),
    SUGGESTIONS(new CommandSuggestions()),
    SUGGESTION(new CommandSuggest()),
    ;

    private final Command command;

    Commands(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public static Set<Command> all() {
        return Arrays.stream(values()).map(Commands::getCommand).collect(Collectors.toSet());
    }

    public static Command getByName(String name) {
        for (Commands c : values()) {
            Command command = c.getCommand();
            if (command.getLabel().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }
}
