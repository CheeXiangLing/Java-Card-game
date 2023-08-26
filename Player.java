import java.util.ArrayList;

public class Player {
    private int id;
    private int score;
    private Card discardedCard;
    private ArrayList<Card> playerCards;

    public Player(int id) {
        this.id = id;
        this.score = 0;
        this.playerCards = new ArrayList<>();
    }

    public Player() {
        this(0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Card getDiscardedCard(){
        return discardedCard;
    }

    public void setDiscardedCard(Card c){
        this.discardedCard = c;
    }

    public ArrayList<Card> getPlayerCards() {
        return playerCards;
    }

    public void clearPlayerCards() {
        playerCards.clear();
    }

    public void drawCard(Card newCard) {
        playerCards.add(newCard);
    }

    public void discardCard(Card c) {
        playerCards.remove(c);
    }

    @Override
    public String toString() {
        return "Player" + id + ": " + playerCards.toString();
    }
}
