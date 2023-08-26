public class Card {
    String name;
    private int suit;
    private int rank;
    private int value;

    public Card(String name) {
        this.name = name;

        char suitName = name.charAt(0);
        String rankName = name.substring(1);

        // suit
        switch (suitName) {
            case 's': // spades
                this.suit = 1;
                break;
            case 'c': // clubs
                this.suit = 2;
                break;
            case 'd': // diamonds
                this.suit = 3;
                break;
            case 'h': // hearts
                this.suit = 4;
                break;
        }

        // rank
        switch (rankName) {
            case "A":
                this.rank = 14;
                break;
            case "K":
                this.rank = 13;
                break;
            case "Q":
                this.rank = 12;
                break;
            case "J":
                this.rank = 11;
                break;
            default:
                this.rank = Integer.parseInt(rankName);
        }

        // value
        if (rank == 14) { // Ace
            value = 1;
        } else if (rank > 10) { // K J Q
            value = 10;
        } else { // 2 to 10
            value = rank;
        }
    }

    public Card() {
        this("sA");
    }

    public String getName() {
        return name;
    }

    public int getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card c = (Card) o;
            return this.name.equals(c.name);
        }
        return false;
    }
}
