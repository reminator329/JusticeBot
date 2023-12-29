package fr.reminator.justicebot.main;

import fr.reminator.justicebot.enums.GoogleSheet;
import fr.reminator.justicebot.main.listeners.CommandListener;
import fr.reminator.justicebot.utils.HTTPRequest;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import java.io.File;
import java.io.IOException;

public class JusticeBot {

    public final static File fileSuggestions = new File("suggestions.txt");
    public final static File fileRoleMenu = new File("roleMenu.txt");
    public static DiscordApi api;

    public static void main(String[] arguments) {
        System.setProperty("user.timezone", "Europe/Paris");

        String token = arguments[0];
        GoogleSheet.CSV_MULTI_QUIZ.setUrl(arguments[1]);
        GoogleSheet.CSV_LISTE_MOTS.setUrl(arguments[2]);

        DiscordApi api = new DiscordApiBuilder().setToken(token)
                .setAllIntents().login().join();
        JusticeBot.api = api;

        api.updateActivity(ActivityType.LISTENING, "/help");

        api.bulkOverwriteGlobalApplicationCommands(Commands.all()).join();

        api.addSlashCommandCreateListener(new CommandListener());

        FallbackLoggerConfiguration.setDebug(false);
        FallbackLoggerConfiguration.setTrace(false);

        System.out.println("Je suis prêt !");
    }
}
