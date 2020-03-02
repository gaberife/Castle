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
//Hey there
public class PlayGame extends Application{
    CardDeck deck;
    /*TODO: Implement computer hand, player hand, most recent discard and full discard pile*/
    StackPane root = new StackPane();
    Text instructions = new Text( "CLICK THE CARDS AFTER DEALING." );
    Group computerHand, computerCastleFaceUp, computerCastleFaceDown,
            playerHand, playerCastleFaceDown, playerCastleFaceUp,
            lastDiscard, discard, dealPile = new Group();
    Group rowOfCards = new Group();
    Group groupOfLoneCards  = new Group();
    Card selectedCard;

    EventHandler<MouseEvent> CLICK = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent CLICK) {
            if(selectedCard != null && selectedCard.getBounds(selectedCard,CLICK.getSceneX(),CLICK.getSceneY())) {
                selectedCard.turnCardFaceUp();
            }
            else
                System.out.println("This is not working");
        }
    };

    EventHandler<MouseEvent> DRAG = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent DRAG){
            selectedCard.setBounds(DRAG.getSceneX(), DRAG.getSceneY());
        }
    };


    public void initDeck() throws FileNotFoundException {
        //Reads in the image files for the front face of the card deck and creates 52 faceUp image objects (TODO: Minus Joker Values))
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
        deck  =  new CardDeck() ;
    }

    public void start (Stage stage) throws FileNotFoundException {
        stage.setTitle( "The Game Of Castle" );

        //Reads in the image file for the back of the card and creates a faceDown Images object
        Images.faceDown = new Image(new FileInputStream(
                "C:\\Users\\Queen\\IdeaProjects\\Castle\\cardBackArrows.png"),
                100, 150, false, false);

        initDeck();

        Button  dealButton   = new Button( "DEAL" );
        Button  shuffleButton = new Button( "SHUFFLE" );
        Button  clearButton   = new Button( "CLEAR TABLE" );

        //Assigns dealing action to the button
        dealButton.setOnAction((ActionEvent event) -> {
            if ( instructions != null ){
                root.getChildren().remove( instructions );
                instructions = null;
            }
            //Empties the list 'inside' the Group
            rowOfCards.getChildren().clear();

            for (int cardIndex  =  0; cardIndex  <  5; cardIndex  ++){
                Card newCard = deck.getCard();

                double cardPosX = 40; //+ ( Card.WIDTH + 20 ) * cardIndex;
                double cardPosY = 50;

                newCard.setCardPos(cardPosX, cardPosY);
                rowOfCards.getChildren().add(newCard);
            }

            selectedCard = deck.getCard();
            selectedCard.setCardPos(188,300);
            groupOfLoneCards.getChildren().clear();
            groupOfLoneCards.getChildren().add(selectedCard);
        });

        //Assigns shuffle action to the button
        //shuffleButton.setOnAction( (ActionEvent event) -> deck.shuffle());

        //Assigns clear action to the button
        clearButton.setOnAction((ActionEvent event) -> rowOfCards.getChildren().clear());

        shuffleButton.setOnAction( (ActionEvent event) -> {
            for (int cardIndex  =  0; cardIndex  <  5; cardIndex  ++) {
                Card newCard = deck.getCard();
            }
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
        mainCardGroup.getChildren().addAll(rowOfCards, groupOfLoneCards);

        instructions.setFont(new Font(24));
        root.getChildren().addAll(borderPane, mainCardGroup, instructions);
        Scene scene = new Scene(root, 910, 600 );

        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, CLICK);
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, DRAG);

        //Reads background image in
        Images.background  = new Image(new FileInputStream("C:\\Users\\Queen\\IdeaProjects\\Castle\\Green Background.jpg"));
        ImagePattern ImagePattern  = new ImagePattern(Images.background);
        root.setBackground(null);
        scene.setFill(ImagePattern);
        stage.setScene(scene);
        stage.show();
    }

    public static void main( String args[] ){
        launch(args);
    }
}