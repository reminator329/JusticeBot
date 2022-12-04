package fr.reminator.justicebot.commands.rolemenu.model;

import fr.reminator.justicebot.main.JusticeBot;
import fr.reminator.justicebot.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MenuRoleGestion {

    private static final Map<String, MenuRoleGestion> instances = new HashMap<>();
    private final Map<String, String> roles;
    private String idRoleTemp;
    private String idChannel;

    private JsonUtils jsonUtils;

    private MenuRoleGestion() {
        roles = new HashMap<>();

        File fileRoleMenu = JusticeBot.fileRoleMenu;
        this.jsonUtils = new JsonUtils(fileRoleMenu.getAbsoluteFile());
        try {
            if (!fileRoleMenu.exists()) {
                final boolean newFile = fileRoleMenu.createNewFile();
                if (!newFile) {
                    System.out.println("Erreur création du fichier roleMenu");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MenuRoleGestion getInstance(String idChannel) {
        if (instances.containsKey(idChannel)) {
            return instances.get(idChannel);
        }
        MenuRoleGestion menuRoleGestion = new MenuRoleGestion();
        menuRoleGestion.idChannel = idChannel;
        instances.put(idChannel, menuRoleGestion);
        return menuRoleGestion;
    }

    public MenuRoleGestion withRole(String idRole) {
        idRoleTemp = idRole;
        return this;
    }

    public MenuRoleGestion withEmote(String emote) {

        System.out.println(emote + "t coucou");
        roles.put(idRoleTemp, emote);
        return this;
    }

    public Set<String> getRoles() {
        return roles.keySet();
    }

    public Set<Map.Entry<String, String>> getMenu() {
        return roles.entrySet();
    }

    public void saveJson() {
        String content = jsonUtils.read();
        JSONObject json = new JSONObject(content);

        JSONArray jsonChannel = new JSONArray();

        roles.forEach((idRole, emote) -> {
            System.out.println(emote);
            JSONObject role = new JSONObject();
            role.put("idRole", idRole);
            role.put("emote", emote);
            jsonChannel.put(role);
        });

        json.remove(idChannel);
        json.put(idChannel, jsonChannel);

        jsonUtils.write(json.toString());
    }

    public void updateFromJson() {
        String content = jsonUtils.read();
        JSONObject json = new JSONObject(content);

        JSONArray jsonArray = json.getJSONArray(idChannel);

        clear();

        jsonArray.forEach(o -> {
            JSONObject role = (JSONObject) o;

            String idRole = role.getString("idRole");
            String emote = role.getString("emote");

            roles.put(idRole, emote);
        });
        System.out.println(roles);
    }

    public void clear() {
        roles.clear();
    }
}
