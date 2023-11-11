package fr.reminator.justicebot.commands.rolemenu.view;

import com.vdurmont.emoji.EmojiParser;
import fr.reminator.justicebot.commands.rolemenu.model.MenuRoleRole;
import fr.reminator.justicebot.commands.rolemenu.view.listeners.ButtonMenuRolesListener;
import fr.reminator.justicebot.commands.rolemenu.model.MenuRoleGestion;
import fr.reminator.justicebot.commands.rolemenu.view.listeners.ListRoleListener;
import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ButtonInteraction;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuRoleViewList implements MenuRoleView {

    public final static String buttonCustomId = "buttonMenuRole";
    public final static String listCustomId = "selectRoleMenu";

    public MenuRoleViewList() {
        JusticeBot.api.addListener(new ButtonMenuRolesListener(this));
        JusticeBot.api.addListener(new ListRoleListener());
    }

    @Override
    public void sendMenu(String idChannelOutput) {

        Channel channel = JusticeBot.api.getChannelById(idChannelOutput).orElse(null);
        TextChannel textChannel = channel.asTextChannel().orElse(null);

        new MessageBuilder()
                .addComponents(
                        ActionRow.of(
                                new ButtonBuilder()
                                        .setCustomId(buttonCustomId)
                                        .setStyle(ButtonStyle.PRIMARY)
                                        .setLabel("Clique ici pour afficher la liste des rôles.")
                                        .setEmoji(EmojiParser.parseToUnicode(":earth_africa:"))
                                        .build()
                        )
                ).send(textChannel);
    }

    public void sendMenuList(ButtonInteraction buttonInteraction) {

        TextChannel textChannel = buttonInteraction.getChannel().get();
        User user = buttonInteraction.getUser();

        String idChannelOutput = textChannel.getIdAsString();

        MenuRoleGestion menuRoleGestion = MenuRoleGestion.getInstance(idChannelOutput);
        menuRoleGestion.updateFromJson();
        List<MenuRoleRole> roles = menuRoleGestion.getRoles();

        if (roles.isEmpty()) {
            buttonInteraction.createImmediateResponder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .setContent("Pas de rôle disponible !")
                    .respond();
            return;
        }

        ServerChannel serverChannel = textChannel.asServerChannel().orElse(null);
        Server server = serverChannel.getServer();

        buttonInteraction.respondLater(true).thenAccept(interactionOriginalResponseUpdater -> {
            buttonInteraction.createFollowupMessageBuilder()
                    .addComponents(
                            ActionRow.of(
                                    new SelectMenuBuilder(ComponentType.TEXT_INPUT, listCustomId)
                                            .setPlaceholder("Choisis tes rôles")
                                            .setMinimumValues(0)
                                            .setMaximumValues(roles.size())
                                            .addOptions(roles.stream().map(role -> {
                                                Role guildRole = server.getRoleById(role.getIdRole()).get();
                                                String emojiString = EmojiParser.parseToUnicode(role.getEmote());
                                                String description = role.getDescription();

                                                SelectMenuOptionBuilder selectMenuOptionBuilder = new SelectMenuOptionBuilder()
                                                        .setLabel(guildRole.getName())
                                                        .setValue(guildRole.getIdAsString())
                                                        .setDefault(user.getRoles(server).contains(guildRole))
                                                        .setEmoji(new Emoji() {
                                                            @Override
                                                            public Optional<String> asUnicodeEmoji() {
                                                                return Optional.ofNullable(emojiString);
                                                            }

                                                            @Override
                                                            public boolean isAnimated() {
                                                                return false;
                                                            }

                                                            @Override
                                                            public String getMentionTag() {
                                                                return null;
                                                            }
                                                        });

                                                if (description != null)
                                                    selectMenuOptionBuilder.setDescription(description);
                                                return selectMenuOptionBuilder.build();
                                            }).collect(Collectors.toList()))
                                            .build()
                            )
                    ).send();
        });
    }
}
