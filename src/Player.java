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
    boolean flipped = false;

    public int indexOfSmallest() {
        int index = 0;
        int min = HAND.get(index).getRank();
        for (int i = 0; i < HAND.size(); i++) {
            if (HAND.get(i).getRank() <= min) {
                min = HAND.get(i).getRank();
                index = i;
            }
        }
        return index;
    }

    public boolean checkBounds(double x, double y) {
        for (int i = 0; i < HAND.size(); i++) {
            if (x > HAND.get(i).getCardPosX() && x < HAND.get(i).getCardPosX() + 100 && y > HAND.get(i).getCardPosY() && y < HAND.get(i).getCardPosY() + 150)
                return true;
        }
        return false;
    }

    public Card getCard(double x, double y) {
        Card selectedCard = null;
        for (int i = 0; i < HAND.size(); i++) {
            if (x > HAND.get(i).getCardPosX() && x < HAND.get(i).getCardPosX() + 100 && y > HAND.get(i).getCardPosY() && y < HAND.get(i).getCardPosY() + 150)
                selectedCard = HAND.get(i);
        }
        return selectedCard;
    }

    public boolean alreadySelected() {
        if (SELECTED.contains(selectedCard))
            return true;
        return false;
    }


    public void initCastle() {
        if (UNSEEN_CASTLE.isEmpty()) {
            if (HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(HAND.size());
                    HAND.get(n).setBounds(900 + (Card.WIDTH + 100) * i, 600);
                    UNSEEN_CASTLE.add(HAND.get(n));
                    HAND.remove(n);
                }
            }
            for (int index = 0; index < 7; index++)
                HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
        }
        if (SEEN_CASTLE.isEmpty() && !UNSEEN_CASTLE.isEmpty()){
            if (SELECTED.size() == 3) {
                for (int i = 0; i < SELECTED.size(); i++) {
                    HAND.remove(SELECTED.get(i));
                    SELECTED.get(i).setBounds(900 + (Card.WIDTH + 100) * i, 560);
                    SEEN_CASTLE.add(SELECTED.get(i));
                }
            }
            SELECTED.removeAll(SELECTED);
            for (int index = 0; index < 4; index++)
                HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
        }
    }


    public void playSelectedCards(ArrayList cards){

    }

    public void setBounds(double x, double y) {
       selectedCard.setCardPos(x,y);
    }
}
