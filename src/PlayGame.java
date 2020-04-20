import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.* ;
import javafx.scene.input.* ;
import java.io.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.*;

public class PlayGame extends Application {
    CardDeck deck;
    StackPane root = new StackPane();
    Scene scene = new Scene(root, 1280, 720);
    Text instructions = new Text("PRESS ENTER TO DEAL CARDS");
    VBox vbox = new VBox(instructions);
    Group dealPile = new Group();
    Group computerHand = new Group();
    Group playerHand = new Group();
    Group discard = new Group();
    Player PLAYER = new Player();
    Computer COMPUTER = new Computer();
    ArrayList<Card> DISCARD = new ArrayList<Card>();
    String phase = "Deal";
    Random rand = new Random();

    EventHandler<MouseEvent> CLICK = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent CLICK) {
            if (phase == "Player Turn" || phase == "Seen") {
                PLAYER.selectedCard = PLAYER.getCard(CLICK.getSceneX(), CLICK.getSceneY());
                if (PLAYER.checkBounds(CLICK.getSceneX(), CLICK.getSceneY())) {
                    if (!PLAYER.alreadySelected()) {
                        PLAYER.SELECTED.add(PLAYER.selectedCard);
                        PLAYER.selectedCard.setY(PLAYER.selectedCard.getCardPosY() - 50);
                        if(!PLAYER.selectedCard.isFaceUp())
                            PLAYER.selectedCard.flipCard();
                    }
                    else {
                        PLAYER.SELECTED.remove(PLAYER.selectedCard);
                        PLAYER.selectedCard.setY(PLAYER.selectedCard.getCardPosY() + 50);
                    }
                }
            }
            else
                vBoxHandler(new Text("IT IS NOT YOUR TURN"));
        }
    }; //Initializes CLICK Mouse Event

    EventHandler<KeyEvent> ENTER = new EventHandler<KeyEvent>() {
        public void handle(KeyEvent ENTER) {
            switch (phase) {
                case "Deal":
                    deal();
                    break;
                case "Unseen":
                case "Seen":
                    initCastle();
                    break;
                case "Start":
                    decideStart();
                    break;
                case "Computer Turn":
                    computerTurn();
                    finished();
                    break;
                case "Player Turn":
                    playerTurn();
                    finished();
                    break;
                default:
                    vBoxHandler( new Text("SOMETHING IS WRONG"));
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
            dealPile.setLayoutX(1140);
            dealPile.setLayoutY(285);
            dealPile.getChildren().add(deck.getCard(index));
        }
    }

    public void start(Stage stage) throws FileNotFoundException {
        stage.setTitle("The Game Of Castle");
        initDeck(); //Initializes Deck

        Group mainCardGroup = new Group();
        mainCardGroup.setManaged(false);
        mainCardGroup.getChildren().addAll(playerHand, computerHand, dealPile, discard);

        instructions.setFont(new Font(24));
        vbox.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(mainCardGroup, vbox);
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
        if (instructions != null)
            vBoxHandler(null);

        if (PLAYER.HAND.isEmpty() && COMPUTER.HAND.isEmpty()) {
            //Allocates 10 Cards to Each player
            for (int index = 0; index < 10; index++) {
                Card newCard = deck.dealCard();
                double CPHandPosX = 40 + (Card.WIDTH + 20) * index;
                double CPHandPosY = 48;
                newCard.setCardPos(CPHandPosX, CPHandPosY);
                COMPUTER.HAND.add(newCard);
                computerHand.getChildren().add(COMPUTER.HAND.get(index));
                dealPile.getChildren().remove(COMPUTER.HAND.get(index));

                newCard = deck.dealCard();
                double HPHandPosX = 40 + (Card.WIDTH + 20) * index;
                double HPHandPosY = 522;
                newCard.setCardPos(HPHandPosX, HPHandPosY);
                PLAYER.HAND.add(newCard);
                playerHand.getChildren().add(PLAYER.HAND.get(index));
                dealPile.getChildren().remove(PLAYER.HAND.get(index));
            }
            vBoxHandler(new Text("PRESS ENTER TO INITIALIZE CASTLE"));
        }
        phase = "Unseen";
    }

    public void initCastle() {
        if (phase == "Unseen") {
            if (PLAYER.UNSEEN_CASTLE.isEmpty())
                PLAYER.initCastle();
            if (COMPUTER.UNSEEN_CASTLE.isEmpty())
                COMPUTER.initCastle();
            if (!PLAYER.UNSEEN_CASTLE.isEmpty() && !COMPUTER.UNSEEN_CASTLE.isEmpty()) {
                phase = "Seen";
                instructions = new Text("SELECT 3 CARDS AND PRESS ENTER TO FINISH INITIALIZING CASTLE");
                vBoxHandler(instructions);
            }
        }
        if (phase == "Seen") {
            if (PLAYER.SEEN_CASTLE.isEmpty())
                PLAYER.initCastle();
            if (COMPUTER.SEEN_CASTLE.isEmpty())
                COMPUTER.initCastle();
            if (!PLAYER.SEEN_CASTLE.isEmpty() && !COMPUTER.SEEN_CASTLE.isEmpty()) {
                phase = "Start";
                instructions = new Text("PRESS ENTER TO DETERMINE WHICH PLAYER GOES FIRST");
                vBoxHandler(instructions);
            }
        }
    }

    public void decideStart() {
        if (DISCARD.isEmpty()) {
            Card CPCard = COMPUTER.smallest();
            int computerMIN = CPCard.getRank();
            Card PLCard = PLAYER.smallest();
            int playerMIN = PLCard.getRank();

            String turn = "null";
            if (computerMIN < playerMIN)
                turn = "Computer";
            else if (computerMIN > playerMIN)
                turn = "Player";
            else {
                int n = rand.nextInt(1);
                if (n == 0)
                    turn = "Player";
                else if (n == 1)
                    turn = "Computer";
            }
            switch (turn) {
                case "Computer":
                    vBoxHandler(new Text("COMPUTER GOES FIRST: " + COMPUTER.smallest().returnCard()));
                    phase = "Player Turn";
                    play(COMPUTER.HAND, computerHand, CPCard);
                    dealCard(COMPUTER.HAND,computerHand);
                    COMPUTER.orderHand();
                    break;
                case "Player":
                    vBoxHandler(new Text("PLAYER GOES FIRST: " + PLAYER.selectedCard.returnCard()));
                    phase = "Computer Turn";
                    play(PLAYER.HAND, playerHand, PLCard);
                    dealCard(PLAYER.HAND,playerHand);
                    PLAYER.orderHand();
                    break;
                default:
                    System.out.println("This is broken");
                    break;
            }
        }
        returnLastDiscard();
    }

    public Card returnLastDiscard() {
        Card lastDiscard;
        if(DISCARD.isEmpty())
            lastDiscard = null;
        else
            lastDiscard = DISCARD.get(DISCARD.size() - 1);
        return lastDiscard;
    }

    public void computerTurn() {
        Card card = COMPUTER.bestPlay(returnLastDiscard());
        ArrayList<Card> selectedCards = new ArrayList<Card>();
        selectedCards = COMPUTER.SELECTED;

        if(COMPUTER.HAND.size() != 0) {
            if (selectedCards.size() == 1) {
                if (card != null) {
                    play(COMPUTER.HAND, computerHand, card);
                    if (card.getRank() == 2) {
                        vBoxHandler(new Text("COMPUTER DISCARD AGAIN"));
                        phase = "Computer Turn";
                    } else if (card.getRank() == 10) {
                        vBoxHandler(new Text("COMPUTER CLEARED THE DISCARD PILE WITH " + card.returnCard()));
                        discard.getChildren().removeAll(DISCARD);
                        DISCARD.clear();
                        phase = "Player Turn";
                    } else {
                        vBoxHandler(new Text("COMPUTER DISCARDED " + card.returnCard()));
                        phase = "Player Turn";
                    }
                } else if (card == null) {
                    if (!checkForValidDiscard(COMPUTER.HAND)) {
                        returnToHand(COMPUTER.HAND, computerHand);
                        COMPUTER.orderHand();
                        phase = "Player Turn";
                    } else {
                        vBoxHandler(new Text("PLEASE SELECT A DIFFERENT CARD"));
                        phase = "Computer Turn";
                    }
                }
            }
            dealCard( COMPUTER.HAND, computerHand);
            COMPUTER.SELECTED.clear();
            COMPUTER.orderHand();
        }else {
            if (COMPUTER.SEEN_CASTLE.size() != 0) {
                if (DISCARD.isEmpty() || returnLastDiscard().getRank() < card.getRank() || returnLastDiscard().getRank() == card.getRank() ||
                        card.getRank() == 10 || card.getRank() == 2) {
                    play(COMPUTER.SEEN_CASTLE, computerHand, card);
                    if (card.getRank() == 2) {
                        vBoxHandler(new Text("COMPUTER DISCARD AGAIN"));
                        phase = "Computer Turn";
                    } else if (card.getRank() == 10) {
                        vBoxHandler(new Text("COMPUTER DISCARDED " + card.returnCard()));
                        discard.getChildren().removeAll(DISCARD);
                        DISCARD.clear();
                        phase = "Player Turn";
                    } else {
                        vBoxHandler(new Text("PLAYER DISCARDED " + card.returnCard()));
                        phase = "Player Turn";
                    }
                } else if (returnLastDiscard().getRank() > card.getRank()) {
                    if (!checkForValidDiscard(COMPUTER.SEEN_CASTLE)) {
                        returnToHand(COMPUTER.HAND, computerHand);
                        COMPUTER.orderHand();
                        phase = "Player Turn";
                    }
                }
            } else {
                if (COMPUTER.UNSEEN_CASTLE.size() != 0) {
                    if (DISCARD.isEmpty() || returnLastDiscard().getRank() < card.getRank() || returnLastDiscard().getRank() == card.getRank() ||
                            card.getRank() == 10 || card.getRank() == 2) {

                        play(COMPUTER.UNSEEN_CASTLE,  computerHand,  card);
                        if (card.getRank() == 2) {
                            vBoxHandler(new Text("COMPUTER DISCARD AGAIN"));
                            phase = "Computer Turn";
                        } else if (card.getRank() == 10) {
                            vBoxHandler(new Text("PLAYER DISCARDED " + card.returnCard()));
                            discard.getChildren().removeAll(DISCARD);
                            DISCARD.clear();
                            phase = "Player Turn";
                        } else {
                            vBoxHandler(new Text("COMPUTER DISCARDED " + card.returnCard()));
                            phase = "Player Turn";
                        }
                    } else if (returnLastDiscard().getRank() > card.getRank()) {
                        COMPUTER.HAND.add(card);
                        COMPUTER.HAND.get(COMPUTER.HAND.size() - 1).flipCard();
                        returnToHand(COMPUTER.HAND, computerHand);
                        COMPUTER.orderHand();
                        phase = "Player Turn";
                    }
                }
            }
        }
        COMPUTER.orderHand();
    }

    public void playerTurn() {
        if (PLAYER.HAND.size() != 0) {
            if (PLAYER.SELECTED.size() == 1) {
                if (DISCARD.isEmpty() || returnLastDiscard().getRank() < PLAYER.selectedCard.getRank() || returnLastDiscard().getRank() == PLAYER.selectedCard.getRank() ||
                        PLAYER.selectedCard.getRank() == 10 || PLAYER.selectedCard.getRank() == 2) {

                    play(PLAYER.HAND,  playerHand,  PLAYER.selectedCard);
                    if (PLAYER.selectedCard.getRank() == 2) {
                        vBoxHandler(new Text("PLAYER DISCARD AGAIN"));
                        phase = "Player Turn";
                    } else if (PLAYER.selectedCard.getRank() == 10) {
                        vBoxHandler(new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard()));
                        discard.getChildren().removeAll(DISCARD);
                        DISCARD.clear();
                        phase = "Computer Turn";
                    } else {
                        vBoxHandler(new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard()));
                        phase = "Computer Turn";
                    }
                } else if (returnLastDiscard().getRank() > PLAYER.selectedCard.getRank()) {
                    if (!checkForValidDiscard(PLAYER.HAND)) {
                        returnToHand(PLAYER.HAND, playerHand);
                        PLAYER.orderHand();
                        phase = "Computer Turn";
                    }
                }
            } else if (PLAYER.SELECTED.size() > 1) {
                if (PLAYER.checkSame(PLAYER.SELECTED)) {
                    if (DISCARD.isEmpty() || returnLastDiscard().getRank() < PLAYER.selectedCard.getRank() || returnLastDiscard().getRank() == PLAYER.selectedCard.getRank() ||
                            PLAYER.selectedCard.getRank() == 10 || PLAYER.selectedCard.getRank() == 2) {

                        PLAYER.SELECTED.forEach(card -> card.setCardPos(590, 285));
                        DISCARD.addAll(PLAYER.SELECTED);
                        PLAYER.HAND.removeAll(PLAYER.SELECTED);
                        playerHand.getChildren().removeAll(PLAYER.SELECTED);
                        discard.getChildren().addAll(PLAYER.SELECTED);
                        PLAYER.SELECTED.forEach((card -> card.toFront()));

                        if (PLAYER.selectedCard.getRank() == 2) {
                            vBoxHandler(new Text("PLAYER DISCARD AGAIN"));
                            phase = "Player Turn";
                        } else if (PLAYER.selectedCard.getRank() == 10) {
                            discard.getChildren().removeAll(DISCARD);
                            DISCARD.clear();
                            vBoxHandler(new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard()));
                            phase = "Computer Turn";
                        } else {
                            vBoxHandler(new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard()));
                            phase = "Computer Turn";
                        }
                        dealCard(PLAYER.HAND, playerHand);
                    } else if (returnLastDiscard().getRank() > PLAYER.selectedCard.getRank()) {
                        if (!checkForValidDiscard(PLAYER.HAND)) {
                            returnToHand(PLAYER.HAND, playerHand);
                            PLAYER.orderHand();
                            phase = "Computer Turn";
                        } else {
                            vBoxHandler(new Text("PLEASE SELECT A DIFFERENT CARD"));
                            phase = "Player Turn";
                        }
                    }
                } else {
                    vBoxHandler(new Text("THAT IS AN INVALID SELECTION, TRY AGAIN"));
                    phase = "Player Turn";
                }
            }
            dealCard( PLAYER.HAND, playerHand);
            PLAYER.orderHand();
        } else {
            if (PLAYER.SEEN_CASTLE.size() != 0) {
                if (DISCARD.isEmpty() || returnLastDiscard().getRank() < PLAYER.selectedCard.getRank() || returnLastDiscard().getRank() == PLAYER.selectedCard.getRank() ||
                        PLAYER.selectedCard.getRank() == 10 || PLAYER.selectedCard.getRank() == 2) {

                    play(PLAYER.SEEN_CASTLE,  playerHand,  PLAYER.selectedCard);
                    if (PLAYER.selectedCard.getRank() == 2) {
                        vBoxHandler(new Text("PLAYER DISCARD AGAIN"));
                        phase = "Player Turn";
                    } else if (PLAYER.selectedCard.getRank() == 10) {
                        vBoxHandler(new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard()));
                        discard.getChildren().removeAll(DISCARD);
                        DISCARD.clear();
                        phase = "Computer Turn";
                    } else {
                        vBoxHandler(new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard()));
                        phase = "Computer Turn";
                    }
                } else if (returnLastDiscard().getRank() > PLAYER.selectedCard.getRank()) {
                    if (!checkForValidDiscard(PLAYER.SEEN_CASTLE)) {
                        returnToHand(PLAYER.HAND, playerHand);
                        PLAYER.orderHand();
                        phase = "Computer Turn";
                    }
                }
            } else {
                if (PLAYER.UNSEEN_CASTLE.size() != 0) {
                    if (DISCARD.isEmpty() || returnLastDiscard().getRank() < PLAYER.selectedCard.getRank() || returnLastDiscard().getRank() == PLAYER.selectedCard.getRank() ||
                            PLAYER.selectedCard.getRank() == 10 || PLAYER.selectedCard.getRank() == 2) {

                        play(PLAYER.UNSEEN_CASTLE,  playerHand,  PLAYER.selectedCard);
                            if (PLAYER.selectedCard.getRank() == 2) {
                            vBoxHandler(new Text("PLAYER DISCARD AGAIN"));
                            phase = "Player Turn";
                        } else if (PLAYER.selectedCard.getRank() == 10) {
                            vBoxHandler(new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard()));
                            discard.getChildren().removeAll(DISCARD);
                            DISCARD.clear();
                            phase = "Computer Turn";
                        } else {
                            vBoxHandler(new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard()));
                            phase = "Computer Turn";
                        }
                    } else if (returnLastDiscard().getRank() > PLAYER.selectedCard.getRank()) {
                        PLAYER.HAND.add(PLAYER.selectedCard);
                        PLAYER.HAND.get(PLAYER.HAND.size() - 1).flipCard();
                        returnToHand(PLAYER.HAND, playerHand);
                        PLAYER.orderHand();
                        phase = "Computer Turn";
                    }
                }
            }
        }
        PLAYER.orderHand();
    }

    public void play(ArrayList<Card> HAND, Group Hand, Card card){
        card.setCardPos(590, 285);
        DISCARD.add(card);
        HAND.remove(card);
        Hand.getChildren().remove(card);
        discard.getChildren().add(card);
        card.toFront();
        if(!card.isFaceUp())
            card.flipCard();
    }

    public void returnToHand(ArrayList<Card> HAND, Group Hand){
        HAND.addAll(DISCARD);
        discard.getChildren().removeAll(DISCARD);
        Hand.getChildren().addAll(DISCARD);
        DISCARD.removeAll(DISCARD);
    }

    public void dealCard(ArrayList<Card> HAND, Group Hand) {
            while (HAND.size() < 4 && checkDealPile()) {
                HAND.add(deck.dealCard());
                Hand.getChildren().add(HAND.get(HAND.size() - 1));
                if(phase == "Computer Turn") {
                    HAND.get(HAND.size() - 1).flipCard();
            }
        }
    }

    public boolean checkDealPile() {
        if (deck.size() != 0)
           return true;
        return false;
    }

    public boolean checkForValidDiscard(ArrayList<Card> HAND) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int i = returnLastDiscard().getRank(); i < 15; i++)
            temp.add(i);
        if (!temp.contains(2))
            temp.add(2);
        if (!temp.contains(10))
            temp.add(10);
        for (Card card : HAND) {
            if (temp.contains(card.getRank())) {
                vBoxHandler(new Text("THAT IS NOT A VALID DISCARD, TRY AGAIN"));
                return true;
            }
        }
        if (phase == "Computer Turn")
            vBoxHandler(new Text("COMPUTER DOES NOT HAVE PLAYABLE CARDS, COMPUTER MUST PICK UP THE DISCARD PILE"));
        if (phase == "Player Turn")
            vBoxHandler(new Text("PLAYER DOES NOT HAVE PLAYABLE CARDS, PLAYER MUST PICK UP THE DISCARD PILE"));
        return false;
    }

    public boolean finished() {
        if(playerHand.getChildren().isEmpty()) {
            vBoxHandler(new Text("CONGRATULATIONS, YOU'VE WON"));
            return true;
        }
        else if(computerHand.getChildren().isEmpty()){
            vBoxHandler(new Text("SORRY, YOU'VE LOST"));
            return true;
        }
        return false;
    }

    public void vBoxHandler(Text text){
        if(text != null){
            text.setFont(new Font(24));
            root.getChildren().remove(vbox);
            vbox = new VBox(text);
            vbox.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().add(vbox);
        }
        else if (root.getChildren().contains(vbox))
            root.getChildren().remove(vbox);
    }

    public static void main(String args[] ){
        launch(args);
    }
}