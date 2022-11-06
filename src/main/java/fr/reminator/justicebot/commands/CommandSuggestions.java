package fr.reminator.justicebot.commands;

import fr.reminator.justicebot.main.JusticeBot;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

public class CommandSuggestions extends Command {

    public CommandSuggestions() {
        super();
        File fileSuggestions = JusticeBot.fileSuggestions;
        try {
            if (!fileSuggestions.exists()) {
                final boolean newFile = fileSuggestions.createNewFile();
                if (!newFile) {
                    System.out.println("Erreur création du fichier suggestions");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLabel() {
        return "suggestions";
    }

    @Override
    public String getDescription() {
        return "Active ou désactive les suggestions";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "enable", "Active les suggestions",
                    List.of(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Le channel où envoyer les suggestions", true))),

                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "disable", "Désactive les suggestions")
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

        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        List<SlashCommandInteractionOption> options = slashCommandInteraction.getOptions();

        String content = "";
        switch (options.get(0).getName()) {
            case "enable" -> {
                ServerChannel channel = enable(event);
                if (channel == null) {
                    content = "La commande n'a pas fonctionné.";
                } else {
                    content = "Suggestions activées dans le channel <#" + channel.getIdAsString() + ">";
                }
            }
            case "disable" -> {
                disable(event);
                content = "Suggestions désactivées";
            }
        }
        event.getInteraction().createImmediateResponder().setContent(content).setFlags(MessageFlag.EPHEMERAL).respond();
    }

    private ServerChannel enable(SlashCommandCreateEvent event) {

        StringBuilder content = new StringBuilder();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(JusticeBot.fileSuggestions.getAbsoluteFile()));
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (content.toString().equals("")) {
            content.append("{}");
        }
        JSONObject json = new JSONObject(content.toString());

        Server server = event.getInteraction().getServer().orElse(null);
        if (server == null) return null;
        String idServeurAsString = server.getIdAsString();

        SlashCommandInteractionOption enable = event.getSlashCommandInteraction().getOptionByName("enable").orElse(null);
        if (enable == null) return null;
        SlashCommandInteractionOption channel = enable.getOptionByName("channel").orElse(null);
        if (channel == null) return null;
        ServerChannel channelValue = channel.getChannelValue().orElse(null);
        if (channelValue == null) return null;
        String idChannelAsString = channelValue.getIdAsString();

        JSONObject guildSuggestions = new JSONObject();
        guildSuggestions.put("enable", true);
        guildSuggestions.put("channel", idChannelAsString);
        json.put(idServeurAsString, guildSuggestions);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(JusticeBot.fileSuggestions.getAbsoluteFile()));
            bw.write(json.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channelValue;
    }

    private void disable(SlashCommandCreateEvent event) {

        StringBuilder content = new StringBuilder();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(JusticeBot.fileSuggestions.getAbsoluteFile()));
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (content.toString().equals("")) {
            content.append("{}");
        }
        JSONObject json = new JSONObject(content.toString());

        Server server = event.getInteraction().getServer().orElse(null);
        if (server == null) return;
        String idServeurAsString = server.getIdAsString();

        JSONObject guildSuggestions = new JSONObject();
        guildSuggestions.put("enable", false);
        guildSuggestions.put("channel", JSONObject.NULL);
        json.put(idServeurAsString, guildSuggestions);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(JusticeBot.fileSuggestions.getAbsoluteFile()));
            bw.write(json.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
