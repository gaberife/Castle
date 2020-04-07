import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.* ;
import javafx.scene.input.* ;
import javafx.geometry.* ;
import java.io.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.*;

public class PlayGame extends Application{
    CardDeck deck;

    /*TODO: Implement computer hand, player hand, most recent discard and full discard pile*/
    StackPane root = new StackPane();
    Text instructions = new Text( "PRESS ENTER TO DEAL CARDS" );
    Group dealPile = new Group();
    Group computerHand = new Group();
    Group playerHand = new Group();
    Player PLAYER = new Player();
    Computer COMPUTER = new Computer();
    ArrayList<Card>  DISCARD  =  new ArrayList<Card>();
    String phase = "deal";
    Random rand = new Random();
    boolean turn = false;

    EventHandler<MouseEvent> CLICK = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent CLICK) {
            PLAYER.selectedCard = PLAYER.getCard(CLICK.getSceneX(),CLICK.getSceneY());
            if (PLAYER.HAND != null && PLAYER.checkBounds(CLICK.getSceneX(), CLICK.getSceneY())){
                if(!PLAYER.alreadySelected()) {
                    PLAYER.SELECTED.add(PLAYER.selectedCard);
                    PLAYER.selectedCard.setY(PLAYER.selectedCard.getCardPosY() - 50);
                }
                else {
                    PLAYER.SELECTED.remove(PLAYER.selectedCard);
                    PLAYER.selectedCard.setY(PLAYER.selectedCard.getCardPosY() + 50);
                }
            }
        }
    }; //Initializes CLICK Mouse Event

    EventHandler<KeyEvent> ENTER = new EventHandler<KeyEvent>(){
        public void handle(KeyEvent ENTER){
            switch(phase){
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

    /*
    EventHandler<MouseEvent> DRAG = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent DRAG){
            //TODO: Solve the disappearing card issue, something about the Z index
            double z =  DRAG.getZ() + 1;
            System.out.println(DRAG.getX() + "," + DRAG.getY() + "," + DRAG.getZ());
            PLAYER.setBounds(DRAG.getSceneX(), DRAG.getSceneY());

        }
    }; //Initializes DRAG Mouse Event
     */

    public void initDeck() throws FileNotFoundException { //Initializes the Deck
        //Reads in the image file for the back of the card and creates a faceDown Images object
        Images.faceDown = new Image(new FileInputStream(
                "C:\\Users\\Queen\\IdeaProjects\\Castle\\cardBackArrows.png"),
                100, 150, false, false);
        //Reads in the image files for the front face of the card deck and creates 52 faceUp image objects
        //TODO: Solve the blank face card issue
        Images.faceUp= new HashMap<String, Image>() ;
        String[] wordsInFilePNGNames= { "hearts", "diamonds", "spades", "clubs"} ;
        for (int suitIndex = 0; suitIndex < 4; suitIndex++){
            for ( int cardRank = 2; cardRank < 15; cardRank ++){
                //NOTE: Files that contain card face images have formatted as such "2 of Hearts.png"
                String PNGFileName = "C:\\Users\\Queen\\IdeaProjects\\Castle\\Playing Card PNGs\\" + cardRank + " of " + wordsInFilePNGNames[suitIndex] + ".png";
                Image faceUpCards = new Image(new FileInputStream(PNGFileName),100, 150, false, false);
                String cardNames = wordsInFilePNGNames[suitIndex] + cardRank ;
                Images.faceUp.put(cardNames, faceUpCards) ;
            }
        }
        //Initializes the deck and the Deal Pile
        deck  =  new CardDeck();
        deck.shuffle();
        for(int index = 0; index < deck.size(); index++){
            dealPile.setLayoutX(1100);
            dealPile.setLayoutY(285);
            dealPile.getChildren().add(deck.getCard(index));
        }
    }

    public void start (Stage stage) throws FileNotFoundException {
        stage.setTitle( "The Game Of Castle" );
        initDeck(); //Initializes Deck
        //Button clearButton      = new Button( "CLEAR TABLE" );
        Button dealCard         = new Button("DEAL CARD");
        Button setCastle        = new Button("CASTLE");
        Button setRandCastle    = new Button ("RANDOM CASTLE");
        System.out.println("There are " + deck.size() + " cards in the deck.");

        dealCard.setOnAction((ActionEvent event) -> {
            Card newCard = deck.dealCard();
            double HPHandPosX = 40 + (Card.WIDTH + 20);
            double HPHandPosY = 400;
            newCard.setCardPos(HPHandPosX, HPHandPosY);
            PLAYER.HAND.add(newCard);
        });

        setCastle.setOnAction((ActionEvent event) -> {
            if(PLAYER.UNSEEN_CASTLE.isEmpty()) {
                if (PLAYER.SELECTED.size() == 3) {
                    for (int i = 0; i < PLAYER.SELECTED.size(); i++) {
                        PLAYER.HAND.remove(PLAYER.SELECTED.get(i));
                        PLAYER.SELECTED.get(i).setBounds(900 + (Card.WIDTH + 100) * i, 600);
                        PLAYER.UNSEEN_CASTLE.add(PLAYER.SELECTED.get(i));
                    }
                }
                PLAYER.SELECTED.removeAll(PLAYER.SELECTED);
                PLAYER.HAND.forEach(Card::flipCard);
            }

        });

        //Adds buttons to the interface
        HBox buttonHolder = new HBox(16);
        //buttonHolder.getChildren().addAll(dealCard, setRandCastle, setCastle);
        buttonHolder.setAlignment(Pos.CENTER);
        buttonHolder.setPadding(new Insets( 0, 0, 20, 0 ));
        BorderPane borderPane = new BorderPane();
        borderPane.setBottom(buttonHolder);

        Group mainCardGroup = new Group();
        mainCardGroup.setManaged(false);
        mainCardGroup.getChildren().addAll(playerHand, computerHand, dealPile);

        instructions.setFont(new Font(24));
        root.getChildren().addAll(borderPane, mainCardGroup, instructions);
        Scene scene = new Scene(root, 1280, 720);

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, CLICK);
        //scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, DRAG);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, ENTER);

        //Reads background image in
        Images.background  = new Image(new FileInputStream("C:\\Users\\Queen\\IdeaProjects\\Castle\\Green Background.jpg"));
        ImagePattern ImagePattern  = new ImagePattern(Images.background);
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

                    newCard = deck.dealCard();
                    double HPHandPosX = 40 + (Card.WIDTH + 20) * index;
                    double HPHandPosY = 500;
                    newCard.setCardPos(HPHandPosX, HPHandPosY);
                    PLAYER.HAND.add(newCard);
                    playerHand.getChildren().add(PLAYER.HAND.get(index));
                }
                System.out.println("After dealing 10 cards to each player, there are " + deck.size() + " cards remaining in the deck.");

                System.out.println("There are " + PLAYER.HAND.size() + " cards in player hand.");
                System.out.println("There are " + COMPUTER.HAND.size() + " cards in computer hand.");
            }
        phase =  "init unseen castle";
    }

    public void flip() {
        if (!PLAYER.UNSEEN_CASTLE.isEmpty()) {
            PLAYER.HAND.forEach(Card::flipCard);
            phase = "init seen castle";
        }
    }

    public void initUnseenCastle(){
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

    public void initSeenCastle(){
        if(PLAYER.SEEN_CASTLE.isEmpty() && COMPUTER.SEEN_CASTLE.isEmpty()){
            if (PLAYER.SELECTED.size() == 3) {
                for (int i = 0; i < PLAYER.SELECTED.size(); i++) {
                    PLAYER.HAND.remove(PLAYER.SELECTED.get(i));
                    PLAYER.SELECTED.get(i).setBounds(900 + (Card.WIDTH + 100) * i, 560);
                    PLAYER.SEEN_CASTLE.add(PLAYER.SELECTED.get(i));
                }
            }
            PLAYER.SELECTED.removeAll(PLAYER.SELECTED);
            if (COMPUTER.HAND.size() != 3) {
                for (int i = 0; i < 3; i++) {
                    int n = rand.nextInt(COMPUTER.HAND.size());
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

    public void decideStart(){
        if (DISCARD.isEmpty()) {
            if (indexOfComputerSmallest() > indexOfHumanSmallest()) {
                COMPUTER.HAND.get(indexOfComputerSmallest()).flipCard();
                COMPUTER.HAND.get(indexOfComputerSmallest()).setCardPos(640, 360);
                DISCARD.add(COMPUTER.HAND.get(indexOfComputerSmallest()));
                COMPUTER.HAND.remove(indexOfComputerSmallest());
                COMPUTER.HAND.add(deck.dealCard());
                playerHand.getChildren().add(COMPUTER.HAND.get(3));
                for (int index = 0; index < COMPUTER.HAND.size(); index++)
                    COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 100);
                phase = "players turn";
            }
            else if (indexOfComputerSmallest() < indexOfHumanSmallest()) {
                PLAYER.HAND.get(indexOfHumanSmallest()).setCardPos(640, 360);
                DISCARD.add(PLAYER.HAND.get(indexOfHumanSmallest()));
                PLAYER.HAND.remove(indexOfHumanSmallest());
                PLAYER.HAND.add(deck.dealCard());
                playerHand.getChildren().add(PLAYER.HAND.get(3));
                PLAYER.HAND.get(3).flipCard();
                for (int index = 0; index < PLAYER.HAND.size(); index++)
                    PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
                phase = "computers turn";
            }
            else if (indexOfComputerSmallest() == indexOfHumanSmallest()){
                int n = rand.nextInt(2);
                if(n == 1){
                    COMPUTER.HAND.get(indexOfComputerSmallest()).flipCard();
                    COMPUTER.HAND.get(indexOfComputerSmallest()).setCardPos(640, 360);
                    DISCARD.add(COMPUTER.HAND.get(indexOfComputerSmallest()));
                    COMPUTER.HAND.remove(indexOfComputerSmallest());
                    COMPUTER.HAND.add(deck.dealCard());
                    playerHand.getChildren().add(COMPUTER.HAND.get(3));
                    for (int index = 0; index < COMPUTER.HAND.size(); index++)
                        COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 100);
                    phase = "players turn";
                }
                else if (n == 2){
                    PLAYER.HAND.get(indexOfHumanSmallest()).setCardPos(640, 360);
                    DISCARD.add(PLAYER.HAND.get(indexOfHumanSmallest()));
                    PLAYER.HAND.remove(indexOfHumanSmallest());
                    PLAYER.HAND.add(deck.dealCard());
                    playerHand.getChildren().add(PLAYER.HAND.get(3));
                    PLAYER.HAND.get(3).flipCard();
                    for (int index = 0; index < PLAYER.HAND.size(); index++)
                        PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
                    phase = "computers turn";
                }
            }
            System.out.println("test");
        }
    }

    public void computerTurn(){
        COMPUTER.HAND.get(indexOfComputerSmallest()).flipCard();
        COMPUTER.HAND.get(indexOfComputerSmallest()).setCardPos(640, 360);
        DISCARD.add(COMPUTER.HAND.get(indexOfComputerSmallest()));
        COMPUTER.HAND.remove(indexOfComputerSmallest());
        COMPUTER.HAND.add(deck.dealCard());
        playerHand.getChildren().add(COMPUTER.HAND.get(3));
        for (int index = 0; index < COMPUTER.HAND.size(); index++)
            COMPUTER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 100);
        phase = "players turn";
    }

    public void playerTurn(){
        PLAYER.selectedCard.setCardPos(640, 360);
        DISCARD.add(PLAYER.selectedCard);
        PLAYER.HAND.remove(PLAYER.selectedCard);
        PLAYER.HAND.add(deck.dealCard());
        playerHand.getChildren().add(PLAYER.HAND.get(3));
        PLAYER.HAND.get(3).flipCard();
        for (int index = 0; index < PLAYER.HAND.size(); index++)
            PLAYER.HAND.get(index).setCardPos(40 + (Card.WIDTH + 20) * index, 500);
        phase = "computers turn";
    }


    public boolean finished(){
        if(deck.size() < 0) {
            System.out.println("Game Finished");
            System.exit(0);
            return true;
        }
        return false;
    }

    public int indexOfComputerSmallest(){
        int index = 0;
        int min = COMPUTER.HAND.get(index).getRank();
        for (int i = 0; i < COMPUTER.HAND.size(); i++){
            if (COMPUTER.HAND.get(i).getRank() <= min){
                min = COMPUTER.HAND.get(i).getRank();
                index = i;
            }
        }
        return index;
    }

    public int indexOfHumanSmallest(){
        int index = 0;
        int min = PLAYER.HAND.get(index).getRank();
        for (int i = 0; i < PLAYER.HAND.size(); i++){
            if (PLAYER.HAND.get(i).getRank() <= min){
                min = PLAYER.HAND.get(i).getRank();
                index = i;
            }
        }
        return index;
    }

    public static void main(String args[] ){
        launch(args);
    }
}