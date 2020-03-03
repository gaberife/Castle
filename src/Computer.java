import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Collections;
public class Computer extends ImageView {
    Image cardFaceUp;
    ArrayList<Card>  HAND  =  new ArrayList<Card>();
    ArrayList<Card>  UNSEEN_CASTLE  =  new ArrayList<Card>();
    ArrayList<Card>  SEEN_CASTLE  =  new ArrayList<Card>();

    public boolean checkBounds(double x, double y) {
        for (int i = 0; i < HAND.size(); i++) {
            if (x > HAND.get(i).getCardPosX() && x < HAND.get(i).getCardPosX() + 100 && y > HAND.get(i).getCardPosY() && y < HAND.get(i).getCardPosY() + 150)
                return true;
        }
        return false;
    }

    public Card getCard(double x, double y) {
        Card card = null;
        for (int i = 0; i < HAND.size(); i++)
            if (x > HAND.get(i).getCardPosX() && x < HAND.get(i).getCardPosX() + 100 && y > HAND.get(i).getCardPosY() && y < HAND.get(i).getCardPosY() + 150)
                card = HAND.get(i);
        return card;
    }

    public void setBounds(double x, double y){
        double cardPosX;
        double cardPosY;
        for (int i = 0; i < HAND.size(); i++) {
            if (x > HAND.get(i).getCardPosX() && x < HAND.get(i).getCardPosX() + 100 && y > HAND.get(i).getCardPosY() && y < HAND.get(i).getCardPosY() + 150) {
                HAND.get(i).setCardPos(x - 50,y - 75);
            }
        }
    }

}
