package fr.reminator.justicebot.commands.plus_moins;

import fr.reminator.justicebot.commands.Command;
import fr.reminator.justicebot.commands.plus_moins.listeners.PlusMoinsListener;
import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PlusMoinsCommand extends Command {

    // Nom des options
    private static final String channelOptionName = "channel";
    private static final String minValOptionName = "minValue";
    private static final String maxValOptionName = "maxValue";
    private static final String withHintOptionName = "withHint";
    private static boolean withHintDefaultValue = true;

    // Autres variables
    private static final int waitingTime = 10;

    @Override
    public String getLabel() {
        return "plus-ou-moins";
    }

    @Override
    public String getDescription() {
        return "Commande pour lancer un plus ou moins compétitif";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createChannelOption(channelOptionName, "Salon dans lequel lancer le jeu", true, Set.of(ChannelType.SERVER_TEXT_CHANNEL)),
                SlashCommandOption.createLongOption(minValOptionName, "Entre la valeur minimum", true),
                SlashCommandOption.createLongOption(maxValOptionName, "Entre la valeur maximale", true),
                SlashCommandOption.createBooleanOption(withHintOptionName, "Avec les aides + ou -", false)
        );
    }

    @Override
    public boolean isEnabledInDms() {
        return false;
    }

    @Override
    public void otherCalls() {
        setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {

        // ***** Récupération de la commande
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();

        System.out.println("[PlusMoinsCommand] Received command");

        // ***** Récupération et vérification des options
        // Channel du jeu
        SlashCommandInteractionOption channelOption = slashCommandInteraction.getOptionByName(channelOptionName).orElse(null);
        if (channelOption == null) return;
        ServerChannel serverChannel = channelOption.getChannelValue().orElse(null);
        if (serverChannel == null) return;

        // Valeur minimale
        SlashCommandInteractionOption minValOption = slashCommandInteraction.getOptionByName(minValOptionName).orElse(null);
        if (minValOption == null) return;
        Long minVal = minValOption.getLongValue().orElse(null);
        if (minVal == null) return;

        // Valeur maximale
        SlashCommandInteractionOption maxValOption = slashCommandInteraction.getOptionByName(maxValOptionName).orElse(null);
        if (maxValOption == null) return;
        Long maxVal = maxValOption.getLongValue().orElse(null);
        if (maxVal == null) return;

        // With help
        boolean withHint = withHintDefaultValue;
        SlashCommandInteractionOption withHelpOption = slashCommandInteraction.getOptionByName(withHintOptionName).orElse(null);
        if (withHelpOption != null) {
            withHint = withHelpOption.getBooleanValue().orElse(withHintDefaultValue);
        }

        if (maxVal < minVal) {
            slashCommandInteraction.createImmediateResponder().setContent("Attention ! La valeur maximale **" + maxVal + "** doit être supérieure ou égale à la valeur minimale **" + minVal + "** !").respond();
            return;
        }

        // Choix de la réponse
        long nombre = (long) (minVal + Math.random() * (maxVal - minVal));

        System.out.println("[PlusMoinsCommand] Options OK");

        // Ajout d'un listener qui va écouter les réponses des joueurs
        JusticeBot.api.addListener(new PlusMoinsListener(serverChannel, nombre, withHint));

        ServerTextChannel serverTextChannel = serverChannel.asServerTextChannel().get();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Nombre mystère")
                .addField("But du jeu", "Trouver un nombre choisit aléatoirement entre **" + minVal + "** et **" + maxVal + "**")
                .addField("Récompense", "10B ℝ");

        if (withHint) {
            embedBuilder.addField("Particularités", "Le bot donnera des indices !");
        } else {
            embedBuilder.addField("Particularités", "Aucun indice ne sera donné !");
        }
        serverTextChannel.sendMessage(embedBuilder);

        slashCommandInteraction.createImmediateResponder()
                .setFlags(MessageFlag.EPHEMERAL)
                .setContent("La réponse est " + nombre)
                .respond();

        // ***** Démarrage d'un timer

        CompletableFuture<Message> message = serverTextChannel.sendMessage("Le jeu démarre dans **" + (waitingTime + 2) + "** secondes.");

        for (int i = waitingTime ; i >= 0; i = i - 2) {
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (i == 0) {
                message = message.getNow(null).edit("Bonne chance !");
            } else if (i < 2) {
                message = message.getNow(null).edit("Le jeu démarre dans **" + i + "** secondes.");
            } else {
                message = message.getNow(null).edit("Le jeu démarre dans **" + i + "** secondes.");
            }
        }

        Role roleGang = serverChannel.getServer().getRoleById("1173027998891511940").get();
        RegularServerChannel regularServerChannel = serverChannel.asRegularServerChannel().get();

        Permissions overwrittenPermissions = regularServerChannel.getOverwrittenPermissions(roleGang);

        new RegularServerChannelUpdater<>(regularServerChannel).addPermissionOverwrite(
                roleGang,
                new PermissionsBuilder(overwrittenPermissions).setAllowed(PermissionType.SEND_MESSAGES).build()
        ).update();
    }
}
