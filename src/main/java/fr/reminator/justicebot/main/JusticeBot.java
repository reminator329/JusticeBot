package fr.reminator.justicebot.main;

import fr.reminator.justicebot.main.listeners.CommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import java.io.File;

public class JusticeBot {

    public final static File fileSuggestions = new File("suggestions.txt");
    public final static File fileRoleMenu = new File("roleMenu.txt");
    public static DiscordApi api;

    public static void main(String[] args) {
        System.setProperty("user.timezone", "Europe/Paris");

        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token)
                .setAllIntents().login().join();
        JusticeBot.api = api;

        api.updateActivity(ActivityType.LISTENING, "/help");

        api.bulkOverwriteGlobalApplicationCommands(Commands.all()).join();

        api.addSlashCommandCreateListener(new CommandListener());

        // Enable debug logging
        FallbackLoggerConfiguration.setDebug(true);

        // Enable trace logging
        FallbackLoggerConfiguration.setTrace(true);

        System.out.println("Je suis prêt !");
    }
}
