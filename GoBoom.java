import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GoBoom extends GoBoom_GUI {

    private Player[] players;
    private LinkedList<Card> deck;
    private LinkedList<Card> center;
    private LinkedList<Card> discardedCards;

    private int trick;
    private int round;
    private int numOfplayersPlayed;
    private int trickWinnerId;
    private int currentPlayerIndex;
    private boolean currentPlayerTurn;
    private boolean startGame;

    GoBoom(int numOfPlayers) {
        this.startGame = false;
        this.players = new Player[numOfPlayers];
        this.deck = new LinkedList<>();
        this.center = new LinkedList<>();
        this.discardedCards = new LinkedList<>();

        this.trick = 0;
        this.round = 0;
        this.numOfplayersPlayed = 0;
        this.trickWinnerId = -1;
        this.currentPlayerIndex = 0;
        this.currentPlayerTurn = true;

        for (int i = 0; i < numOfPlayers; i++) {
            Player p = new Player(i + 1);
            players[i] = p;
        }
    }

    public boolean getStartGame() {
        return startGame;
    }

    public void setStartGame(boolean bool) {
        this.startGame = bool;
    }

    public int getTrick() {
        return trick;
    }

    public void setTrick(int trick) {
        this.trick = trick;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Player[] getPlayers() {
        return players;
    }

    public LinkedList<Card> getCenter() {
        return center;
    }

    public void clearCenter() {
        center.clear();
    }

    public LinkedList<Card> getDeck() {
        return deck;
    }

    public void clearDeck() {
        deck.clear();
    }

    public boolean getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public void setCurrentPlayerTurn(boolean currentPlayerTurn) {
        this.currentPlayerTurn = currentPlayerTurn;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public int getNumOfplayersPlayed() {
        return numOfplayersPlayed;
    }

    public void setNumOfplayersPlayed(int numOfplayersPlayed) {
        this.numOfplayersPlayed = numOfplayersPlayed;
    }

    private int getFirstPlayerIndex() {
        int index = 0;
        switch (center.get(0).getRank()) {
            case 14, 5, 9, 13:
                index = 0; // p1
                break;

            case 2, 6, 10:
                index = 1; // p2
                break;

            case 3, 7, 11:
                index = 2; // p3
                break;

            case 4, 8, 12:
                index = 3; // p4
                break;
        }
        return index;
    }

    public void newGame() {
        Random rand = new Random();

        numOfplayersPlayed = 0;
        trickWinnerId = -1;

        center.clear();
        deck.clear();
        discardedCards.clear();

        String[] suits = { "s", "c", "d", "h" };
        String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };

        while (deck.size() < 52) { // generate 52 unique card
            String newCardname = suits[rand.nextInt(4)] + ranks[rand.nextInt(13)];
            Card c = new Card(newCardname);
            if (!deck.contains(c)) {
                deck.add(c);
            }
        }

        center.add(deck.get(0)); // place first card in deck at center
        deck.remove(0);

        currentPlayerIndex = getFirstPlayerIndex(); // decide first player

        // deal 7 cards to all players
        for (int i = 0; i < players.length; i++) {
            players[i].clearPlayerCards();
            for (int j = 0; j < 7; j++) {
                players[i].drawCard(deck.get(j)); // draw top card from deck
                deck.remove(j); // remove card from deck
            }
        }
    }

    public void resetGame() {
        for (Player p : players) { // reset player score
            p.setScore(0);
        }
        trick = 1;
        round = 1;
    }

    public void playerDiscardCard(GoBoom game, String cardName) {
        Card c = new Card(cardName);
        if (players[currentPlayerIndex].getPlayerCards().contains(c)) { // player has said card

            if (center.size() == 0) { // player place new lead card if center empty
                players[currentPlayerIndex].setDiscardedCard(c);
                players[currentPlayerIndex].discardCard(c);
                center.add(c);

                currentPlayerTurn = false; // player turn finished

            } else if (c.getSuit() == center.get(0).getSuit()
                    || c.getRank() == center.get(0).getRank()) { // card is same suit OR rank as lead
                players[currentPlayerIndex].setDiscardedCard(c);
                players[currentPlayerIndex].discardCard(c);
                center.add(c);

                currentPlayerTurn = false; // player turn finished

            } else {
                System.out.println("*Card does not match suit or rank of lead card\n");
                setResultText("*Card does not match suit or rank of lead card");
                updateplayerText(game);
            }

        } else { // for debugging purposes
            System.out.println("*Player does not have the card\n");
            setResultText("*Player does not have the card");
            updateplayerText(game);
        }
    }

    public void switchNextPlayer() {
        if (currentPlayerIndex + 1 < 4) {
            currentPlayerIndex++;
        } else {
            currentPlayerIndex = 0;
        }
    }

    public void getTrickWinner(GoBoom game) {
        // The highest-rank card with the same suit as the lead card wins the trick.
        Card bigCard = center.get(0);
        for (Player p : players) {
            if (p.getDiscardedCard().getSuit() == bigCard.getSuit()
                    && p.getDiscardedCard().getRank() > bigCard.getRank()) {
                bigCard = p.getDiscardedCard();
                trickWinnerId = p.getId();
            }
        }

        if (trickWinnerId > -1) { // someone wins the trick
            System.out.println("*** Player" + trickWinnerId + " wins Trick #" + trick + " ***\n");
            setResultText("*** Player" + trickWinnerId + " wins Trick #" + trick + " ***");
            currentPlayerIndex = trickWinnerId - 1; // player leads next trick
            center.clear();

        } else { // no one wins the trick(the game goes on with the house who is temporary player
                 // wins)
            System.out.println("*** No one wins Trick" + trick + " ***\n");
            setResultText("*** No one wins Trick" + trick + " ***");

            for (Card i : center) {
                discardedCards.add(i);
            }
            center.clear();

            if (deck.size() == 0) { // refill deck if deck empty
                deck = discardedCards;
            }
            center.add(deck.get(0)); // place first card in deck at center
            deck.remove(0);
            currentPlayerIndex = getFirstPlayerIndex(); // first player decided by first trick lead card
        }
        numOfplayersPlayed = 0;
    }

    public void playerDrawCard() {
        if (center.size() > 0) {
            HashSet<Integer> playerSuits = new HashSet<>();
            HashSet<Integer> playerRanks = new HashSet<>();

            for (Card i : players[currentPlayerIndex].getPlayerCards()) { // get what suits and ranks player have
                if (playerSuits.size() > 4 && playerRanks.size() > 13) {
                    break;
                } else {
                    playerSuits.add(i.getSuit());
                    playerRanks.add(i.getRank());
                }
            }

            while (!playerSuits.contains(center.get(0).getSuit())
                    && !playerRanks.contains(center.get(0).getRank())) { // draw cards until player has playable card
                if (deck.size() > 0) { // deck not empty
                    players[currentPlayerIndex].drawCard(deck.get(0));
                    playerSuits.add(deck.get(0).getSuit());
                    playerRanks.add(deck.get(0).getRank());
                    deck.remove(0);
                } else { // deck empty
                    currentPlayerTurn = false; // skip turn
                    break;
                }
            }
        }
    }

    public void calculatePlayersScore() {
        int score = 0;
        for (Player p : players) {
            score = 0;
            if (p.getPlayerCards().size() > 0) {
                for (int i = 0; i < p.getPlayerCards().size(); i++) {
                    p.setScore(score + p.getPlayerCards().get(i).getValue());
                    score = p.getScore();
                }
            }
        }
    }

    public void saveGame(String fileName) {
        try {
            if (trick > 0) {
                FileWriter w = new FileWriter(fileName); // auto creates file

                // GoBoom's data
                w.write(Integer.toString(players.length)); // num of players to put players into list
                w.write("\n");

                for (Card c : deck) { // deck
                    w.write(c.getName() + ",");
                }
                w.write("\n");

                for (Card c : center) { // center
                    w.write(c.getName() + ",");
                }
                w.write("\n");

                for (Card c : discardedCards) { // discardedCards
                    w.write(c.getName() + ",");
                }
                w.write("\n");

                w.write(Integer.toString(trick) + "\n");
                w.write(Integer.toString(round) + "\n");
                w.write(Integer.toString(numOfplayersPlayed) + "\n");
                w.write(Integer.toString(currentPlayerIndex) + "\n");

                // player's data
                for (Player p : players) { // score
                    w.write(p.getScore() + ",");
                }
                w.write("\n");

                for (Player p : players) { // discardedCard
                    if (p.getDiscardedCard() == null) {
                        w.write(" ,");
                    } else {
                        w.write(p.getDiscardedCard().getName() + ",");
                    }
                }
                w.write("\n");

                for (Player p : players) { // playerCards
                    w.write(p.getPlayerCards() + "");
                }
                w.write("\n");

                w.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCards(LinkedList<Card> cards, String fileName, int lineNum) {
        try {
            cards.clear();
            String data = Files.readAllLines(Paths.get(fileName)).get(lineNum);
            String cardName = "";
            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == ',') {
                    Card c = new Card(cardName);
                    cards.add(c);
                    cardName = "";
                } else {
                    cardName = cardName + data.charAt(i);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void loadGame(String fileName) {
        int lineNum = 0; // The line number max 12
        try {
            // GoBoom's data
            String data = Files.readAllLines(Paths.get(fileName)).get(lineNum); // players
            players = new Player[Integer.parseInt(data)]; // clear array
            for (int i = 0; i < Integer.parseInt(data); i++) {
                Player p = new Player(i + 1);
                players[i] = p;
            }
            lineNum++;

            loadCards(deck, fileName, lineNum);
            lineNum++;

            loadCards(center, fileName, lineNum);
            lineNum++;

            loadCards(discardedCards, fileName, lineNum);
            lineNum++;

            data = Files.readAllLines(Paths.get(fileName)).get(lineNum);
            trick = Integer.parseInt(data);
            lineNum++;

            data = Files.readAllLines(Paths.get(fileName)).get(lineNum);
            round = Integer.parseInt(data);
            lineNum++;

            data = Files.readAllLines(Paths.get(fileName)).get(lineNum);
            numOfplayersPlayed = Integer.parseInt(data);
            lineNum++;

            data = Files.readAllLines(Paths.get(fileName)).get(lineNum);
            currentPlayerIndex = Integer.parseInt(data);
            lineNum++;

            // player's data
            data = Files.readAllLines(Paths.get(fileName)).get(lineNum); // player score
            int k = 0;
            for (Player p : players) {
                String playerScore = "";
                while (k < data.length()) {
                    if (data.charAt(k) == ',') {
                        p.setScore(Integer.parseInt(playerScore));
                        k++;
                        break;
                    } else {
                        playerScore = playerScore + data.charAt(k);
                    }
                    k++;
                }
            }
            lineNum++;

            data = Files.readAllLines(Paths.get(fileName)).get(lineNum); // player discarded card
            k = 0;
            for (Player p : players) {
                String cardName = "";
                while (k < data.length()) {
                    if (data.charAt(k) == ',') {
                        break;
                    } else {
                        cardName = cardName + data.charAt(k);
                        k++;
                    }
                }
                if (!cardName.contains(" ")) {
                    Card c = new Card(cardName);
                    p.setDiscardedCard(c);
                }
                k++;
            }
            lineNum++;

            data = Files.readAllLines(Paths.get(fileName)).get(lineNum);
            data = data.replace(" ", "");
            String temp = "";

            for (Player p : players) { // get player cards
                p.clearPlayerCards();
                String cards = data.substring(data.indexOf("[") + 1, data.indexOf("]"));

                for (int i = 0; i < cards.length(); i++) { // get cards
                    if (cards.charAt(i) == ',') {
                        Card c = new Card(temp);
                        p.drawCard(c);
                        temp = "";
                    } else {
                        temp = temp + cards.charAt(i);
                    }
                }
                Card c = new Card(temp);
                p.drawCard(c);
                data = data.replace("[" + cards + "]", "");
                temp = "";
            }

        } catch (IOException e) {
            System.out.println("*Save file does not exists\n");
            setResultText("*Save file does not exists");
        } catch (Exception e) {
            System.out.println("*Corrupted save file\n");
            setResultText("*Corrupted save file");
        }
    }
}
