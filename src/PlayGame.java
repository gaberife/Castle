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
    Group humanCastle = new Group();
    Group computerCastle = new Group();
    Player PLAYER = new Player();
    Computer COMPUTER = new Computer();
    ArrayList<Card> DISCARD = new ArrayList<Card>();
    String phase = "Deal";
    Random rand = new Random();

    EventHandler<MouseEvent> CLICK = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent CLICK) {
            if (phase == "Player Turn" || phase == "Seen") {
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
            } else {
                instructions = new Text("IT IS NOT YOUR TURN");
                vBoxHandler(instructions);
            }
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
                case "Flip":
                    flip();
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
                    instructions = new Text("SOMETHING IS WRONG");
                    vBoxHandler(instructions);
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
        if (instructions != null) {
            instructions = null;
            vBoxHandler(instructions);
        }
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

            instructions = new Text("PRESS ENTER TO INITIALIZE CASTLE");
            vBoxHandler(instructions);
        }
        phase = "Unseen";
    }

    public void flip() {
        if (!PLAYER.UNSEEN_CASTLE.isEmpty()) {
            PLAYER.HAND.forEach(Card::flipCard);
            phase = "Seen";
            instructions = new Text("PRESS ENTER TO INITIALIZE SEEN CASTLE");
            vBoxHandler(instructions);
        }
    }

    public void initCastle() {
        if (phase == "Unseen") {
            if (PLAYER.UNSEEN_CASTLE.isEmpty())
                PLAYER.initCastle();
            if (COMPUTER.UNSEEN_CASTLE.isEmpty())
                COMPUTER.initCastle();
            if (!PLAYER.UNSEEN_CASTLE.isEmpty() && !COMPUTER.UNSEEN_CASTLE.isEmpty()) {
                phase = "Flip";
                instructions = new Text("PRESS ENTER TO FLIP OVER YOUR CARDS");
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
        System.out.println("decideStart()");

        if (DISCARD.isEmpty()) {
            int computerMIN = COMPUTER.smallest().getRank();
            PLAYER.selectedCard = PLAYER.smallest();
            int playerMIN = PLAYER.selectedCard.getRank();

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
                    instructions = new Text("COMPUTER GOES FIRST: " + COMPUTER.smallest().returnCard());
                    vBoxHandler(instructions);
                    COMPUTER.smallest().flipCard();
                    COMPUTER.smallest().setCardPos(590, 285);
                    System.out.println("Computer Array has: " + COMPUTER.HAND.size() + " cards. Computer Group has: " + computerHand.getChildren().size()
                            + "\nPlayer Array has: " + PLAYER.HAND.size() + " cards. Player Group has: " + playerHand.getChildren().size()+ "\n");
                    DISCARD.add(COMPUTER.smallest());
                    computerHand.getChildren().remove(COMPUTER.smallest());
                    discard.getChildren().add(COMPUTER.smallest());
                    COMPUTER.HAND.remove(COMPUTER.smallest());
                    COMPUTER.HAND.add(deck.dealCard());
                    computerHand.getChildren().add(COMPUTER.HAND.get(3));
                    for (int index = 0; index < COMPUTER.HAND.size(); index++)
                        COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 48);
                    phase = "Player Turn";
                    break;
                case "Player":
                    instructions = new Text("PLAYER GOES FIRST: " + PLAYER.selectedCard.returnCard());
                    vBoxHandler(instructions);
                    PLAYER.selectedCard.setCardPos(590, 285);
                    System.out.println("Computer Array has: " + COMPUTER.HAND.size() + " cards. Computer Group has: " + computerHand.getChildren().size()
                            + "\nPlayer Array has: " + PLAYER.HAND.size() + " cards. Player Group has: " + playerHand.getChildren().size()+ "\n");
                    DISCARD.add(PLAYER.selectedCard);
                    playerHand.getChildren().remove(PLAYER.selectedCard);
                    discard.getChildren().add(PLAYER.selectedCard);
                    PLAYER.HAND.remove(PLAYER.selectedCard);
                    PLAYER.HAND.add(deck.dealCard());
                    playerHand.getChildren().add(PLAYER.HAND.get(3));
                    PLAYER.HAND.get(3).flipCard();
                    for (int index = 0; index < PLAYER.HAND.size(); index++)
                        PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 522);
                    phase = "Computer Turn";
                    break;
                default:
                    System.out.println("This is broken");
                    break;
            }
        }
        returnLastDiscard();
    }

    public int returnLastDiscard() {
        System.out.println("returnLastDiscard()");
        System.out.println("Computer Array has: " + COMPUTER.HAND.size() + " cards. Computer Group has: " + computerHand.getChildren().size()
                + "\nPlayer Array has: " + PLAYER.HAND.size() + " cards. Player Group has: " + playerHand.getChildren().size()+ "\n");
        int lastDiscard;
        if(DISCARD.isEmpty())
            lastDiscard = 0;
        else
            lastDiscard = DISCARD.get(DISCARD.size() - 1).getRank();

        System.out.println("Discard Array has: " + DISCARD.size() + " cards. Discard Group has: " + discard.getChildren().size() + "\nDiscard Array contains the following cards: ");
        for(int i = 0; i < DISCARD.size(); i++){
            System.out.print(DISCARD.get(i).returnCard() + " in discard group = ");
            if (discard.getChildren().contains(DISCARD.get(i)))
                System.out.println("TRUE");
            else
                System.out.println("FALSE");
        }
        System.out.println();
        return lastDiscard;
    }

    public void computerTurn() {
        System.out.println("computerTurn()");
        System.out.println("Computer Array has: " + COMPUTER.HAND.size() + " cards. Computer Group has: " + computerHand.getChildren().size()
                + "\nPlayer Array has: " + PLAYER.HAND.size() + " cards. Player Group has: " + playerHand.getChildren().size()+ "\n");
        instructions = new Text("COMPUTER DISCARDED " + COMPUTER.bestPlay().returnCard());
        vBoxHandler(instructions);
        COMPUTER.smallest().flipCard();
        COMPUTER.smallest().setCardPos(590, 285);
        DISCARD.add(COMPUTER.bestPlay());
        computerHand.getChildren().remove(COMPUTER.bestPlay());
        discard.getChildren().add(COMPUTER.bestPlay());
        COMPUTER.HAND.remove(COMPUTER.bestPlay());
        COMPUTER.bestPlay().toFront();
        COMPUTER.HAND.add(deck.dealCard());
        computerHand.getChildren().add(COMPUTER.HAND.get(3));
        for (int index = 0; index < COMPUTER.HAND.size(); index++) {
            COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 48);
            COMPUTER.HAND.get(index).toFront();
        }

        phase = "Player Turn";

        returnLastDiscard();
        System.out.println("Selected card is: " + COMPUTER.bestPlay().returnCard() + "\nComputer Array contains the following cards: ");
        for(int i = 0; i < COMPUTER.HAND.size(); i++) {
            System.out.print(COMPUTER.HAND.get(i).returnCard() + " in computer group = ");
            if (computerHand.getChildren().contains(COMPUTER.HAND.get(i)))
                System.out.println("TRUE");
            else
                System.out.println("FALSE");

        }
    }

    public void playerTurn() {
        System.out.println("playerTurn()");
        System.out.println("Computer Array has: " + COMPUTER.HAND.size() + " cards. Computer Group has: " + computerHand.getChildren().size()
                + "\nPlayer Array has: " + PLAYER.HAND.size() + " cards. Player Group has: " + playerHand.getChildren().size()+ "\n");

        if (returnLastDiscard() < PLAYER.selectedCard.getRank() || returnLastDiscard() == PLAYER.selectedCard.getRank() ||
                 PLAYER.selectedCard.getRank() == 10 || PLAYER.selectedCard.getRank() == 2) {
            PLAYER.selectedCard.setCardPos(590, 285);
            DISCARD.add(PLAYER.selectedCard);
            PLAYER.HAND.remove(PLAYER.selectedCard);
            playerHand.getChildren().remove(PLAYER.selectedCard);
            discard.getChildren().add(PLAYER.selectedCard);
            PLAYER.selectedCard.toFront();

            if (PLAYER.selectedCard.getRank() == 2) {
                instructions = new Text("DISCARD AGAIN");
                vBoxHandler(instructions);
                phase = "Player Turn";
            }
            else{
                if(PLAYER.selectedCard.getRank() == 10) {
                    instructions = new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard());
                    vBoxHandler(instructions);
                    discard.getChildren().removeAll(DISCARD);
                    DISCARD.clear();
                }
                else{
                    instructions = new Text("PLAYER DISCARDED " + PLAYER.selectedCard.returnCard());
                    vBoxHandler(instructions);
                }
                phase = "Computer Turn";
            }
            PLAYER.HAND.add(deck.dealCard());
            playerHand.getChildren().add(PLAYER.HAND.get(PLAYER.HAND.size()-1));
            PLAYER.HAND.get(3).flipCard();
            for (int index = 0; index < PLAYER.HAND.size(); index++)
                PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 522);
        }
        else if (returnLastDiscard() > PLAYER.selectedCard.getRank()) {
            instructions = new Text("THAT IS NOT A VALID DISCARD");
            vBoxHandler(instructions);
            boolean playableCards = false;
            ArrayList<Integer> temp  = new ArrayList<Integer>();
            for(int i = returnLastDiscard(); i < 15; i++)
                temp.add(i);
            for(Card card : PLAYER.HAND) {
                if(!temp.contains(card.getRank()))
                    playableCards = false;
                else
                    playableCards = true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!playableCards) {
                instructions = new Text("PAYER DOES NOT HAVE ANY PLAYABLE CARDS.");
                vBoxHandler(instructions);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                instructions = new Text("PLAYER MUST PICK UP THE DISCARD PILE.");
                vBoxHandler(instructions);

                PLAYER.HAND.addAll(DISCARD);
                discard.getChildren().removeAll(DISCARD);
                playerHand.getChildren().addAll(DISCARD);
                DISCARD.removeAll(DISCARD);
                for (int index = 0; index < PLAYER.HAND.size(); index++)
                    PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 522);

                phase = "Computer Turn";
            }
            else{
                instructions = new Text("PLEASE SELECT A DIFFERENT CARD");
                vBoxHandler(instructions);
            }
        }

        returnLastDiscard();
        System.out.println("Player selected card is: " + PLAYER.selectedCard.returnCard() + "\nPlayer Array contains the following cards: ");
        for(int i = 0; i < PLAYER.HAND.size(); i++) {
            System.out.print(PLAYER.HAND.get(i).returnCard() + " in player group = ");
            if (playerHand.getChildren().contains(PLAYER.HAND.get(i)))
                System.out.println("TRUE");
            else
                System.out.println("FALSE");
        }
    }

    public boolean finished() {
        if (deck.size() < 0) {
            System.out.println("Deal Pile is depleted, Game Finished");
            System.exit(0);
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