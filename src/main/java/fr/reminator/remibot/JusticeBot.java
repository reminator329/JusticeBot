package fr.reminator.remibot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class JusticeBot {

    public static void main(String[] args) {
        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token)
                .setAllIntents().login().join();
        System.out.println(api.createBotInvite());

        api.addMessageCreateListener(event -> {
            System.out.println("test");
            System.out.println(event.getMessage());
            System.out.println(event.getMessageContent());
            System.out.println(event.getMessage().getContent());
            if (event.getMessageContent().equalsIgnoreCase("a")) {
                System.out.println("Bonjour");
                event.getChannel().sendMessage("Pong!");
            }
        });
    }
}
