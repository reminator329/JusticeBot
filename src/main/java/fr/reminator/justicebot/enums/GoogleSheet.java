package fr.reminator.justicebot.enums;

public enum GoogleSheet {

    CSV_MULTI_QUIZ(""),
    CSV_LISTE_MOTS(""),
    ;

    private String url;

    GoogleSheet (String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
