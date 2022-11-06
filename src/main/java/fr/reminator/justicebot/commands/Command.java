package fr.reminator.justicebot.commands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

public abstract class Command extends SlashCommandBuilder {

    public Command() {
        setName(getLabel());
        setDescription(getDescription());
        setOptions(getOptions());
        setEnabledInDms(isEnabledInDms());
        otherCalls();
    }

    public abstract String getLabel();

    public abstract String getDescription();

    public abstract List<SlashCommandOption> getOptions();

    public abstract boolean isEnabledInDms();

    public abstract void otherCalls();

    public abstract void execute(SlashCommandCreateEvent event);

}
