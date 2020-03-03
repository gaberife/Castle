import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CardDeck {
    static int[] suits = {Card.HEARTS, Card.DIAMONDS, Card.SPADES, Card.CLUBS}; //ToDo: include Card.JOKER
    ArrayList<Card> cardDeck = new ArrayList<Card>();

    public CardDeck() {
        for (int suitIndex = 0; suitIndex < 4; suitIndex++) {
            for (int cardRank = 1; cardRank < 14; cardRank++)
                addCard(new Card(cardRank, suits[suitIndex]));
        }
    }

    public void shuffle() {
        Collections.shuffle(cardDeck);
    }

    public void addCard(Card card) {
        if (cardDeck.size() < 52) { //TODO: IMPLEMENT JOKER PLAYABILITY
            cardDeck.add(card);
        }
    }

    public Card getCard(int n) {
        return cardDeck.get(n);
    }

    public Card dealCard() {
        Card cardToReturn = null;
        if (cardDeck.size() > 0) {
            cardToReturn = cardDeck.remove(cardDeck.size() - 1);
        }
        return cardToReturn;
    }

    public int size() {
        return cardDeck.size();
    }
}