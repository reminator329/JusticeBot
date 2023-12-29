package fr.reminator.justicebot.commands.mystery_word;

import fr.reminator.justicebot.commands.Command;
import fr.reminator.justicebot.commands.mystery_word.utils.MysteryWordUtils;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MysteryWordCommand extends Command {

    public static final Map<String, MysteryWordUtils> games = new HashMap<>();

    // Nom des options
    private static final String channelOptionName = "channel";
    private static final String roleOptionName = "role";
    private static final String scoreMaxOptionName = "scoreMax";
    private static final String waitingTimeOptionName = "waitingTime";
    @Override
    public String getLabel() {
        return "mystery-word";
    }

    @Override
    public String getDescription() {
        return "Commande pour lancer un jeu du mot mystère.";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createChannelOption(channelOptionName, "Salon dans lequel lancer le jeu", true, Set.of(ChannelType.SERVER_TEXT_CHANNEL)),
                SlashCommandOption.createRoleOption(roleOptionName, "Rôle qui peut jouer au jeu.", true),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.LONG)
                        .setName(scoreMaxOptionName)
                        .setDescription("Score à atteindre.")
                        .setRequired(true)
                        .setLongMinValue(1)
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.LONG)
                        .setName(waitingTimeOptionName)
                        .setDescription("Temps avant de démarrer le jeu en secondes.")
                        .setRequired(false)
                        .setLongMinValue(0)
                        .build()
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

        System.out.println("[MysteryWordCommand] Received command");

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

        // ScoreMax
        SlashCommandInteractionOption scoreMaxOption = slashCommandInteraction.getOptionByName(scoreMaxOptionName).orElse(null);
        if (scoreMaxOption == null) return;
        Long scoreMax = scoreMaxOption.getLongValue().orElse(null);
        if (scoreMax == null) return;

        // WaitingTime
        long waitingTimeDefaultValue = 120L;
        long waitingTime = waitingTimeDefaultValue;
        SlashCommandInteractionOption waitingTimeOption = slashCommandInteraction.getOptionByName(waitingTimeOptionName).orElse(null);
        if (waitingTimeOption != null) {
            waitingTime = waitingTimeOption.getLongValue().orElse(waitingTimeDefaultValue);
        }

        System.out.println("[MysteryWordCommand] Options OK");

        // ***** Création du gameUtils
        MysteryWordUtils gameUtils = new MysteryWordUtils(slashCommandInteraction, serverChannel, roleGame, scoreMax, waitingTime);

        String idAsString = serverChannel.getIdAsString();
        if (games.containsKey(idAsString)) {
            games.get(idAsString).stopGame();
        }
        games.put(idAsString, gameUtils);

        gameUtils.startGame();

    }
}
