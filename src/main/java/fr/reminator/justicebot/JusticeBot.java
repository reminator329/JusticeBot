package fr.reminator.justicebot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class JusticeBot {

    public static void main(String[] args) {
        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token)
                .setAllIntents().login().join();
        System.out.println(api.createBotInvite());

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong !");
            }
        });
    }
}
