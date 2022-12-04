package fr.reminator.justicebot.commands.rolemenu;

import fr.reminator.justicebot.commands.Command;
import fr.reminator.justicebot.commands.rolemenu.listeners.MenuRolesListener;
import fr.reminator.justicebot.commands.rolemenu.model.MenuRoleGestion;
import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleView;
import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleViewList;
import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandRoleMenu extends Command {

    private final MenuRoleView view;

    public CommandRoleMenu() {
        super();
        view = new MenuRoleViewList();
    }

    @Override
    public String getLabel() {
        return "role-menu";
    }

    @Override
    public String getDescription() {
        return "Gestion des role-menu.";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "create", "Permet de créer un role-menu.", List.of(
                        SlashCommandOption.createChannelOption("channel", "Salon dans lequel créer le role-menu.", true, Set.of(ChannelType.SERVER_TEXT_CHANNEL))
                ))
        );
    }

    @Override
    public boolean isEnabledInDms() {
        return false;
    }

    @Override
    public void otherCalls() {
        setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        TextChannel textChannel = event.getInteraction().getChannel().orElse(null);
        if (textChannel == null) return;

        SlashCommandInteractionOption create = event.getSlashCommandInteraction().getOptionByName("create").orElse(null);
        if (create == null) return;
        SlashCommandInteractionOption channel = create.getOptionByName("channel").orElse(null);
        if (channel == null) return;
        ServerChannel serverChannel = channel.getChannelValue().orElse(null);
        if (serverChannel == null) return;
        String idAsString = serverChannel.getIdAsString();

        Server server = event.getInteraction().getServer().orElse(null);
        if (server == null) return;

        MenuRoleGestion.getInstance(idAsString).clear();

        event.getInteraction().respondLater(true).thenAccept(interactionOriginalResponseUpdater -> {
            JusticeBot.api.addListener(new MenuRolesListener(textChannel.getIdAsString(), idAsString, view));
            event.getSlashCommandInteraction().createFollowupMessageBuilder()
                    .setContent("Sélectionne un rôle à ajouter au menu.")
                    .addComponents(
                            ActionRow.of(
                                    SelectMenu.create("menuRoles", "Clique ici pour choisir un rôle à ajouter.", 1, 1,
                                            getListeRoles(server))
                            )
                    )
                    .send();
                });
    }

    private List<SelectMenuOption> getListeRoles(Server server) {
        Role everyoneRole = server.getEveryoneRole();
        List<Role> roles = server.getRoles().stream().filter(role -> !role.equals(everyoneRole)).collect(Collectors.toList());
        return roles.stream().map(r -> SelectMenuOption.create(r.getName(), String.valueOf(r.getId()))).collect(Collectors.toList());
    }
}
