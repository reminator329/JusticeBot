package fr.reminator.justicebot.utils;

import fr.reminator.justicebot.commands.mystery_word.listeners.MysteryWordListener;
import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.entity.channel.RegularServerChannel;
import org.javacord.api.entity.channel.RegularServerChannelUpdater;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.listener.GloballyAttachableListener;
import org.javacord.api.listener.ObjectAttachableListener;

import java.util.HashSet;
import java.util.Set;

public class GameUtils {

    private final ServerChannel serverChannel;
    private final Role roleGame;
    private final ThreadEvent timerLock;
    private final Set<GloballyAttachableListener> listeners = new HashSet<>();

    public GameUtils(ServerChannel serverChannel, Role roleGame) {
        this.serverChannel = serverChannel;
        this.roleGame = roleGame;
        this.timerLock = new ThreadEvent();
    }

    public ServerChannel getServerChannel() {
        return serverChannel;
    }

    public Role getRoleGame() {
        return roleGame;
    }

    public ThreadEvent getTimerLock() {
        return timerLock;
    }

    /**
     * Envoi un message qui se modifie chaque seconde indiquant dans combien de temps le jeu commence.
     * @param waitingTime Temps en secondes
     */
    public void sendTimer(long waitingTime) {
        timerLock.clear();
        this.sendTimer(null, waitingTime);
    }

    private void sendTimer(Message message, long waitingTime) {

        ServerTextChannel serverTextChannel = this.serverChannel.asServerTextChannel().get();

        String text = "";

        // TODO Faire un meilleur truc
        if (waitingTime == 0) {
            text = "Bonne chance !";
        } else if (waitingTime < 2) {
            text = "Le jeu démarre dans **" + waitingTime + "** seconde.";
        } else if (waitingTime < 60) {
            text = "Le jeu démarre dans **" + waitingTime + "** secondes.";
        } else {
            long min = waitingTime / 60;
            long sec = waitingTime - 60 * min;
            if (min < 2) {
                text = "Le jeu démarre dans **" + min + "** minute et **" + sec + "** secondes.";
            } else {
                text = "Le jeu démarre dans **" + min + "** minutes et **" + sec + "** secondes.";
            }
        }

        if (message == null) {
            serverTextChannel.sendMessage(text).thenAccept(message2 -> this.sendTimerThenAccept(message2, waitingTime));
        } else {
            message.edit(text).thenAccept(message2 -> this.sendTimerThenAccept(message2, waitingTime));
        }
    }

    private void sendTimerThenAccept(Message message, long waitingTime) {
        if (waitingTime <= 0) {
            this.timerLock.set();
            return;
        }
        try {
            Thread.sleep(1000);
            this.sendTimer(message, waitingTime - 1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Active la posibilité d'écrire des messages au rôle qui participe au jeu
     */
    public void setAllowedSendMessageForRoleGame() {
        RegularServerChannel regularServerChannel = serverChannel.asRegularServerChannel().get();

        Permissions overwrittenPermissions = regularServerChannel.getOverwrittenPermissions(this.roleGame);

        new RegularServerChannelUpdater<>(regularServerChannel).addPermissionOverwrite(
                this.roleGame,
                new PermissionsBuilder(overwrittenPermissions).setAllowed(PermissionType.SEND_MESSAGES).build()
        ).update();
    }

    /**
     * Désactive la posibilité d'écrire des messages au rôle qui participe au jeu
     */
    public void setDeniedSendMessageForRoleGame() {
        RegularServerChannel regularServerChannel = serverChannel.asRegularServerChannel().get();

        Permissions overwrittenPermissions = regularServerChannel.getOverwrittenPermissions(this.roleGame);

        new RegularServerChannelUpdater<>(regularServerChannel).addPermissionOverwrite(
                this.roleGame,
                new PermissionsBuilder(overwrittenPermissions).setDenied(PermissionType.SEND_MESSAGES).build()
        ).update();
    }

    public void stopGame() {
        stopListeners();
    }

    public void addListener(GloballyAttachableListener listener) {
        JusticeBot.api.addListener(listener);
        listeners.add(listener);
    }

    public void stopListeners() {
        listeners.forEach(listener -> {
            JusticeBot.api.removeListener(listener);
        });
    }
}
