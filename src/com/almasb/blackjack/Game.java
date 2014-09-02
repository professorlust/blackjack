package com.almasb.blackjack;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.almasb.java.ui.FXWindow;

/**
 * Game's logic and UI
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class Game extends FXWindow {

    private Deck deck = new Deck();
    private Hand dealer = new Hand(), player = new Hand();
    private Text message = new Text();

    private Button btnPlay = new Button("PLAY");
    private Button btnHit = new Button("HIT");
    private Button btnStand = new Button("STAND");

    private HBox dealerCards = new HBox(20);
    private HBox playerCards = new HBox(20);

    @Override
    protected void createContent(Pane root) {
        root.setPrefSize(800, 600);

        Region background = new Region();
        background.setPrefSize(800, 600);
        background.setStyle("-fx-background-color: rgba(0, 0, 0, 1)");

        HBox rootLayout = new HBox(5);
        rootLayout.setPadding(new Insets(5, 5, 5, 5));
        Rectangle left = new Rectangle(550, 560);
        left.setArcWidth(50);
        left.setArcHeight(50);
        left.setFill(Color.GREEN);
        Rectangle right = new Rectangle(230, 560);
        right.setArcWidth(50);
        right.setArcHeight(50);
        right.setFill(Color.ORANGE);

        // LEFT

        StackPane leftStack = new StackPane();

        VBox leftVBox = new VBox(50);
        leftVBox.setAlignment(Pos.TOP_CENTER);

        Text dealerScore = new Text("Dealer: ");
        Text playerScore = new Text("Player: ");

        leftVBox.getChildren().addAll(dealerScore, dealerCards, message, playerCards, playerScore);
        leftStack.getChildren().addAll(left, leftVBox);

        // RIGHT

        StackPane rightStack = new StackPane();

        VBox rightVBox = new VBox(20);
        rightVBox.setAlignment(Pos.CENTER);

        final TextField bet = new TextField("BET");
        bet.setDisable(true);
        bet.setMaxWidth(50);
        Text money = new Text("MONEY");

        HBox buttonsHBox = new HBox(15);
        buttonsHBox.setAlignment(Pos.CENTER);
        btnHit.setDisable(true);
        btnStand.setDisable(true);
        buttonsHBox.getChildren().addAll(btnHit, btnStand);
        rightVBox.getChildren().addAll(bet, btnPlay, money, buttonsHBox);
        rightStack.getChildren().addAll(right, rightVBox);

        // ADD BOTH STACKS TO ROOT LAYOUT

        rootLayout.getChildren().addAll(leftStack, rightStack);
        root.getChildren().addAll(background, rootLayout);

        // BIND PROPERTIES

        playerScore.textProperty().bind(new SimpleStringProperty("Player: ").concat(player.valueProperty().asString()));
        dealerScore.textProperty().bind(new SimpleStringProperty("Dealer: ").concat(dealer.valueProperty().asString()));

        player.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue.intValue() >= 21) {
                endGame();
            }
        });

        dealer.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue.intValue() >= 21) {
                endGame();
            }
        });

        player.getCards().addListener((ListChangeListener.Change<? extends Card> change) -> {
            change.next();
            playerCards.getChildren().addAll(change.getAddedSubList());
        });

        dealer.getCards().addListener((ListChangeListener.Change<? extends Card> change) -> {
            change.next();
            dealerCards.getChildren().addAll(change.getAddedSubList());
        });

        // INIT BUTTONS

        btnPlay.setOnAction(event -> {
            startNewGame();
        });

        btnHit.setOnAction(event -> {
            player.takeCard(deck.drawCard());
        });

        btnStand.setOnAction(event -> {
            while (dealer.valueProperty().get() < 17) {
                dealer.takeCard(deck.drawCard());
            }

            endGame();
        });
    }

    @Override
    protected void initScene(Scene scene) {}

    @Override
    protected void initStage(Stage primaryStage) {
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);
        primaryStage.setTitle("BlackJack");
        primaryStage.show();
    }

    private void startNewGame() {
        btnPlay.setDisable(true);
        btnHit.setDisable(false);
        btnStand.setDisable(false);
        message.setText("");

        deck.refill();

        dealer.reset();
        player.reset();

        dealerCards.getChildren().clear();
        playerCards.getChildren().clear();

        dealer.takeCard(deck.drawCard());
        dealer.takeCard(deck.drawCard());
        player.takeCard(deck.drawCard());
        player.takeCard(deck.drawCard());
    }

    private void endGame() {
        btnHit.setDisable(true);
        btnStand.setDisable(true);

        int dealerValue = dealer.valueProperty().get();
        int playerValue = player.valueProperty().get();
        String winner = "Exceptional case: d: " + dealerValue + " p: " + playerValue;

        // the order of checking is important
        if (dealerValue == 21 || playerValue > 21 || dealerValue == playerValue
                || (dealerValue < 21 && dealerValue > playerValue)) {
            winner = "DEALER";
        }
        else if (playerValue == 21 || dealerValue > 21 || playerValue > dealerValue) {
            winner = "PLAYER";
        }

        message.setText(winner + " WON");
        btnPlay.setDisable(false);
    }
}
