import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.Random;

public class Player extends ImageView {
    ArrayList<Card> HAND = new ArrayList<Card>();
    Card selectedCard;
    ArrayList<Card> SELECTED = new ArrayList<Card>();
    ArrayList<Card> UNSEEN_CASTLE = new ArrayList<Card>();
    ArrayList<Card> SEEN_CASTLE = new ArrayList<Card>();
    Random rand = new Random();

    public Card smallest() {
        Card card = null;
        Card min = HAND.get(0);
        for (Card temp : HAND) {
            if (temp.getRank() <= min.getRank() && temp.getRank() != 2) {
                min = temp;
                card = min;
            }
        }
        return card;
    }

    public boolean checkBounds(double x, double y) {
        if (!HAND.isEmpty()) {
            for (Card card : HAND) {
                if (x > card.getCardPosX() && x < card.getCardPosX() + 100 && y > card.getCardPosY() && y < card.getCardPosY() + 150)
                    return true;
            }
        }
        else if (HAND.isEmpty() && !SEEN_CASTLE.isEmpty()) {
                for (Card card : SEEN_CASTLE) {
                    if (x > card.getCardPosX() && x < card.getCardPosX() + 100 && y > card.getCardPosY() && y < card.getCardPosY() + 150)
                        return true;
                }
            }
        else if (HAND.isEmpty() && SEEN_CASTLE.isEmpty() && !SEEN_CASTLE.isEmpty()) {
                if (UNSEEN_CASTLE != null) {
                    for (Card card : UNSEEN_CASTLE) {
                        if (x > card.getCardPosX() && x < card.getCardPosX() + 100 && y > card.getCardPosY() && y < card.getCardPosY() + 150)
                            return true;
                    }
                }
            }
        return false;
    }

    public Card getCard(double x, double y) {
        Card selectedCard = null;
        for (Card card : HAND) {
            if (x > card.getCardPosX() && x < card.getCardPosX() + 100 && y > card.getCardPosY() && y < card.getCardPosY() + 150)
                selectedCard = card;
        }
        for (Card card : SEEN_CASTLE) {
            if (x > card.getCardPosX() && x < card.getCardPosX() + 100 && y > card.getCardPosY() && y < card.getCardPosY() + 150)
                selectedCard = card;
        }
        for (Card card : UNSEEN_CASTLE) {
            if (x > card.getCardPosX() && x < card.getCardPosX() + 100 && y > card.getCardPosY() && y < card.getCardPosY() + 150)
                selectedCard = card;
        }
        return selectedCard;
    }

    public boolean alreadySelected() {
        if (SELECTED.contains(selectedCard))
            return true;
        return false;
    }

    public  boolean checkSame(ArrayList<Card> temp) {
        int first = temp.get(0).getRank();
        for (int i = 1; i < temp.size(); i++)
            if (temp.get(0).getRank() != first)
                return false;
        return true;
    }

    public void orderHand(){
        for (int index = 0; index < HAND.size(); index++) {
            HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 522);
            if (!HAND.get(index).isFaceUp())
                HAND.get(index).flipCard();
        }
        SELECTED.clear();
    }

    public void initCastle() {
        if (UNSEEN_CASTLE.isEmpty()) {
            if (HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(HAND.size());
                    HAND.get(n).setCardPos(900 + (Card.WIDTH + 100) * i, 546);
                    HAND.get(n).toFront();
                    UNSEEN_CASTLE.add(HAND.get(n));
                    HAND.remove(n);
                }
            }
            orderHand();
        }
        if (SEEN_CASTLE.isEmpty() && !UNSEEN_CASTLE.isEmpty()){
            if (SELECTED.size() == 3) {
                for (int i = 0; i < SELECTED.size(); i++) {
                    HAND.remove(SELECTED.get(i));
                    SELECTED.get(i).setCardPos(900 + (Card.WIDTH + 100) * i, 496);
                    SELECTED.get(i).toFront();
                    SEEN_CASTLE.add(SELECTED.get(i));
                }
            }
            SELECTED.removeAll(SELECTED);
            orderHand();
        }
    }
}
