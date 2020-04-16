import javafx.scene.image.ImageView;
import java.util.Random;
import java.util.ArrayList;

public class Computer extends ImageView {
    ArrayList<Card> HAND = new ArrayList<Card>();
    Card selectedCard;
    ArrayList<Card> UNSEEN_CASTLE = new ArrayList<Card>();
    ArrayList<Card> SEEN_CASTLE = new ArrayList<Card>();
    Random rand = new Random();


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

    public int indexOfBestPlay() {
        for (int i = 0; i < HAND.size(); i++) {
            if (HAND.get(i).getRank() == 2)
                return i;
            else if (HAND.get(i).getRank() == 10)
                return i;

        }
        return indexOfSmallest();
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



    public void initCastle() {
        if (UNSEEN_CASTLE.isEmpty()) {
            if (HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(HAND.size());
                    HAND.get(n).setCardPos(900 + (Card.WIDTH + 100) * i, 24);
                    UNSEEN_CASTLE.add(HAND.get(n));
                    HAND.remove(n);
                }
            }
            for (int index = 0; index < 7; index++)
                HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 48);
        }
        else if (SEEN_CASTLE.isEmpty() && !UNSEEN_CASTLE.isEmpty()) {
            if (HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(HAND.size());
                    HAND.get(n).flipCard();
                    HAND.get(n).setCardPos(900 + (Card.WIDTH + 100) * i, 74);
                    SEEN_CASTLE.add(HAND.get(n));
                    HAND.remove(n);
                }
            }
            for (int index = 0; index < 4; index++)
                HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 48);
        }
    }
}
