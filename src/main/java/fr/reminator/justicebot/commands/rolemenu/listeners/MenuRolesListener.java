package fr.reminator.justicebot.commands.rolemenu.listeners;

import com.vdurmont.emoji.EmojiParser;
import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleView;
import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleViewEmbed;
import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleViewList;
import fr.reminator.justicebot.main.JusticeBot;
import fr.reminator.justicebot.commands.rolemenu.model.MenuRoleGestion;
import fr.reminator.justicebot.utils.JsonUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.ModalInteraction;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.javacord.api.listener.interaction.ModalSubmitListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Deprecated
public class MenuRolesListener implements MessageComponentCreateListener, ModalSubmitListener {

    private final String idChannelCreation;
    private final String idChannelOutput;

    MenuRoleView view;

    public MenuRolesListener(String idChannelCreation, String idChannelOutput, MenuRoleView view) {
        this.idChannelCreation = idChannelCreation;
        this.idChannelOutput = idChannelOutput;
        this.view = view;
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();

        if (!"menuRoles".equals(messageComponentInteraction.getCustomId())) {
            return;
        }

        addRole(messageComponentInteraction);
    }

    @Override
    public void onModalSubmit(ModalSubmitEvent event) {
        ModalInteraction modalInteraction = event.getModalInteraction();
        String customId = modalInteraction.getCustomId();

        if (!"emoteModal_menuRoles".equals(customId)) {
            return;
        }

        addEmote(modalInteraction);
    }

    private void addEmote(ModalInteraction modalInteraction) {
        TextChannel textChannel = modalInteraction.getChannel().orElse(null);
        if (textChannel == null || !textChannel.getIdAsString().equalsIgnoreCase(idChannelCreation)) return;

        String emote = modalInteraction.getTextInputValueByCustomId("emote").orElse(null);
        if (emote == null) return;

        MenuRoleGestion menuRoleGestion = MenuRoleGestion.getInstance(idChannelOutput);
        //menuRoleGestion.withEmote(emote);


        modalInteraction.respondLater(true).thenAccept(interactionOriginalResponseUpdater -> {
            modalInteraction.createFollowupMessageBuilder()
                    .setContent("Sélectionne un autre rôle à ajouter au menu.")
                    .addComponents(
                            ActionRow.of(
                                    SelectMenu.create("menuRoles", "Clique ici pour choisir un rôle à ajouter.", 1, 1,
                                            getListeRoles(modalInteraction.getServer().get()))
                            )
                    )
                    .send();
        });
    }

    private List<SelectMenuOption> getListeRoles(Server server) {
        Role everyoneRole = server.getEveryoneRole();

        MenuRoleGestion menuRoleGestion = MenuRoleGestion.getInstance(idChannelOutput);
        /*
        Set<String> rolesMenu = menuRoleGestion.getRoles();

        List<Role> roles = server.getRoles().stream().filter(role -> !role.equals(everyoneRole) && !rolesMenu.contains(role.getIdAsString())).collect(Collectors.toList());
        List<SelectMenuOption> content = new ArrayList<>(List.of(SelectMenuOption.create("Terminer", "null", "Sélectionne si tous les rôles du menu sont ajoutés.")));
        content.addAll(roles.stream().map(r -> SelectMenuOption.create(r.getName(), String.valueOf(r.getId()))).collect(Collectors.toList()));
        return content;
         */
        return null;
    }

    public void addRole(MessageComponentInteraction messageComponentInteraction) {

        TextChannel textChannel = messageComponentInteraction.getChannel().orElse(null);
        if (textChannel == null || !textChannel.getIdAsString().equalsIgnoreCase(idChannelCreation)) return;

        SelectMenuInteraction selectMenuInteraction = messageComponentInteraction.asSelectMenuInteraction().orElse(null);
        if (selectMenuInteraction == null) return;

        SelectMenuOption selectMenuOption = selectMenuInteraction.getChosenOptions().get(0);
        String idRole = selectMenuOption.getValue();

        if (idRole.equals("null")) {
            JusticeBot.api.removeListener(this);
            messageComponentInteraction.respondLater(true).thenAccept(interactionOriginalResponseUpdater ->
                    messageComponentInteraction.createFollowupMessageBuilder().setContent("Menu de rôles créé dans <#" + JusticeBot.api.getChannelById(idChannelOutput).get().getIdAsString() + ">").send()
            );
            sendMenu();
            return;
        }
        /*
        MenuRoleGestion menuRoleGestion = MenuRoleGestion.getInstance(idChannelOutput);
        menuRoleGestion.withRole(idRole);

        messageComponentInteraction.respondWithModal("emoteModal_menuRoles", "Entre l'émote pour le rôle sélectionné.",
                ActionRow.of(TextInput.create(TextInputStyle.SHORT, "emote", "Émote", ":exemple:", "", true)));


         */
    }

    private void sendMenu() {
        view.sendMenu(idChannelOutput);
        MenuRoleGestion.getInstance(idChannelOutput).saveJson();
    }
}
