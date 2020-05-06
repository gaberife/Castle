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

    public Card smallest(ArrayList<Card> hand) {
        Card card = null;
        Card min = hand.get(0);
        for (Card temp : hand) {
            if (temp.rank <= min.rank && temp.rank != 2) {
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
            if (temp.rank >= max.rank) {
                max = temp;
                card = max;
            }
        }
        return card;
    }

    public boolean containsRank(ArrayList<Card> givenHand, int givenRank) {
        return givenHand.stream().filter(o -> o.rank == (givenRank)).findFirst().isPresent();
    }

    public Card containsRangeReturn(ArrayList<Card> givenHand, ArrayList<Integer> givenRange) {
        for (Card temp : givenHand) {
            for (int i : givenRange) {
                if (temp.rank == i) {
                    return temp;
                }
            }
        }
        return null;
    }

    public Card containsRankReturn(ArrayList<Card> givenHand, int givenRank) {
        for (Card temp : givenHand) {
            if (temp.rank == givenRank) {
                return temp;
            }
        }
        return null;
    }

    public Card bestPlay(ArrayList<Card> hand, Card card) {
        if (card == null) {
            selectedCard = smallest(hand);
            System.out.println("Smallest");
        } else {
            if (containsRank(hand, card.rank)) {
                selectedCard = containsRankReturn(hand, card.rank);
                System.out.println("Equal to");
            } else {
                ArrayList<Integer> temp = new ArrayList<Integer>();
                for (int i = card.rank + 1; i < 15; i++)
                    temp.add(i);
                if (containsRangeReturn(hand, temp) != null) {
                    selectedCard = containsRangeReturn(hand, temp);
                    System.out.println("In Range");
                } else if (containsRank(hand, 2)) {
                    selectedCard = containsRankReturn(hand, 2);
                    System.out.println("Two");
                } else if (containsRank(hand, 10)) {
                    selectedCard = containsRankReturn(hand, 10);
                    System.out.println("Ten");
                } else {
                    selectedCard = null;
                    System.out.println("NULL");
                }
            }
        }
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
        int n = rand.nextInt(UNSEEN_CASTLE.size());
        Card randomCard = UNSEEN_CASTLE.get(n);
        SELECTED.add(randomCard);
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

    public boolean checkSame(ArrayList<Card> temp) {
        int first = temp.get(0).rank;
        for (int i = 1; i < temp.size(); i++)
            if (temp.get(0).rank != first)
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
        } else if (SEEN_CASTLE.isEmpty() && !UNSEEN_CASTLE.isEmpty()) {
            if (HAND.size() != 3) {
                while (SEEN_CASTLE.size() != 3) {
                    Card temp;
                    if (containsRank(HAND, 2))
                        temp = containsRankReturn(HAND, 2);
                    else if (containsRank(HAND, 10))
                        temp = containsRankReturn(HAND, 10);
                    else if (containsRank(HAND, 14))
                        temp = containsRankReturn(HAND, 14);
                    else
                        temp = largest();
                    SEEN_CASTLE.add(temp);
                    HAND.remove(temp);
                }
                for (int i = 0; i < 3; i++) {
                    SEEN_CASTLE.get(i).flipCard();
                    SEEN_CASTLE.get(i).toFront();
                    SEEN_CASTLE.get(i).setCardPos(900 + (Card.WIDTH + 100) * i, 74);
                }
            }
            orderHand();
        }
    }

    public void orderHand() {
        for (int index = 0; index < HAND.size(); index++) {
            HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 48);
            HAND.get(index).toFront();
            //System.out.println(HAND.get(index).returnCard());
            if (HAND.get(index).isFaceUp())
                HAND.get(index).flipCard();
        }
        System.out.println();
    }
}
