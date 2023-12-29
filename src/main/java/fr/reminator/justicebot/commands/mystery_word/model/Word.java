package fr.reminator.justicebot.commands.mystery_word.model;

public class Word {

    private final String name;
    private final String description;
    private final String citation;

    public Word(String name, String description, String citation) {
        this.name = name;
        this.description = description;
        this.citation = citation;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCitation() {
        return citation;
    }
}
