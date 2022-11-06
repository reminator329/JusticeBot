package fr.reminator.justicebot.main;

import fr.reminator.justicebot.listeners.CommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class JusticeBot {

    public final static File fileSuggestions = new File("suggestions.txt");
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
        System.out.println("Je suis prêt !");
    }
}
