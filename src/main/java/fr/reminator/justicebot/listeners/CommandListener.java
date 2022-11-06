package fr.reminator.justicebot.listeners;

import fr.reminator.justicebot.commands.Command;
import fr.reminator.justicebot.main.Commands;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class CommandListener implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        Command command = Commands.getByName(slashCommandInteraction.getCommandName());
        assert command != null;
        command.execute(event);
    }
}
