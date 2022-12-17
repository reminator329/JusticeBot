package fr.reminator.justicebot.commands.rolemenu.view;

import com.vdurmont.emoji.EmojiParser;
import fr.reminator.justicebot.commands.rolemenu.model.MenuRoleGestion;
import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;

import java.util.concurrent.CompletableFuture;

@Deprecated
public class MenuRoleViewEmbed implements MenuRoleView {

    @Override
    public void sendMenu(String idChannelOutput) {
        /*
        MenuRoleGestion menuRoleGestion = MenuRoleGestion.getInstance(idChannelOutput);
        StringBuilder stringBuilder = new StringBuilder("**Menu de rôles**\n\n");
        menuRoleGestion.getMenu().forEach(e -> stringBuilder.append(e.getValue() + " : " + JusticeBot.api.getRoleById(e.getKey()).get().getName() + "\n"));

        new MessageBuilder()
                .setContent(stringBuilder.toString())
                .send(JusticeBot.api.getTextChannelById(idChannelOutput).get()).thenAccept(m -> {
                    menuRoleGestion.getMenu().forEach(e -> m.addReactions(EmojiParser.parseToUnicode(e.getValue())));
                    // Il faut ajouter un listener emote
                    //JusticeBot.api.getMessageByLink("").get().thenAccept(m -> m.getReactionByEmoji("").get().get)
                });

         */
    }
}
