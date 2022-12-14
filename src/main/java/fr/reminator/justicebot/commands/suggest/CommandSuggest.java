package fr.reminator.justicebot.commands.suggest;

import com.vdurmont.emoji.EmojiParser;
import fr.reminator.justicebot.commands.Command;
import fr.reminator.justicebot.main.JusticeBot;
import fr.reminator.justicebot.utils.JsonUtils;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.List;

public class CommandSuggest extends Command {

    private final JsonUtils jsonUtils;

    public CommandSuggest() {
        super();
        this.jsonUtils = new JsonUtils(JusticeBot.fileSuggestions.getAbsoluteFile());
    }

    @Override
    public String getLabel() {
        return "suggest";
    }

    @Override
    public String getDescription() {
        return "Permet d'envoyer une suggestion au serveur (si activée).";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(SlashCommandOption.create(SlashCommandOptionType.STRING, "suggestion", "La suggestion à envoyer au serveur discord.", true));
    }

    @Override
    public boolean isEnabledInDms() {
        return false;
    }

    @Override
    public void otherCalls() {
        // setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {

        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        SlashCommandInteractionOption suggestion = slashCommandInteraction.getOptionByName("suggestion").orElse(null);
        if (suggestion == null) return;
        String suggestionValue = suggestion.getStringValue().orElse(null);
        if (suggestionValue == null) return;

        String content = jsonUtils.read();
        JSONObject json = new JSONObject(content);

        Server server = event.getInteraction().getServer().orElse(null);
        if (server == null) return;
        String idServeurAsString = server.getIdAsString();
        JSONObject guildSuggestions;
        boolean enable;
        String channelId;
        try {
            guildSuggestions = json.getJSONObject(idServeurAsString);
            enable = guildSuggestions.getBoolean("enable");
            if (!enable) {
                event.getInteraction().createImmediateResponder().setContent("Les suggestions ne sont pas activées dans ce serveur. Veuillez contacter un modérateur pour activer cette commande.").setFlags(MessageFlag.EPHEMERAL).respond();
                return;
            }
            channelId = guildSuggestions.getString("channel");
        } catch (JSONException e) {
            event.getInteraction().createImmediateResponder().setContent("Les suggestions ne sont pas activées dans ce serveur. Veuillez contacter un modérateur pour activer cette commande.").setFlags(MessageFlag.EPHEMERAL).respond();
            return;
        }

        User user = event.getInteraction().getUser();
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription(suggestionValue)
                .setColor(Color.GREEN)
                .setFooter(user.getName() + "#" + user.getDiscriminator(), String.valueOf(user.getAvatar().getUrl()));

        Channel channel = JusticeBot.api.getChannelById(channelId).orElse(null);
        if (channel == null) return;
        TextChannel textChannel = channel.asTextChannel().orElse(null);
        if (textChannel == null) {
            System.out.println("erreur");
            return;
        }
        textChannel.sendMessage(embed).thenAccept(message -> {
            message.addReactions(EmojiParser.parseToUnicode(":heart:"));
            message.addReactions(EmojiParser.parseToUnicode(":neutral_face:"));
            message.addReactions(EmojiParser.parseToUnicode(":x:"));
        });
        event.getInteraction().createImmediateResponder().setContent("Suggestion envoyée dans le salon <#" + channelId + ">").setFlags(MessageFlag.EPHEMERAL).respond();
    }
}
