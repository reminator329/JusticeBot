package fr.reminator.justicebot.utils;

import java.io.*;

public class JsonUtils {

    private final File jsonFile;

    public JsonUtils(File path) {
        this.jsonFile = path;
    }

    public String read() {
        StringBuilder content = new StringBuilder();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(jsonFile));
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
        return content.toString();
    }

    public void write(String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
