import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.* ;
import javafx.scene.input.* ;
import javafx.scene.Node;
import java.io.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayGame extends Application {
    CardDeck deck;

    /*TODO: Implement computer hand, player hand, most recent discard and full discard pile*/
    StackPane root = new StackPane();
    Text instructions = new Text("PRESS ENTER TO DEAL CARDS");
    Group dealPile = new Group();
    Group computerHand = new Group();
    Group playerHand = new Group();
    Group playerCastle = new Group();
    Node selected;
    Group computerCastle = new Group();
    Player PLAYER = new Player();
    Computer COMPUTER = new Computer();
    ArrayList<Card> DISCARD = new ArrayList<Card>();
    String phase = "deal";
    Random rand = new Random();
    boolean turn = false;

    EventHandler<MouseEvent> CLICK = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent CLICK) {
            double clickX = CLICK.getSceneX();
            double clickY = CLICK.getSceneY();

            PLAYER.selectedCard = PLAYER.getCard(clickX, clickY);
            if (PLAYER.HAND != null && PLAYER.checkBounds(clickX, clickY)) {
                if (!PLAYER.alreadySelected()) {
                    PLAYER.SELECTED.add(PLAYER.selectedCard);
                    PLAYER.selectedCard.setY(PLAYER.selectedCard.getCardPosY() - 50);
                } else {
                    PLAYER.SELECTED.remove(PLAYER.selectedCard);
                    PLAYER.selectedCard.setY(PLAYER.selectedCard.getCardPosY() + 50);
                }
            }
        }
    }; //Initializes CLICK Mouse Event

    EventHandler<KeyEvent> ENTER = new EventHandler<KeyEvent>() {
        public void handle(KeyEvent ENTER) {
            switch (phase) {
                case "deal":
                    deal();
                    break;
                case "init unseen castle":
                    initUnseenCastle();
                    break;
                case "init seen castle":
                    initSeenCastle();
                    break;
                case "flip":
                    flip();
                    break;
                case "start":
                    decideStart();
                    break;
                case "computers turn":
                    computerTurn();
                    finished();
                    break;
                case "players turn":
                    playerTurn();
                    finished();
                    break;
                default:
                    System.out.println("Game Over");
                    break;
            }
        }
    };

    public void initDeck() throws FileNotFoundException { //Initializes the Deck
        //Reads in the image file for the back of the card and creates a faceDown Images object
        Images.faceDown = new Image(new FileInputStream(
                "C:\\Users\\Queen\\IdeaProjects\\Castle\\cardBackArrows.png"),
                100, 150, false, false);
        //Reads in the image files for the front face of the card deck and creates 52 faceUp image objects
        //TODO: Solve the blank face card issue
        Images.faceUp = new HashMap<String, Image>();
        String[] wordsInFilePNGNames = {"hearts", "diamonds", "spades", "clubs"};
        for (int suitIndex = 0; suitIndex < 4; suitIndex++) {
            for (int cardRank = 2; cardRank < 15; cardRank++) {
                //NOTE: Files that contain card face images have formatted as such "2 of Hearts.png"
                String PNGFileName = "C:\\Users\\Queen\\IdeaProjects\\Castle\\Playing Card PNGs\\" + cardRank + " of " + wordsInFilePNGNames[suitIndex] + ".png";
                Image faceUpCards = new Image(new FileInputStream(PNGFileName), 100, 150, false, false);
                String cardNames = wordsInFilePNGNames[suitIndex] + cardRank;
                Images.faceUp.put(cardNames, faceUpCards);
            }
        }
        //Initializes the deck and the Deal Pile
        deck = new CardDeck();
        deck.shuffle();
        for (int index = 0; index < deck.size(); index++) {
            dealPile.setLayoutX(1100);
            dealPile.setLayoutY(285);
            dealPile.getChildren().add(deck.getCard(index));
        }
    }

    public void start(Stage stage) throws FileNotFoundException {
        stage.setTitle("The Game Of Castle");
        initDeck(); //Initializes Deck

        Group mainCardGroup = new Group();
        mainCardGroup.setManaged(false);
        mainCardGroup.getChildren().addAll(playerHand, computerHand, dealPile);

        instructions.setFont(new Font(24));
        root.getChildren().addAll(mainCardGroup, instructions);
        Scene scene = new Scene(root, 1280, 720);

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, CLICK);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, ENTER);

        //Reads background image in
        Images.background = new Image(new FileInputStream("C:\\Users\\Queen\\IdeaProjects\\Castle\\Green Background.jpg"));
        ImagePattern ImagePattern = new ImagePattern(Images.background);
        root.setBackground(null);
        scene.setFill(ImagePattern);
        stage.setScene(scene);
        stage.show();
    }

    public void deal() {
        if (instructions != null) {
            root.getChildren().remove(instructions);
            instructions = null;
        }
        if (PLAYER.HAND.isEmpty() && COMPUTER.HAND.isEmpty()) {
            //Allocates 10 Cards to Each player
            for (int index = 0; index < 10; index++) {
                Card newCard = deck.dealCard();
                double CPHandPosX = 40 + (Card.WIDTH + 20) * index;
                double CPHandPosY = 100;
                newCard.setCardPos(CPHandPosX, CPHandPosY);
                COMPUTER.HAND.add(newCard);
                computerHand.getChildren().add(COMPUTER.HAND.get(index));
                dealPile.getChildren().remove(COMPUTER.HAND.get(index));

                newCard = deck.dealCard();
                double HPHandPosX = 40 + (Card.WIDTH + 20) * index;
                double HPHandPosY = 500;
                newCard.setCardPos(HPHandPosX, HPHandPosY);
                PLAYER.HAND.add(newCard);
                playerHand.getChildren().add(PLAYER.HAND.get(index));
                dealPile.getChildren().remove(PLAYER.HAND.get(index));
            }
            System.out.println("After dealing 10 cards to each player, there are " + deck.size() + " cards remaining in the deck.");

            System.out.println("There are " + PLAYER.HAND.size() + " cards in player hand.");
            System.out.println("There are " + COMPUTER.HAND.size() + " cards in computer hand.");
        }
        phase = "init unseen castle";
    }

    public void flip() {
        if (!PLAYER.UNSEEN_CASTLE.isEmpty()) {
            PLAYER.HAND.forEach(Card::flipCard);
            phase = "init seen castle";
        }
    }

    public void initUnseenCastle() {
        if (PLAYER.UNSEEN_CASTLE.isEmpty() && COMPUTER.UNSEEN_CASTLE.isEmpty()) {
            if (PLAYER.HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(PLAYER.HAND.size());
                    PLAYER.HAND.get(n).setBounds(900 + (Card.WIDTH + 100) * i, 600);
                    PLAYER.UNSEEN_CASTLE.add(PLAYER.HAND.get(n));
                    PLAYER.HAND.remove(n);
                }
            }
            if (COMPUTER.HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(COMPUTER.HAND.size());
                    COMPUTER.HAND.get(n).setBounds(900 + (Card.WIDTH + 100) * i, 100);
                    COMPUTER.UNSEEN_CASTLE.add(COMPUTER.HAND.get(n));
                    COMPUTER.HAND.remove(n);
                }
            }
        }
        for (int index = 0; index < 7; index++) {
            COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 100);
            PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
        }
        phase = "flip";
    }

    public void initSeenCastle() {
        if (PLAYER.SEEN_CASTLE.isEmpty() && COMPUTER.SEEN_CASTLE.isEmpty()) {
            if (PLAYER.SELECTED.size() == 3) {
                for (int i = 0; i < PLAYER.SELECTED.size(); i++) {
                    if(playerHand.getChildren().equals(PLAYER.SELECTED.get(i)))
                        playerHand.getChildren().get(i).toFront();
                    PLAYER.HAND.remove(PLAYER.SELECTED.get(i));
                    PLAYER.SELECTED.get(i).setBounds(900 + (Card.WIDTH + 100) * i, 560);
                    PLAYER.SEEN_CASTLE.add(PLAYER.SELECTED.get(i));
                }
            }
            PLAYER.SELECTED.removeAll(PLAYER.SELECTED);
            if (COMPUTER.HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(COMPUTER.HAND.size());
                    computerHand.getChildren().get(n).toFront();
                    COMPUTER.HAND.get(n).flipCard();
                    COMPUTER.HAND.get(n).setBounds(900 + (Card.WIDTH + 100) * i, 140);
                    COMPUTER.UNSEEN_CASTLE.add(COMPUTER.HAND.get(n));
                    COMPUTER.HAND.remove(n);
                }
            }
        }
        for (int index = 0; index < 4; index++) {
            COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 100);
            PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
        }
        phase = "start";
    }

    public void decideStart() {
        if (DISCARD.isEmpty()) {
            if (COMPUTER.indexOfSmallest() > COMPUTER.indexOfSmallest()) {
                COMPUTER.HAND.get(COMPUTER.indexOfSmallest()).flipCard();
                COMPUTER.HAND.get(COMPUTER.indexOfSmallest()).setCardPos(640, 360);
                DISCARD.add(COMPUTER.HAND.get(COMPUTER.indexOfSmallest()));
                COMPUTER.HAND.remove(COMPUTER.indexOfSmallest());
                COMPUTER.HAND.add(deck.dealCard());
                playerHand.getChildren().add(COMPUTER.HAND.get(3));
                for (int index = 0; index < COMPUTER.HAND.size(); index++)
                    COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 100);
                phase = "players turn";
            } else if (COMPUTER.indexOfSmallest() < PLAYER.indexOfSmallest()) {
                PLAYER.HAND.get(PLAYER.indexOfSmallest()).setCardPos(640, 360);
                DISCARD.add(PLAYER.HAND.get(PLAYER.indexOfSmallest()));
                PLAYER.HAND.remove(PLAYER.indexOfSmallest());
                PLAYER.HAND.add(deck.dealCard());
                playerHand.getChildren().add(PLAYER.HAND.get(3));
                PLAYER.HAND.get(3).flipCard();
                for (int index = 0; index < PLAYER.HAND.size(); index++)
                    PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
                phase = "computers turn";
            } else if (COMPUTER.indexOfSmallest() == PLAYER.indexOfSmallest()) {
                int n = rand.nextInt(2);
                if (n == 1) {
                    COMPUTER.HAND.get(COMPUTER.indexOfSmallest()).flipCard();
                    COMPUTER.HAND.get(COMPUTER.indexOfSmallest()).setCardPos(640, 360);
                    DISCARD.add(COMPUTER.HAND.get(COMPUTER.indexOfSmallest()));
                    COMPUTER.HAND.remove(COMPUTER.indexOfSmallest());
                    COMPUTER.HAND.add(deck.dealCard());
                    playerHand.getChildren().add(COMPUTER.HAND.get(3));
                    for (int index = 0; index < COMPUTER.HAND.size(); index++)
                        COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 100);
                    phase = "players turn";
                } else if (n == 2) {
                    PLAYER.HAND.get(PLAYER.indexOfSmallest()).setCardPos(640, 360);
                    DISCARD.add(PLAYER.HAND.get(PLAYER.indexOfSmallest()));
                    PLAYER.HAND.remove(PLAYER.indexOfSmallest());
                    PLAYER.HAND.add(deck.dealCard());
                    playerHand.getChildren().add(PLAYER.HAND.get(3));
                    PLAYER.HAND.get(3).flipCard();
                    for (int index = 0; index < PLAYER.HAND.size(); index++)
                        PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
                    phase = "computers turn";
                }
            }
        }
    }

    public void computerTurn() {
        COMPUTER.HAND.get(COMPUTER.indexOfSmallest()).flipCard();
        COMPUTER.HAND.get(COMPUTER.indexOfSmallest()).setCardPos(640, 360);
        System.out.println("Computer Discarded " + COMPUTER.HAND.get(COMPUTER.indexOfSmallest()).returnRank() + COMPUTER.HAND.get(COMPUTER.indexOfSmallest()).returnSuit());
        DISCARD.add(COMPUTER.HAND.get(COMPUTER.indexOfSmallest()));
        COMPUTER.HAND.remove(COMPUTER.indexOfSmallest());
        COMPUTER.HAND.add(deck.dealCard());
        playerHand.getChildren().add(COMPUTER.HAND.get(3));
        for (int index = 0; index < COMPUTER.HAND.size(); index++)
            COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 100);
        phase = "players turn";
    }

    public void playerTurn() {
        PLAYER.selectedCard.setCardPos(640, 360);
        DISCARD.add(PLAYER.selectedCard);
        System.out.println("Player Discarded " + PLAYER.selectedCard.returnRank() + PLAYER.selectedCard.returnSuit());
        PLAYER.HAND.remove(PLAYER.selectedCard);
        PLAYER.HAND.add(deck.dealCard());
        playerHand.getChildren().add(PLAYER.HAND.get(3));
        PLAYER.HAND.get(3).flipCard();
        for (int index = 0; index < PLAYER.HAND.size(); index++)
            PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
        phase = "computers turn";
    }

    public boolean finished() {
        if (deck.size() < 0) {
            System.out.println("Deal Pile is depleted, Game Finished");
            System.exit(0);
            return true;
        }
        return false;
    }

    public void bringCardToFront(Group hand, Card card, Group newGroup) {
        for (Node n: hand.getChildren()){
            if(n.equals(card)){
                System.out.println("test");
                newGroup.getChildren().add(n);
                hand.getChildren().remove(n);
                //n.toFront();
            }
        }
    }

    public static void main(String args[] ){
        launch(args);
    }
}