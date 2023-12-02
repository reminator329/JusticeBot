package fr.reminator.justicebot.commands.plus_moins;

import fr.reminator.justicebot.utils.GameUtils;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.permission.Role;

public class PlusMoinsUtils extends GameUtils {

    private final long answer;
    private final boolean help;

    public PlusMoinsUtils(ServerChannel serverChannel, Role roleGame, long answer, boolean help) {
        super(serverChannel, roleGame);
        this.answer = answer;
        this.help = help;
    }

    public long getAnswer() {
        return answer;
    }

    public boolean isHelp() {
        return help;
    }
}
