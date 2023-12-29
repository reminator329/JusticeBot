package fr.reminator.justicebot.commands.mystery_word.utils;

import fr.reminator.justicebot.commands.mystery_word.model.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordParser {

    public List<Word> getWords(String csv) {

        List<Word> res = new ArrayList<>();
        String[] wordsList = csv.split("\n");

        for (String word : wordsList) {
            String[] items = word.split(",");
            if (items.length == 0) continue;

            String name = items[0];
            String description = "";
            String citation = "";
            if (items.length > 1)
                description = items[1];
            if (items.length > 2)
                citation = items[2];

            res.add(new Word(name, description, citation));
        }
        return res;
    }
}
