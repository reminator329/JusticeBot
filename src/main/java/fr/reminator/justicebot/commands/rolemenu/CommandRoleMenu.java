package fr.reminator.justicebot.commands.rolemenu;

import fr.reminator.justicebot.commands.Command;
import fr.reminator.justicebot.commands.rolemenu.model.MenuRoleGestion;
import fr.reminator.justicebot.commands.rolemenu.model.MenuRoleRole;
import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleView;
import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleViewList;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
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
                )),
                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add-role", "Permet d'ajouter un rôle au role-menu.", List.of(
                        SlashCommandOption.createChannelOption("channel", "Salon dans lequel le role-menu a été créé.", true, Set.of(ChannelType.SERVER_TEXT_CHANNEL)),
                        SlashCommandOption.createRoleOption("role", "Rôle à ajouter dans le role-menu.", true),
                        SlashCommandOption.createStringOption("emote", "Émote à associer au rôle.", true),
                        SlashCommandOption.createStringOption("description", "Description du rôle.", true)
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

        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        List<SlashCommandInteractionOption> options = slashCommandInteraction.getOptions();
        SlashCommandInteractionOption option = options.get(0);

        String content = "";
        switch (option.getName()) {
            case "create" -> {
                createMenu(option);
                content = "Role-menu créé !";
            }
            case "add-role" -> {
                addRole(option);
                content = "Rôle ajouté !";
            }
        }
        event.getInteraction().createImmediateResponder().setContent(content).setFlags(MessageFlag.EPHEMERAL).respond();
    }

    private void createMenu(SlashCommandInteractionOption option) {
        SlashCommandInteractionOption channel = option.getOptionByName("channel").orElse(null);
        if (channel == null) return;
        ServerChannel serverChannel = channel.getChannelValue().orElse(null);
        if (serverChannel == null) return;
        String idAsString = serverChannel.getIdAsString();

        MenuRoleGestion instance = MenuRoleGestion.getInstance(idAsString);
        instance.clear();
        instance.saveJson();

        view.sendMenu(idAsString);
    }

    private void addRole(SlashCommandInteractionOption option) {
        SlashCommandInteractionOption channel = option.getOptionByName("channel").orElse(null);
        if (channel == null) return;
        ServerChannel serverChannel = channel.getChannelValue().orElse(null);
        if (serverChannel == null) return;
        String idAsString = serverChannel.getIdAsString();

        SlashCommandInteractionOption role = option.getOptionByName("role").orElse(null);
        if (role == null) return;
        Role roleValue = role.getRoleValue().orElse(null);
        if (roleValue == null) return;
        String idRole = roleValue.getIdAsString();
        System.out.println("AJOUT ROLE");

        SlashCommandInteractionOption emoteOption = option.getOptionByName("emote").orElse(null);
        if (emoteOption == null) return;
        System.out.println("AJOUT ROLE");
        System.out.println(emoteOption.getStringRepresentationValue().orElse(null));
        String emote = emoteOption.getStringValue().orElse(null);
        if (emote == null) return;
        emote = ":" + emote + ":";
        System.out.println("AJOUT ROLE");

        SlashCommandInteractionOption descriptionOption = option.getOptionByName("description").orElse(null);
        if (descriptionOption == null) return;
        String description = descriptionOption.getStringValue().orElse(null);


        System.out.println("AJOUT ROLE");
        MenuRoleGestion instance = MenuRoleGestion.getInstance(idAsString);
        instance.addRole(new MenuRoleRole(idRole, emote, description));
        instance.saveJson();
        System.out.println("AJOUT ROLE");
    }

    private List<SelectMenuOption> getListeRoles(Server server) {
        Role everyoneRole = server.getEveryoneRole();
        List<Role> roles = server.getRoles().stream().filter(role -> !role.equals(everyoneRole)).collect(Collectors.toList());
        return roles.stream().map(r -> SelectMenuOption.create(r.getName(), String.valueOf(r.getId()))).collect(Collectors.toList());
    }
}
