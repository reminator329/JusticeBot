package fr.reminator.justicebot.commands.plus_moins.listeners;

import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.entity.channel.RegularServerChannel;
import org.javacord.api.entity.channel.RegularServerChannelUpdater;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class PlusMoinsListener implements MessageCreateListener {

    private final ServerChannel serverChannel;
    private final long answer;
    private final boolean help;

    public PlusMoinsListener(ServerChannel serverChannel, long answer, boolean help) {
        this.serverChannel = serverChannel;
        this.answer = answer;
        this.help = help;
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

            Role roleGang = serverChannel.getServer().getRoleById("1173027998891511940").get();
            RegularServerChannel regularServerChannel = serverChannel.asRegularServerChannel().get();

            Permissions overwrittenPermissions = regularServerChannel.getOverwrittenPermissions(roleGang);

            new RegularServerChannelUpdater<>(regularServerChannel).addPermissionOverwrite(
                    roleGang,
                    new PermissionsBuilder(overwrittenPermissions).setDenied(PermissionType.SEND_MESSAGES).build()
            ).update();

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
