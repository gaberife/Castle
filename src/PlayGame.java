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
import java.util.HashMap;

public class PlayGame extends Application{
    CardDeck deck;

    /*TODO: Implement computer hand, player hand, most recent discard and full discard pile*/
    StackPane root = new StackPane();
    Text instructions = new Text( "CLICK THE CARDS AFTER DEALING." );
    Group lastDiscard, discard = new Group(); //TODO: Implement These
    Group dealPile = new Group();
    Group computerHand = new Group();
    Group playerHand = new Group();
    Group groupOfLoneCards  = new Group();
    Player PLAYER = new Player();
    Computer COMPUTER = new Computer();


    //TODO: make it so that more than just selected card can be interacted with
    EventHandler<MouseEvent> CLICK = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent CLICK) {
            double x = CLICK.getSceneX();
            double y = CLICK.getSceneY();
            if(PLAYER.HAND != null && PLAYER.checkBounds(CLICK.getSceneX(), CLICK.getSceneY()))
                PLAYER.getCard(x,y).flipCard();
            System.out.println(PLAYER.getScaleZ());

            //else
                //System.out.println("This is not working");
        }
    }; //Initializes CLICK Mouse Event

    EventHandler<MouseEvent> DRAG = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent DRAG){
            //TODO: Solve the disappearing card issue, something about the Z index
            PLAYER.setBounds(DRAG.getSceneX(), DRAG.getSceneY());
            PLAYER.toFront();
            PLAYER.setScaleZ(PLAYER.getScaleZ() + 1);
            System.out.println(playerHand.getScaleZ());

        }
    }; //Initializes DRAG Mouse Event

    public void initDeck() throws FileNotFoundException { //Initializes the Deck
        //Reads in the image file for the back of the card and creates a faceDown Images object
        Images.faceDown = new Image(new FileInputStream(
                "C:\\Users\\Queen\\IdeaProjects\\Castle\\cardBackArrows.png"),
                100, 150, false, false);
        //Reads in the image files for the front face of the card deck and creates 52 faceUp image objects (TODO: Minus Joker Values))
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
        Button  dealButton   = new Button( "DEAL" );
        Button  clearButton   = new Button( "CLEAR TABLE" );
        System.out.println("There are " + deck.size() + " cards in the deck.");
        dealButton.setOnAction((ActionEvent event) -> { //Assigns dealing action to the button
            if (instructions != null){
                root.getChildren().remove(instructions);
                instructions = null;
            }
            //Allocates 10 Cards to Each player
            for(int cardIndex  =  0; cardIndex  < 10; cardIndex ++){
                Card newCard = deck.dealCard();
                double CPHandPosX = 40 + (Card.WIDTH + 20) * cardIndex;
                double CPHandPosY = 50;
                newCard.setCardPos(CPHandPosX, CPHandPosY);
                COMPUTER.HAND.add(newCard);
                computerHand.getChildren().add(COMPUTER.HAND.get(cardIndex));

                newCard = deck.dealCard();
                double HPHandPosX = 40 + (Card.WIDTH + 20) * cardIndex;
                double HPHandPosY = 400;
                newCard.setCardPos(HPHandPosX, HPHandPosY);
                PLAYER.HAND.add(newCard);
                playerHand.getChildren().add(PLAYER.HAND.get(cardIndex));
            }
            System.out.println("After dealing 10 cards to each player, there are " + deck.size() + " cards remaining in the deck.");
        });

        //Assigns clear action to the button
        clearButton.setOnAction((ActionEvent event) -> {
            playerHand.getChildren().clear();
            computerHand.getChildren().clear();
        });

        //Adds buttons to the interface
        HBox buttonHolder = new HBox(16);
        buttonHolder.getChildren().addAll(dealButton, clearButton);
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
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, DRAG);


        //Reads background image in
        Images.background  = new Image(new FileInputStream("C:\\Users\\Queen\\IdeaProjects\\Castle\\Green Background.jpg"));
        ImagePattern ImagePattern  = new ImagePattern(Images.background);
        root.setBackground(null);
        scene.setFill(ImagePattern);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String args[] ){
        launch(args);
    }
}