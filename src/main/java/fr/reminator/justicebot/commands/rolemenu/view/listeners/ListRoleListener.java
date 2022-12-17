package fr.reminator.justicebot.commands.rolemenu.view.listeners;

import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleViewList;
import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.List;
import java.util.stream.Collectors;

public class ListRoleListener implements MessageComponentCreateListener {


    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {

        MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
        if (!MenuRoleViewList.listCustomId.equals(messageComponentInteraction.getCustomId())) {
            return;
        }
        SelectMenuInteraction selectMenuInteraction = messageComponentInteraction.asSelectMenuInteraction().orElse(null);
        List<SelectMenuOption> chosenOptions = selectMenuInteraction.getChosenOptions();
        List<SelectMenuOption> possibleOptions = selectMenuInteraction.getPossibleOptions();

        Server server = selectMenuInteraction.getServer().get();
        User user = selectMenuInteraction.getUser();

        List<Role> chosenRoles = chosenOptions.stream().map(o -> server.getRoleById(o.getValue()).get()).collect(Collectors.toList());
        List<Role> possibleRoles = possibleOptions.stream().map(o -> server.getRoleById(o.getValue()).get()).collect(Collectors.toList());
        List<Role> roles = user.getRoles(server);

        System.out.println("EOPIFGUZ?IEUFGZE HFN8UHZE9M FH9PZEH FMHZEIM FGNIZUEHFG IUHZEUI FG");
        System.out.println(possibleRoles.stream().filter(role -> !chosenRoles.contains(role) && roles.contains(role)).collect(Collectors.toList()));
        System.out.println(chosenRoles.stream().filter(role -> !roles.contains(role)).collect(Collectors.toList()));

        server.createUpdater()
                .removeRolesFromUser(user, possibleRoles.stream().filter(role -> !chosenRoles.contains(role) && roles.contains(role)).collect(Collectors.toList()))
                .addRolesToUser(user, chosenRoles.stream().filter(role -> !roles.contains(role)).collect(Collectors.toList()))
                .update();

        selectMenuInteraction.respondLater(true).thenAccept(interactionOriginalResponseUpdater -> {
            selectMenuInteraction.createFollowupMessageBuilder().setContent("Rôles modifiés").send();
        });
    }
}
