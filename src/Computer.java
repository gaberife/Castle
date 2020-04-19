import javafx.scene.image.ImageView;
import java.util.Random;
import java.util.ArrayList;

public class Computer extends ImageView {
    ArrayList<Card> HAND = new ArrayList<Card>();
    Card selectedCard;
    ArrayList<Card> UNSEEN_CASTLE = new ArrayList<Card>();
    ArrayList<Card> SEEN_CASTLE = new ArrayList<Card>();
    Random rand = new Random();
    ArrayList<Card> SELECTED = new ArrayList<Card>();


    public Card smallest() {
        Card card = null;
        Card min = HAND.get(0);
        for (Card temp : HAND) {
            if (temp.getRank() <= min.getRank()) {
                min = temp;
                card = min;
            }
        }
        return card;
    }
    public Card largest() {
        Card card = null;
        Card max = HAND.get(0);
        for (Card temp : HAND) {
            if (temp.getRank() >= max.getRank()) {
                max = temp;
                card = max;
            }
        }
        return card;
    }
    public Card bestPlay() {
        /*
        int RANK = card.getRank();
        for(Card temp : HAND) {
            if (HAND.contains(temp.getRank() > RANK))
                selectedCard = temp;
        }
         */
        selectedCard = largest();
        SELECTED.add(selectedCard);
        return selectedCard;
    }

    public boolean checkBounds(double x, double y) {
        for (int i = 0; i < HAND.size(); i++) {
            if (x > HAND.get(i).getCardPosX() && x < HAND.get(i).getCardPosX() + 100 && y > HAND.get(i).getCardPosY() && y < HAND.get(i).getCardPosY() + 150)
                return true;
        }
        return false;
    }

    public Card getRandomCard() {
        Random rand = new Random();
        int n = rand.nextInt(HAND.size());
        Card randomCard = HAND.get(n);
        return randomCard;
    }

    public void setBounds(double x, double y) {
        double cardPosX;
        double cardPosY;
        for (int i = 0; i < HAND.size(); i++) {
            if (x > HAND.get(i).getCardPosX() && x < HAND.get(i).getCardPosX() + 100 && y > HAND.get(i).getCardPosY() && y < HAND.get(i).getCardPosY() + 150) {
                HAND.get(i).setCardPos(x - 50, y - 75);
            }
        }
    }

    public  boolean checkSame(ArrayList<Card> temp) {
        int first = temp.get(0).getRank();
        for (int i = 1; i < temp.size(); i++)
            if (temp.get(0).getRank() != first)
                return false;
        return true;
    }

    public void initCastle() {
        if (UNSEEN_CASTLE.isEmpty()) {
            if (HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(HAND.size());
                    HAND.get(n).setCardPos(900 + (Card.WIDTH + 100) * i, 24);
                    HAND.get(n).toFront();
                    UNSEEN_CASTLE.add(HAND.get(n));
                    HAND.remove(n);
                }
            }
            for (int index = 0; index < 7; index++)
                HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 48);
        }
        else if (SEEN_CASTLE.isEmpty() && !UNSEEN_CASTLE.isEmpty()) {
            if (HAND.size() != 3) {
                if(SEEN_CASTLE.size() != 3){
                    for(int i = 0; i < HAND.size()-1; i++) {
                        Card temp = HAND.get(i);
                        if (temp.getRank() == 2) {
                            SEEN_CASTLE.add(temp);
                            HAND.remove(temp);
                        }
                        else if (temp.getRank() == 10) {
                            SEEN_CASTLE.add(temp);
                            HAND.remove(temp);
                        }
                        else if (temp.getRank() > 11) {
                            SEEN_CASTLE.add(temp);
                            HAND.remove(temp);
                        }
                        else {
                            int n = rand.nextInt(HAND.size());
                            SEEN_CASTLE.add(HAND.get(n));
                            HAND.remove(n);
                        }
                    }
                }
                for (int i = 0; i < 3; i++){
                    SEEN_CASTLE.get(i).flipCard();
                    SEEN_CASTLE.get(i).toFront();
                    SEEN_CASTLE.get(i).setCardPos(900 + (Card.WIDTH + 100) * i, 74);
                }

            }
            orderHand();
        }
    }

    public void orderHand(){
        for (int index = 0; index < HAND.size(); index++) {
            HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 48);
            HAND.get(index).toFront();
            if (HAND.get(index).isFaceUp())
                HAND.get(index).flipCard();
        }
    }
}
