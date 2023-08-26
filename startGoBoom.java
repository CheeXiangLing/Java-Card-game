import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class startGoBoom { //complie with "javac startGoBoom.java" then type "java startGoBoom"
    public static void main(String[] args) {

        GoBoom game = new GoBoom(4);

        JButton drawCardButton = new JButton("Draw Card");
        drawCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.getStartGame()) {
                    game.playerDrawCard();
                    System.out.println(">d\n");
                    game.setResultText("");

                    game.updateplayerText(game);
                    game.cardAreaClear();
                    game.makeCardButtons(game);
                }
            }
        });
        game.buttonAreaAdd(drawCardButton);

        JButton startButton = new JButton("Start New Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.newGame();
                game.setRound(1);
                game.setTrick(1);
                System.out.println(">s\n");
                game.setStartGame(true);

                game.updateplayerText(game);
                game.cardAreaClear();
                game.makeCardButtons(game);
            }
        });
        game.buttonAreaAdd(startButton);

        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.getStartGame()) {
                    game.resetGame();
                    System.out.println(">r\n");
                    game.setStartGame(true);

                    game.updateplayerText(game);
                    game.cardAreaClear();
                    game.makeCardButtons(game);
                }
            }
        });
        game.buttonAreaAdd(resetButton);

        JButton loadButton = new JButton("Resume Previous Game");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Player p : game.getPlayers()) {
                    p.clearPlayerCards();
                    p.setScore(0);
                }

                game.clearDeck();
                game.clearCenter();

                System.out.println(">l\n");
                game.loadGame("saveGoBoom.txt");
                game.setStartGame(true);

                game.updateplayerText(game);
                game.cardAreaClear();
                game.makeCardButtons(game);
            }
        });
        game.buttonAreaAdd(loadButton);

        JButton quitButton = new JButton("Quit and Save Game");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(">x\n");
                game.saveGame("saveGoBoom.txt");
                System.exit(0);
            }
        });
        game.buttonAreaAdd(quitButton);

        game.frameVisible(true);
    }
}
