import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;

public class Player extends ImageView {
    ArrayList<Card>  HAND  =  new ArrayList<Card>();
    Card selectedCard;
    ArrayList<Card> SELECTED = new ArrayList<Card>();
    ArrayList<Card>  UNSEEN_CASTLE  =  new ArrayList<Card>();
    ArrayList<Card>  SEEN_CASTLE  =  new ArrayList<Card>();
    boolean flipped = false;

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

    public boolean alreadySelected(){
        if(SELECTED.contains(selectedCard))
            return true;
        return false;
    }

    public void setBounds(double x, double y) {
       selectedCard.setCardPos(x,y);
    }
}
