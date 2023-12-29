package fr.reminator.justicebot.commands.mystery_word.listeners;

import fr.reminator.justicebot.commands.mystery_word.utils.MysteryWordUtils;
import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MysteryWordListener implements MessageCreateListener {

    private final MysteryWordUtils gameUtils;

    public MysteryWordListener(MysteryWordUtils gameUtils) {
        this.gameUtils = gameUtils;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageAuthor().isBotUser()) return;
        if (!event.getChannel().getIdAsString().equals(this.gameUtils.getServerChannel().getIdAsString())) return;

        gameUtils.checkAnswer(event);
    }
}
