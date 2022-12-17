package fr.reminator.justicebot.commands.rolemenu.model;

public class Role {

    private String idRole;
    private String emote;
    private String description;

    public Role(String id, String emote, String description) {
        this.idRole = id;
        this.emote = emote;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getEmote() {
        return emote;
    }

    public String getIdRole() {
        return idRole;
    }
}
