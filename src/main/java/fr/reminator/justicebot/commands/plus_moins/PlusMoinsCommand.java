package fr.reminator.justicebot.commands.plus_moins;

import fr.reminator.justicebot.commands.Command;
import fr.reminator.justicebot.commands.plus_moins.listeners.PlusMoinsMessageCreateListener;
import fr.reminator.justicebot.main.JusticeBot;
import fr.reminator.justicebot.utils.GameUtils;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;
import java.util.Set;

public class PlusMoinsCommand extends Command {

    // Nom des options
    private static final String channelOptionName = "channel";
    private static final String roleOptionName = "role";
    private static final String minValOptionName = "minValue";
    private static final String maxValOptionName = "maxValue";
    private static final String withHintOptionName = "withHint";

    // Autres variables
    // TODO mettre une option de commande
    private static final int waitingTime = 30;

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
                SlashCommandOption.createRoleOption(roleOptionName, "Rôle qui peut jouer au jeu.", true),
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

        // Role

        SlashCommandInteractionOption roleOption = slashCommandInteraction.getOptionByName(roleOptionName).orElse(null);
        if (roleOption == null) return;
        Role roleGame = roleOption.getRoleValue().orElse(null);
        if (roleGame == null) return;

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

        if (maxVal < minVal) {
            slashCommandInteraction.createImmediateResponder().setContent("Attention ! La valeur maximale **" + maxVal + "** doit être supérieure ou égale à la valeur minimale **" + minVal + "** !").respond();
            return;
        }

        // With help
        boolean withHintDefaultValue = true;
        boolean withHint = true;
        SlashCommandInteractionOption withHelpOption = slashCommandInteraction.getOptionByName(withHintOptionName).orElse(null);
        if (withHelpOption != null) {
            withHint = withHelpOption.getBooleanValue().orElse(withHintDefaultValue);
        }

        // ***** Traitements du jeu

        // Choix de la réponse
        long nombre = (long) (minVal + Math.random() * (maxVal - minVal));

        System.out.println("[PlusMoinsCommand] Options OK");

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

        // ***** Création du gameUtils
        PlusMoinsUtils gameUtils = new PlusMoinsUtils(serverChannel, roleGame, nombre, withHint);

        // ***** Modifications du channel
        gameUtils.setDeniedSendMessageForRoleGame();

        // ***** Démarrage d'un timer
        gameUtils.sendTimer(waitingTime);
        try {
            gameUtils.getTimerLock().doWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ajout d'un listener qui va écouter les réponses des joueurs
        JusticeBot.api.addListener(new PlusMoinsMessageCreateListener(gameUtils));

        // ***** Modifications du channel
        gameUtils.setAllowedSendMessageForRoleGame();
    }
}
