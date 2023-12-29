package fr.reminator.justicebot.commands.mystery_word.model;

import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public class MysteryWordPlayer {

    private final User user;
    private int score = 0;

    private final List<Word> findedWords = new ArrayList<>();

    public MysteryWordPlayer(User user) {
        this.user = user;
    }

    public void addFindedWord(Word word) {
        findedWords.add(word);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }

    public List<Word> getFindedWords() {
        return findedWords;
    }
}
