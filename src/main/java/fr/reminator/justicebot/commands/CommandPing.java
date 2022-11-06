package fr.reminator.justicebot.commands;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

public class CommandPing extends Command {

    public CommandPing() {
        super();
    }

    @Override
    public String getLabel() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Vérifie que le serveur réponde.";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return null;
    }

    @Override
    public boolean isEnabledInDms() {
        return true;
    }

    @Override
    public void otherCalls() {
        // setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR, PermissionType.BAN_MEMBERS);
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        event.getInteraction().createImmediateResponder().setContent("Pong.").respond();
    }
}
