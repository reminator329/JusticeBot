package fr.reminator.justicebot.commands.rolemenu.model;

import fr.reminator.justicebot.main.JusticeBot;
import fr.reminator.justicebot.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MenuRoleGestion {

    private static final Map<String, MenuRoleGestion> instances = new HashMap<>();
    private final List<Role> roles;
    private String idRoleTemp;
    private String idChannel;

    private final JsonUtils jsonUtils;

    private MenuRoleGestion() {
        roles = new ArrayList<>();

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

    public void addRole(Role role) {
        roles.add(role);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void saveJson() {
        String content = jsonUtils.read();
        JSONObject json = new JSONObject(content);

        JSONArray jsonChannel = new JSONArray();

        roles.forEach(role -> {
            JSONObject roleJson = new JSONObject();
            roleJson.put("idRole", role.getIdRole());
            roleJson.put("emote", role.getEmote());
            roleJson.put("description", role.getDescription());
            System.out.println(roleJson + " " + role.getDescription());
            jsonChannel.put(roleJson);
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
            String description = role.getString("description");

            roles.add(new Role(idRole, emote, description));
        });
    }

    public void clear() {
        roles.clear();
    }
}
