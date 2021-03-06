import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Card extends ImageView {
    int rank;
    int suit;

    double xPos;
    double yPos;

    Image CardFaceUp;
    public static final int HEARTS = 1;
    public static final int DIAMONDS = 2;
    public static final int SPADES = 3;
    public static final int CLUBS = 4;

    public static final int WIDTH = 5;
    public static final int HEIGHT = 10;

    public Card(int givenRank, int givenSuit) {
        rank = givenRank;
        suit = givenSuit;
        xPos = getX();
        yPos = getY();

        if (suit == HEARTS)
            CardFaceUp = Images.faceUp.get("hearts" + rank);
        else if (suit == DIAMONDS)
            CardFaceUp = Images.faceUp.get("diamonds" + rank);
        else if (suit == SPADES)
            CardFaceUp = Images.faceUp.get("spades" + rank);
        else if (suit == CLUBS)
            CardFaceUp = Images.faceUp.get("clubs" + rank);
        setImage(Images.faceDown);
    }

    public String returnCard() {
        String ranking = null;
        switch (rank) {
            case 2:
                ranking = "2 OF";
                break;
            case 3:
                ranking = "3 OF";
                break;
            case 4:
                ranking = "4 OF";
                break;
            case 5:
                ranking = "5 OF";
                break;
            case 6:
                ranking = "6 OF";
                break;
            case 7:
                ranking = "7 OF";
                break;
            case 8:
                ranking = "8 OF";
                break;
            case 9:
                ranking = "9 OF";
                break;
            case 10:
                ranking = "10 of";
                break;
            case 11:
                ranking = "JACK OF";
                break;
            case 12:
                ranking = "QUEEN OF";
                break;
            case 13:
                ranking = "KING OF";
                break;
            case 14:
                ranking = "ACE OF";
                break;
            default:
                System.out.print(rank);
                break;
        }
        //String ranking = null;
        switch(suit){
            case 1:
                ranking = ranking + " HEARTS";
                break;
            case 2:
                ranking = ranking + " DIAMONDS";
                break;
            case 3:
                ranking = ranking + " SPADES";
                break;
            case 4:
                ranking = ranking + " CLUBS";
                break;
            default:
                ranking = ranking + " This isn't working.";
                break;
        }
        return ranking;
    }

    //Returns rank
    public int getRank(){
        return rank;
    }

    //Flips cards
    public void flipCard(){
        if(getImage() == CardFaceUp)
            setImage(Images.faceDown);
        else
            setImage(CardFaceUp);
    }

    //Returns if card is face up
    public boolean isFaceUp(){
        return(getImage() == CardFaceUp);
    }

    //Sets the cards position on the board
    public void setCardPos(double x, double y){
        setX(x);
        setY(y);
    }

    //Returns Cards X Position
    public double getCardPosX(){
        return getX();
    }

    //Returns Cards Y Position
    public double getCardPosY(){
        return getY();
    }

    public boolean getBounds(Card card, double x, double y){
        if(x > card.getCardPosX() && x < card.getCardPosX() + 100 && y > card.getCardPosY() && y < card.getCardPosY() + 150)
            return true;
        else
            return false;
    }

    public void setBounds(double x, double y){
        setX(x-50);
        setY(y-75);
    }

        //Returns suit Name
    public String getSuitString(){
        String  string  =  "";
        switch(suit){
            case  HEARTS:
                string  =  "Hearts";
                break;
            case  DIAMONDS:
                string  =  "Diamonds";
                break;
            case  SPADES:
                string  =  "Spades";
                break;
            case  CLUBS:
                string  =  "Clubs";
                break;
            default:
                string  =  "Program error!!!";
        }
        return  string ;
    }


}