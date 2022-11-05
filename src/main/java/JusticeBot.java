import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class JusticeBot {

    public static void main(String[] args) {
        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        System.out.println(api.createBotInvite());
    }
}
