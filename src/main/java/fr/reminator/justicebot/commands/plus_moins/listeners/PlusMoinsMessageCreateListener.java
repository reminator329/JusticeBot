package fr.reminator.justicebot.commands.plus_moins.listeners;

import fr.reminator.justicebot.commands.plus_moins.PlusMoinsUtils;
import fr.reminator.justicebot.main.JusticeBot;
import fr.reminator.justicebot.utils.GameUtils;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class PlusMoinsMessageCreateListener implements MessageCreateListener {

    private final PlusMoinsUtils gameUtils;
    private final ServerChannel serverChannel;
    private final long answer;
    private final boolean help;

    public PlusMoinsMessageCreateListener(PlusMoinsUtils gameUtils) {
        this.gameUtils = gameUtils;
        this.serverChannel = gameUtils.getServerChannel();
        this.answer = gameUtils.getAnswer();
        this.help = gameUtils.isHelp();
    }
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String messageContent = event.getMessageContent();
        if (!event.getChannel().getIdAsString().equals(this.serverChannel.getIdAsString())) return;

        // Transformation en Long
        long userVal = 0;
        try {
            userVal = Long.parseLong(messageContent);
        } catch (NumberFormatException e) {
            return;
        }

        if (userVal == this.answer) {

            gameUtils.setDeniedSendMessageForRoleGame();

            JusticeBot.api.removeListener(this);

            event.getMessage().reply("GG ! " + event.getMessageAuthor().asUser().get().getMentionTag() + " a gagné !! :tada:\n" +
                    "Le nombre mystère était bien " + this.answer);
        } else if (this.help) {
            if (userVal < this.answer) {
                event.getMessage().reply("Le nombre est **plus grand** !");
            } else {
                event.getMessage().reply("Le nombre est **plus petit** !");
            }
        }
    }
}
