package fr.reminator.justicebot.commands.mystery_word.utils;

import fr.reminator.justicebot.commands.mystery_word.listeners.MysteryWordListener;
import fr.reminator.justicebot.commands.mystery_word.model.MysteryWordPlayer;
import fr.reminator.justicebot.commands.mystery_word.model.Word;
import fr.reminator.justicebot.enums.GoogleSheet;
import fr.reminator.justicebot.utils.GameUtils;
import fr.reminator.justicebot.utils.HTTPRequest;
import fr.reminator.justicebot.utils.TimeUtils;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.io.IOException;
import java.util.*;

public class MysteryWordUtils extends GameUtils {

    private final SlashCommandInteraction slashCommandInteraction;
    private final long scoreMax;
    private Word answer;
    private final long waitingTime;
    private String hint;
    private final long delayHint = 0L;
    private long periodHint;
    private final char charHint = '.';
    private final Timer timerHint = new Timer();
    private TimerTask currentTimerTask = null;
    private final Map<String, MysteryWordPlayer> winners = new HashMap<>();

    public MysteryWordUtils(SlashCommandInteraction slashCommandInteraction, ServerChannel serverChannel, Role roleGame, long scoreMax, long waitingTime) {
        super(serverChannel, roleGame);
        this.slashCommandInteraction = slashCommandInteraction;
        this.scoreMax = scoreMax;
        this.waitingTime = waitingTime;
    }

    private void chooseWord() {

        // Choix du mot
        String lienListeMots = GoogleSheet.CSV_LISTE_MOTS.getUrl();

        String csv = "";
        try {
            csv = new HTTPRequest(lienListeMots).GET();
            List<Word> words = new WordParser().getWords(csv);

            int nombre = (int) (Math.random() * words.size());

            this.answer = words.get(nombre);

            slashCommandInteraction.createImmediateResponder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .setContent("La réponse est " + this.answer.getName())
                    .respond();

            this.hint = answer.getName().replaceAll("[A-z]", "" + charHint);

            long totalUnderscores = getNbUnderscores(this.hint);

            long maxTime;
            if (!this.answer.getName().contains(" ")) {
                maxTime = (long) (1.5 * TimeUtils.MIN);
            } else {
                maxTime = 2 * TimeUtils.MIN;
            }

            this.periodHint = (maxTime - delayHint) / totalUnderscores;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRules() {

        String strMots;
        if (scoreMax > 1) {
            strMots = "mots";
        } else {
            strMots = "mot";
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Nombre mystère")
                .addField("But du jeu", "Trouver le mot mystère. \n" +
                        "Pour gagner, il faut trouver " + scoreMax + " " + strMots + ".")
                .addField("Récompense", "10B ℝ");

        ServerTextChannel serverTextChannel = this.getServerChannel().asServerTextChannel().get();
        serverTextChannel.sendMessage(embedBuilder);
    }

    public void sendAndWaitTimer(long waitingTime) {

        // ***** Démarrage d'un timer
        sendTimer(waitingTime);

        boolean waitTimer = true;
        while (waitTimer) {
            try {
                getTimerLock().doWait();
                waitTimer = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startGame() {

        chooseWord();

        sendRules();

        // ***** Modifications du channel
        setDeniedSendMessageForRoleGame();

        sendAndWaitTimer(this.waitingTime);

        // Ajout d'un listener qui va écouter les réponses des joueurs
        addListener(new MysteryWordListener(this));

        // ***** Modifications du channel
        setAllowedSendMessageForRoleGame();

        startTimerHint();
    }

    private void startNewGame() {

        chooseWord();

        sendAndWaitTimer(30);

        // Ajout d'un listener qui va écouter les réponses des joueurs
        addListener(new MysteryWordListener(this));

        // ***** Modifications du channel
        setAllowedSendMessageForRoleGame();

        startTimerHint();
    }

    @Override
    public void stopGame() {
        stopTimerHint();
        super.stopGame();
    }

    public void checkAnswer(MessageCreateEvent event) {

        String messageContent = event.getMessageContent();
        boolean isAnswer = messageContent.equalsIgnoreCase(answer.getName());

        if (isAnswer) {

            stopListeners();

            setDeniedSendMessageForRoleGame();

            User user = event.getMessageAuthor().asUser().get();

            event.getMessage().reply("GG " + user.getMentionTag() + ", tu a trouvé !! :tada:\n" + "Le mot mystère était bien " + answer.getName());
            String description = answer.getDescription();
            String citation = answer.getCitation();
            if (description != null && !description.isEmpty())
                getServerChannel().asServerTextChannel().get().sendMessage("# Le saviez vous ?\n" + description);
            if (citation != null && !citation.isEmpty())
                getServerChannel().asServerTextChannel().get().sendMessage("*\"" + citation + "\"*");

            stopTimerHint();

            MysteryWordPlayer winner;

            if (winners.containsKey(user.getIdAsString())) {
                winner = winners.get(user.getIdAsString());
            } else {
                winner = new MysteryWordPlayer(user);
                winners.put(user.getIdAsString(), winner);
            }
            winner.setScore(winner.getScore() + 1);
            winner.addFindedWord(this.answer);

            if (winner.getScore() == scoreMax) {
                if (scoreMax > 1) {
                    StringBuilder message = new StringBuilder(user.getMentionTag() + " a trouvé les " + winner.getScore() + " mots ci-dessous et gagne le jeu !! :tada:");
                    for (Word word : winner.getFindedWords()) {
                        message.append("\n- ").append(word.getName());
                    }
                    getServerChannel().asServerTextChannel().get().sendMessage(message.toString());
                }
            } else {
                startNewGame();
            }

        } else {
            event.getMessage().reply("Faux ! Voici l'indice disponible :\n`" + hint + "`");
        }

    }

    private void startTimerHint() {
        this.currentTimerTask = new TimerTaskHint();
        timerHint.schedule(this.currentTimerTask, this.delayHint, this.periodHint);
    }

    private void stopTimerHint() {
        this.currentTimerTask.cancel();
        timerHint.purge();
    }

    private long getNbUnderscores(String str) {
        return str.chars().filter(c -> c == charHint).count();
    }

    public Word getAnswer() {
        return answer;
    }

    public String getHint() {
        return hint;
    }

    public class TimerTaskHint extends TimerTask {

        @Override
        public void run() {
            long nbUnderscores = getNbUnderscores(hint);

            long skip = (long) (Math.random() * nbUnderscores);
            for (int i = 0; i < hint.length(); i++) {
                if (hint.charAt(i) == charHint) {
                    if (skip == 0) {
                        hint = hint.substring(0, i) + answer.getName().charAt(i) + hint.substring(i + 1);
                        break;
                    }
                    skip--;
                }
            }
            getServerChannel().asServerTextChannel().get().sendMessage("Un nouvel indice a été donné !\n`" + hint + "`");
            if (getNbUnderscores(hint) == 0) {
                getServerChannel().asServerTextChannel().get().sendMessage("Toutes les lettres ont été données !");
                stopTimerHint();
            }
        }
    }

}
