package Game;

import java.io.*;
import java.util.*;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import java.util.stream.Collectors;


public class Main extends Application {

    // variable to see if a card is selected or not
    public AtomicInteger cardSelected = new AtomicInteger(0);
    // variable to see what card is selected
    List<StackPane> selectedCard = new ArrayList<>();
    Button[] buttons = new Button[10];
    Button[] pileButtons = new Button[5];
    public Pane gameUI;
    public int pileScore = 0;
    public List<Label> pileScoreLabels = new ArrayList<>();
    public List<String> availableCardPool = new ArrayList<>();
    public HBox pileLine;  
    public HBox cardLine; 
    public HBox computerPileLine;
    public HBox computerCardLine;
    public int playerRoundScore = 0;
    public int computerRoundScore = 0;
    public Label playerRoundScoreLabel;
    public Label playerPileScoreLabel;
    public Pane selectedPlayerPile = null;
    public Label computerRoundScoreLabel;
    public Label computerPileScoreLabel;
    public Label PileScoreLabel;
    public Button startRoundButton;
    public List<String> selectedPileCards = new ArrayList<>();
    public int roundCount = 0;
    public HBox playerCardArea;
    public HBox computerCardArea;

    HumanPlayer human = new HumanPlayer(this);
    ComputerPlayer computer = new ComputerPlayer(this);   


    @Override

    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        // ----------- Star Screen UI ---------
        VBox startScreen = new VBox(20);
        startScreen.setStyle("-fx-background-color:#008000");
        startScreen.setAlignment(Pos.CENTER);
        startScreen.setPrefSize(700, 400);

        Label title = new Label("Welcome to Platoon!");
        title.setFont(Font.font("System", FontWeight.BOLD, 30));

        Button playButton = new Button("Play");
        playButton.setPrefSize(80, 40);

        startScreen.getChildren().addAll(title, playButton);
        root.getChildren().add(startScreen);

        // new way to comment so I can see stuff
        // ----------- RoundPrepUI ------------
        Pane gameUI = new Pane();
        this.gameUI = gameUI;
        gameUI.setStyle("-fx-background-color:#008000");

        // lining up the buttons/images
        this.cardLine = new HBox(0.2);  
        cardLine.setAlignment(Pos.CENTER);
        cardLine.setPrefWidth(700); 
        cardLine.setLayoutY(250);  

        this.computerCardLine = new HBox(0.2);
        computerCardLine.setAlignment(Pos.CENTER);
        computerCardLine.setPrefWidth(700);
        computerCardLine.setLayoutY(10);

        Button playRoundButton = new Button("Play Round");
        playRoundButton.setStyle("-fx-font-size: 16; -fx-text-fill: black;");
        playRoundButton.setPrefSize(100,30);
        playRoundButton.setLayoutX(300);
        playRoundButton.setLayoutY(50);
        playRoundButton.setVisible(false);

        playRoundButton.setOnAction(e -> {
            System.out.println("Play round clicked");
            gameUI.getChildren().remove(playRoundButton);

            // create and add computerPlayer's hand and piles
            computer.handMaker(computerCardLine);
            computerPileLine = computer.pileMaker(gameUI, 0); 

            // set initial position off the screen            
            computerCardLine.setTranslateY(-140);
            computerPileLine.setTranslateY(-140);

            runtest();
            roundStartTransition();
        });

        // method to make the player hand
        human.handMaker(cardLine);

        // method to make the piles
        human.pileMaker(gameUI, pileScore);

        gameUI.getChildren().addAll(cardLine, playRoundButton);

        checkPlayRoundButton();

        // make the gameUI load when player presses play
        playButton.setOnAction(event -> {
            root.getChildren().clear();
            root.getChildren().add(gameUI);
        });

        Scene scene = new Scene(root, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // for some reason this code would only work here
        // if the user selects a card in a pile and presses q it goes back to the hand
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.Q) {
                // Check if any cards are selected
                if (!selectedCard.isEmpty()) {
                    List<StackPane> cardsToReturn = new ArrayList<>(selectedCard);
                    for (StackPane card : cardsToReturn) {
                        if (card.getParent() != cardLine) {
                            Pane currentPile = (Pane) card.getParent();
                            returnCardToHand(card, currentPile, cardLine);
                        }
                    }
                }
            }
        });
    }

    public List<String> loadImageFileNames(String filePath) {
        List<String> fileNames = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Add the file name to the list, trimming any extra spaces
                fileNames.add(line.trim());
            }
            // adds all the file names to the card pool
            if (availableCardPool.isEmpty()) {
                availableCardPool.addAll(fileNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return fileNames;
    }

    public void removeCardFromPool(String cardFileName) {
        availableCardPool.remove(cardFileName);
    }

    public List<String> getAvailableCards() {
        return new ArrayList<>(availableCardPool);
    }

    // ------- animations --------
    public void shakeButton(Node node, String errorMsg) {
        Label cardLimit = new Label("" + errorMsg);
        cardLimit.setStyle("-fx-background-color: RED");
        cardLimit.setAlignment(Pos.CENTER);

        // Add it to the UI
        cardLimit.setLayoutX(220);
        cardLimit.setLayoutY(213);
        gameUI.getChildren().add(cardLimit); // Make sure gameUI is your main Pane

        TranslateTransition shake = new TranslateTransition();
        shake.setNode(node);
        shake.setDuration(Duration.millis(100));
        shake.setByX(1.5);
        shake.setCycleCount(4);
        shake.setAutoReverse(true); // makes it so the card moves in the other direction making the shake
        shake.play();

        PauseTransition errorAnimation = new PauseTransition(Duration.seconds(3));
        errorAnimation.setOnFinished(event -> gameUI.getChildren().remove(cardLimit));
        errorAnimation.play();
    }

    public void handMaker(Pane cardLine) {
        // putting the cards into a list to draw from
        List<String> cardFileNames = loadImageFileNames("src/resources/card_Images.txt");

        // temp list so we can remove cards to prevent dupes
        List<String> tempList = new ArrayList<>(cardFileNames);
        Random rand = new Random();

        
        // ------- mini button code section -------
        // making buttons and putting images on them
        for (int i = 0; i < 10; i++) {
            int cardIndex = rand.nextInt(tempList.size());
            String fileName = tempList.remove(cardIndex); // removes the card at the index and stores it
        
            // taking the image and making it 
            File imageFile = new File("src/resources/images/" + fileName);
            if (imageFile.exists()) {
                Image img = new Image(imageFile.toURI().toString()); 
                ImageView view = new ImageView(img);
                view.setFitWidth(45);  
                view.setFitHeight(60); 
                view.setPreserveRatio(true);
                view.setSmooth(true);

                Button button = new Button();
                button.setGraphic(view);
                button.setStyle("-fx-padding: 0; -fx-background-color: transparent;"); // no padding or button background
                button.setPrefSize(45, 60); // match image size
                button.setMinSize(45, 60);
                button.setMaxSize(45, 60);

                // variable that tracks the state of the button (selected or not)
                final boolean[] isClicked = {false};

                Arc topLeft = makeCorner();
                topLeft.setRotate(270);
                topLeft.setTranslateX(-20);
                topLeft.setTranslateY(-28);
    
                Arc topRight = makeCorner();
                topRight.setRotate(0);
                topRight.setTranslateX(20);
                topRight.setTranslateY(-28);
    
                Arc bottomRight = makeCorner();
                bottomRight.setRotate(90);
                bottomRight.setTranslateX(20);
                bottomRight.setTranslateY(28);
    
                Arc bottomLeft = makeCorner();
                bottomLeft.setRotate(180);
                bottomLeft.setTranslateX(-20);
                bottomLeft.setTranslateY(28);
    
                topLeft.setVisible(false);
                topRight.setVisible(false);
                bottomLeft.setVisible(false);
                bottomRight.setVisible(false);
    
                button.setOnMouseEntered(event -> {
                    topLeft.setVisible(true);
                    topRight.setVisible(true);
                    bottomLeft.setVisible(true);
                    bottomRight.setVisible(true);
                });
                
                button.setOnMouseExited(event -> {
                    topLeft.setVisible(false);
                    topRight.setVisible(false);
                    bottomLeft.setVisible(false);
                    bottomRight.setVisible(false);
                });

                StackPane cardFinal = new StackPane(button, topLeft, topRight, bottomLeft, bottomRight);
                cardFinal.setUserData(fileName);
    
                // keeps button up when clicked
                button.setOnMouseClicked(event -> {
                    if (!isClicked[0]) {
                        if (cardSelected.get() < 5) {
                            cardFinal.setTranslateY(-8);
                            isClicked[0] = true;
                            selectedCard.add(cardFinal);
                            cardSelected.incrementAndGet();
                        } else {
                            // if a 6th button is selected, shake the card
                            shakeButton(button, "YOU CAN ONLY SELECT 5 CARDS AT ONCE");
                        }
                    } else { // puts button back down when clicked again
                        cardFinal.setTranslateY(0);
                        isClicked[0] = false;
                        selectedCard.remove(cardFinal);
                        cardSelected.decrementAndGet();                    }
                });
                

                buttons[i] = button;
                cardLine.getChildren().add(cardFinal);
            } else {
                System.out.println("Missing card file: " + fileName);
            }
        }
        
    }

    public void addCardtoPile(Pane cardLine, Pane selectedPile) {
        // count how many cards are in the pile
        long baseCards = selectedPile.getChildren().stream()
            .filter(n -> "card".equals(n.getUserData()))
            .count();

        // check if adding the selected cards would go over the 5 card limit 
        if (baseCards + selectedCard.size() <= 5) {
            List<Node> removeFromHand = new ArrayList<>();

            double pileVisual = 40;
    
            long[] index = {0};
    
            for (StackPane cardFinal : selectedCard) {
                final StackPane finalCard = cardFinal;
                finalCard.setUserData("card");

                Parent parent = finalCard.getParent();

                boolean inPileLine = false;
                Pane currentPile = null;

                for (Node node : pileLine.getChildren()) {
                    if (node instanceof Pane && ((Pane) node).getChildren().contains(finalCard)) {
                        inPileLine = true;
                        currentPile = (Pane) node;
                        break;
                    }
                }


                double offsetY = (baseCards + index[0]) * 10;
                final double finalOffset = offsetY;
                index[0]++;
        
                // get card's position
                Point2D start = finalCard.localToScene(0, 0);    
                Point2D end = selectedPile.localToScene(0, offsetY);
        
                // put it into game coords
                Point2D cardRoot = gameUI.sceneToLocal(start);
                Point2D pileRoot = gameUI.sceneToLocal(end);
        
                if (inPileLine && currentPile != null) {
                    currentPile.getChildren().remove(finalCard);
                    updatePileScore(currentPile);
                    repositionPileCards(currentPile);
                }

                // removes the card from hand
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(finalCard);
                }                
                finalCard.setTranslateX(0);
                finalCard.setTranslateY(0);
                gameUI.getChildren().add(finalCard);
                finalCard.relocate(cardRoot.getX(), cardRoot.getY());
        
                TranslateTransition move = new TranslateTransition(Duration.millis(300), finalCard);
                move.setToX(pileRoot.getX() - cardRoot.getX());
                move.setToY(pileRoot.getY() - cardRoot.getY());

                move.setOnFinished(e -> {
                    gameUI.getChildren().remove(finalCard);
                    finalCard.setTranslateX(0);
                    finalCard.setTranslateY(0);
                    finalCard.setLayoutX(0);
                    finalCard.setLayoutY(pileVisual + finalOffset);
                    selectedPile.getChildren().add(finalCard);
                    
                    updatePileScore(selectedPile);
                    repositionPileCards(selectedPile);
                });
        
                move.play();
                removeFromHand.add(finalCard);
            }
        
            PauseTransition delay = new PauseTransition(Duration.millis(400));
            delay.setOnFinished(e -> {
                cardLine.getChildren().removeAll(removeFromHand);
                selectedCard.clear();
                cardSelected.set(0);
                repositionPileCards(selectedPile);
                checkPlayRoundButton();
            });
            delay.play();

        } else {
            shakeButton(selectedCard.get(selectedCard.size() - 1), "You can't have more than 5 cards in a pile!");
        }
    }
    
    public Arc makeCorner() {
        Arc arc = new Arc();
        arc.setRadiusX(6);       
        arc.setRadiusY(6);        
        arc.setLength(90);        
        arc.setStartAngle(0);     
        arc.setType(ArcType.OPEN);
        arc.setStroke(Color.web("#33c5ea")); 
        arc.setStrokeWidth(3);
        arc.setFill(null);         
        return arc;
    }

    public HBox pileMaker(Pane gameUI, int pileScore) {
        // ------ card pile code -------
        List<Pane> pileStacks = new ArrayList<>();
        
        pileLine = new HBox(40);
        pileLine.setAlignment(Pos.CENTER);
        pileLine.setPrefWidth(700); 
        pileLine.setLayoutY(100);  

        List<List<Arc>> pileCorners = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            // make visual for the pile
            Rectangle pileBorder = new Rectangle(45, 60);
            pileBorder.setArcWidth(10);
            pileBorder.setArcHeight(10);
            pileBorder.setFill(Color.web("#008000"));
            pileBorder.setStroke(Color.WHITE);
            pileBorder.setStrokeWidth(3);
            pileBorder.setLayoutX(0);
            pileBorder.setLayoutY(40);

            // score holder for pile
            Rectangle pileScoreBox = new Rectangle(32,32);
            pileScoreBox.setArcWidth(10);
            pileScoreBox.setArcHeight(10);
            pileScoreBox.setFill(Color.WHITE);
            pileScoreBox.setStrokeWidth(3);
            pileScoreBox.setTranslateX(45);
            pileScoreBox.setTranslateY(54);

            // text visuals
            Label pileScoreText = new Label("0");
            pileScoreText.setTextFill(Color.BLACK);
            pileScoreText.setFont(Font.font("System", FontWeight.BOLD, 20));
            pileScoreText.setTranslateX(49);
            pileScoreText.setTranslateY(57);  

            pileScoreLabels.add(pileScoreText);
            
            // make pile a button so the player can select it
            Button pileButton = new Button();
            pileButton.setStyle("-fx-padding: 0; -fx-background-color: transparent;"); // no padding or button background
            pileButton.setPrefSize(55, 70); // match image size
            pileButton.setLayoutX(0);
            pileButton.setLayoutY(40);
            pileButtons[i] = pileButton;

            // pile gets highlighted when hovered over
            Arc topLeft = makeCorner();
            topLeft.setRotate(270);
            topLeft.setTranslateX(-5);
            topLeft.setTranslateY(40);

            Arc topRight = makeCorner();
            topRight.setRotate(0);
            topRight.setTranslateX(44);
            topRight.setTranslateY(40);

            Arc bottomRight = makeCorner();
            bottomRight.setRotate(90);
            bottomRight.setTranslateX(44);
            bottomRight.setTranslateY(105);

            Arc bottomLeft = makeCorner();
            bottomLeft.setRotate(180);
            bottomLeft.setTranslateX(-5);
            bottomLeft.setTranslateY(105);

            List<Arc> corners = new ArrayList<>();
            corners.add(topLeft);
            corners.add(topRight);
            corners.add(bottomLeft);
            corners.add(bottomRight);
            pileCorners.add(corners);    

            topLeft.setVisible(false);
            topRight.setVisible(false);
            bottomLeft.setVisible(false);
            bottomRight.setVisible(false);

            pileButton.setOnMouseEntered(event -> {
                topLeft.setVisible(true);
                topRight.setVisible(true);
                bottomLeft.setVisible(true);
                bottomRight.setVisible(true);
            });
            
            Pane pileFinal = new Pane();
            pileFinal.setPrefSize(45, 100);
            pileFinal.getChildren().addAll(pileBorder, pileScoreBox, pileScoreText, topLeft, topRight, bottomLeft, bottomRight, pileButton);

            pileLine.getChildren().add(pileFinal);
            
            pileStacks.add(pileFinal);

            pileButton.setOnMouseExited(event -> {
                if (pileFinal != selectedPlayerPile) {
                    topLeft.setVisible(false);
                    topRight.setVisible(false);
                    bottomLeft.setVisible(false);
                    bottomRight.setVisible(false);    
                }
            });

            // tracks what pile is selected
            final int pileIndex = i;
            pileButton.setOnMousePressed(event -> {
                if (selectedPlayerPile != null) {
                    int prevPile = pileStacks.indexOf(selectedPlayerPile);
                    if (prevPile >= 0 && prevPile < pileCorners.size()) {
                        for (Arc arc : pileCorners.get(prevPile)) {
                            arc.setVisible(false);
                        }
                    }
                }

                getPileCards(pileFinal);

                topLeft.setVisible(true);
                topRight.setVisible(true);
                bottomLeft.setVisible(true);
                bottomRight.setVisible(true);

                if (selectedCard.isEmpty()) {
                    final int pileIndex2 = pileIndex;
                    playerPileScoreLabel.setText(pileScoreLabels.get(pileIndex2).getText());
                    selectedPlayerPile = pileStacks.get(pileIndex2);
                    checkRoundStart();
                } else {
                    addCardtoPile(pileLine, pileStacks.get(pileIndex));
                }            
            });      
        
        }

        // adding the cards and card piles
        gameUI.getChildren().addAll(pileLine);

        return pileLine;
    }

    public int getCardValue(StackPane cardFinal) {
        int cardValue = 0;
        
        for (int i = 0; i < cardFinal.getChildren().size(); i++) {
            Node node = cardFinal.getChildren().get(i);
            if (node instanceof Button) {
                Button cardButton = (Button) node;
                // get the ImageView from the Button
                if (cardButton.getGraphic() instanceof ImageView) {
                    ImageView view = (ImageView) cardButton.getGraphic();
                    Image image = view.getImage();
                    String url = image.getUrl();
                    
                    File file = new File(url);
                    String imageName = file.getName();

                    // removes .png
                    String cardNameNoPNG = imageName.substring(0, imageName.lastIndexOf("."));
                    cardNameNoPNG = cardNameNoPNG.replace("%20", " ");

                    // splitting name into two parts so we can analyze both separately
                    String[] cardName = cardNameNoPNG.split(" ");
                    
                    // store cardName array into a stack pane for later use
                    cardFinal.setUserData(cardName);

                    // debug code
                    System.out.println("Card name: " + cardNameNoPNG);  

                    String value = cardName[0];

                    switch (value) {
                        case "1":
                            cardValue = 1;
                            break;
                        case "2":
                            cardValue = 2;
                            break;
                        case "3":
                            cardValue = 3;
                            break;
                        case "4":
                            cardValue = 4;
                            break;
                        case "5":
                            cardValue = 5;
                            break;
                        case "6":
                            cardValue = 6;
                            break;
                        case "7":
                            cardValue = 7;
                            break;
                        case "8":
                            cardValue = 8;
                            break;
                        case "9":
                            cardValue = 9;
                            break;
                        case "10":
                            cardValue = 10;
                            break;  
                        case "jack":
                            cardValue = 1;
                            break;
                        case "queen":
                            cardValue = 1;
                            break;
                        case "king":
                            cardValue = 10;
                            break;      
                    }
                }
            }
        }
        return cardValue;
    }

    public void returnCardToHand(StackPane card, Pane fromPile, Pane hand) {
        Point2D cardScenePos = card.localToScene(0, 0);
        Point2D cardStartInGameUI = gameUI.sceneToLocal(cardScenePos);
    
        // remove from the pile and place temporarily on gameUI for animation
        fromPile.getChildren().remove(card);
        updatePileScore(fromPile);
        gameUI.getChildren().add(card);
        card.relocate(cardStartInGameUI.getX(), cardStartInGameUI.getY());
        card.setTranslateX(0);
        card.setTranslateY(0);
    
        // get position of hand
        int handIndex = hand.getChildren().size();
        double offsetX = handIndex * 46; 
        Point2D handScenePos = hand.localToScene(offsetX, 0);
        Point2D handTargetInGameUI = gameUI.sceneToLocal(handScenePos);
    
        // moving back to hand animation
        TranslateTransition moveBack = new TranslateTransition(Duration.millis(300), card);
        moveBack.setToX(handTargetInGameUI.getX() - cardStartInGameUI.getX());
        moveBack.setToY(handTargetInGameUI.getY() - cardStartInGameUI.getY());
    
        moveBack.setOnFinished(e -> {
            gameUI.getChildren().remove(card);
            card.setTranslateX(0);
            card.setTranslateY(0);
        
            hand.getChildren().add(card);
            selectedCard.remove(card);
            cardSelected.decrementAndGet();

            repositionPileCards(fromPile);
        });
    
        moveBack.play();    
    }
    
    public void runtest() {
        System.out.println("--- Testing Card Piles ---");
        
        for (int i = 0; i < pileLine.getChildren().size(); i++) {
            // turn current pile into an object
            Pane pile = (Pane) pileLine.getChildren().get(i);
            // get current score of the pile
            int pileScore = Integer.parseInt(pileScoreLabels.get(i).getText());
            
            System.out.println("Pile " + (i+1) + " (Score: " + pileScore + "):");
            
            // use java streams to find all stackPane objects in pane
            List<Node> stackPanesInPile = pile.getChildren().stream()
                .filter(node -> node instanceof StackPane)
                .toList();
            
            // print out how many stackpane fouund
            System.out.println("  Found " + stackPanesInPile.size() + " cards in this pile");
            
            // loops for each stackPane found
            for (Node cardNode : stackPanesInPile) {
                
               StackPane card = (StackPane) cardNode;
                    
               // find the button with the image
                Optional<Node> buttonNode = card.getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .findFirst();
                    
                if (buttonNode.isPresent()) {
                    Button cardButton = (Button) buttonNode.get();
                    if (cardButton.getGraphic() instanceof ImageView) {
                        ImageView view = (ImageView) cardButton.getGraphic();
                        Image image = view.getImage();
                        String url = image.getUrl();
                            
                        File file = new File(url);
                        String imageName = file.getName();
                        
                        // remove %20 and .png
                        String cardName = imageName.substring(0, imageName.lastIndexOf("."));
                        cardName = cardName.replace("%20", " ");   
                        int cardValue = getCardValue(card);
                            
                        System.out.println("  - " + cardName + " (Value: " + cardValue + ")");
                    }
                }
            }
        }
        System.out.println("--- End of Test ---");
    }    

    public void getPileCards(Pane selectedPile) {
        System.out.println("--- Testing Player Card Piles ---");

        HBox pileLine = (HBox) selectedPile.getParent();
        int pileIndex = pileLine.getChildren().indexOf(selectedPile);
        
        int pileValue = 0;

        selectedPileCards.clear();
        
        // use java streams to find all stackPane objects in pane
        List<Node> cardsInPile = selectedPile.getChildren().stream()
            .filter(node -> node instanceof StackPane)
            .toList();
        
        System.out.println("Pile " + (pileIndex + 1));
        System.out.println(" Found " + cardsInPile.size() + " cards in this pile");
        
        // loops for each stackPane found
        for (Node cardNode : cardsInPile) {
            StackPane cardPane = (StackPane) cardNode;
            
            for (Node node : cardPane.getChildren()) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    Node graphic = button.getGraphic();
                    if (graphic instanceof ImageView) {
                        ImageView imageView = (ImageView) graphic;
                        Image image = imageView.getImage();
                        String url = image.getUrl();    
                        File file = new File(url);
                        String imageName = file.getName();
                        // get name of image
                        String cardName = imageName.substring(0, imageName.lastIndexOf("."));
                        cardName = cardName.replace("%20", " ");
                                    
                        // add card name to selectedPileCards for roundCalculation
                        selectedPileCards.add(cardName);

                                
                        // update pile value and grab card value
                        int cardValue = getPlayerCardValue(cardName);
                        pileValue += cardValue;
                                
                        System.out.println(" - " + cardName + " (Value: " + cardValue + ")");       
                        break;             
                    }
                }
            }                 
        }
    
    // update total pile value
    System.out.println(" Total Pile Value: " + pileValue);
    System.out.println("--- End of Test ---");
    }

    private int getPlayerCardValue(String cardFileName) {
        // fix file name
        String cleanCardName = cardFileName.replace("%20", " ");
        if (cleanCardName.endsWith(".png")) {
            cleanCardName = cleanCardName.substring(0, cleanCardName.lastIndexOf("."));
        }
        
        String[] parts = cleanCardName.split(" ");
        String value = parts[0].toLowerCase();
        
        switch (value) {
            case "jack": return 1;
            case "queen": return 1;
            case "king": return 10;
            default: return Integer.parseInt(value);
        }
    }


   

    public void roundStartTransition() {
        // move pileButtons to the top of the pile
        for (int i = 0; i < pileLine.getChildren().size(); i++) {
            Pane pile = (Pane) pileLine.getChildren().get(i);
            Button pileButton = pileButtons[i];

            pile.getChildren().remove(pileButton);
            pile.getChildren().add(pileButton);

            // tracks if a pile is clicked
            final int pileIndex = i;
            pileButton.setOnMouseClicked(event -> {
                selectedPlayerPile = pile;
                playerPileScoreLabel.setText(pileScoreLabels.get(pileIndex).getText());
            });
        }

        // create round score labels
        playerRoundScoreLabel = new Label("" + playerRoundScore);
        playerRoundScoreLabel.setFont(Font.font("System", FontWeight.BOLD, 30));
        playerRoundScoreLabel.setTextFill(Color.WHITE);
        playerRoundScoreLabel.setLayoutX(150);
        playerRoundScoreLabel.setLayoutY(230);
        
        // create pile score label
        playerPileScoreLabel = new Label("0");
        playerPileScoreLabel.setFont(Font.font("System", FontWeight.BOLD, 30));
        playerPileScoreLabel.setTextFill(Color.WHITE);
        playerPileScoreLabel.setLayoutX(500);
        playerPileScoreLabel.setLayoutY(230);

        // create containers so the text is centeres
        StackPane playerRoundScoreContainer = new StackPane();
        playerRoundScoreContainer.setPrefSize(100, 50); 
        playerRoundScoreContainer.setLayoutX(100);
        playerRoundScoreContainer.setLayoutY(220);

        StackPane playerPileScoreContainer = new StackPane();
        playerPileScoreContainer.setPrefSize(100, 50);
        playerPileScoreContainer.setLayoutX(510); 
        playerPileScoreContainer.setLayoutY(220); 

        playerRoundScoreContainer.getChildren().add(playerRoundScoreLabel);
        playerPileScoreContainer.getChildren().add(playerPileScoreLabel);


        // do the same thing for the computer player
        computerRoundScoreLabel = new Label("" + computerRoundScore);
        computerRoundScoreLabel.setFont(Font.font("System", FontWeight.BOLD, 30));
        computerRoundScoreLabel.setTextFill(Color.WHITE);
        computerRoundScoreLabel.setLayoutX(150);
        computerRoundScoreLabel.setLayoutY(230);
        
        // create pile score label
        computerPileScoreLabel = new Label("0");
        computerPileScoreLabel.setFont(Font.font("System", FontWeight.BOLD, 30));
        computerPileScoreLabel.setTextFill(Color.WHITE);
        computerPileScoreLabel.setLayoutX(500);
        computerPileScoreLabel.setLayoutY(230);

        // create containers so the text is centeres
        StackPane computerRoundScoreContainer = new StackPane();
        computerRoundScoreContainer.setPrefSize(100, 50); 
        computerRoundScoreContainer.setLayoutX(100);
        computerRoundScoreContainer.setLayoutY(140);

        StackPane computerPileScoreContainer = new StackPane();
        computerPileScoreContainer.setPrefSize(100, 50);
        computerPileScoreContainer.setLayoutX(510); 
        computerPileScoreContainer.setLayoutY(140); 

        computerRoundScoreContainer.getChildren().add(computerRoundScoreLabel);
        computerPileScoreContainer.getChildren().add(computerPileScoreLabel);

        

        // moves hand off screen (idk if this is actually needed)
        TranslateTransition moveHandOffScreen = new TranslateTransition(Duration.millis(500), cardLine);
        moveHandOffScreen.setToY(400); // move down off screen
        
        // moves piles down
        TranslateTransition movePilesDown = new TranslateTransition(Duration.millis(500), pileLine);
        movePilesDown.setToY(140); 
        
        // move computer pile down
        TranslateTransition moveCompPilesDown = new TranslateTransition(Duration.millis(500), computerPileLine);
        moveCompPilesDown.setToY(15);

        // move piles down when hand goes away
        moveHandOffScreen.setOnFinished(e -> {
            movePilesDown.play(); 
            moveCompPilesDown.play();
        });

        moveCompPilesDown.setOnFinished(e -> {
            gameUI.getChildren().addAll(playerPileScoreContainer, playerRoundScoreContainer, computerPileScoreContainer, computerRoundScoreContainer);
        });

        moveHandOffScreen.play();
    }

    public void checkPlayRoundButton() {
        int conditions = 0;
        boolean allPilesHaveCards = true;
        boolean handIsEmpty = cardLine.getChildren().isEmpty();
        
        System.out.println("Hand is empty: " + handIsEmpty);
    
        // Check all piles first for debugging
        for (int i = 0; i < pileLine.getChildren().size(); i++) {
            Pane pile = (Pane) pileLine.getChildren().get(i);
                        
            // count stackpanes regardless of userData
            long stackPaneCount = pile.getChildren().stream()
                .filter(node -> node instanceof StackPane)
                .count();
            
            System.out.println("Pile " + i + " StackPane count: " + stackPaneCount);

            if (stackPaneCount == 0) {
                allPilesHaveCards = false;
            }
        }
        
        System.out.println("All piles have cards: " + allPilesHaveCards);
        
        // searches every node in gameUI for something that has play and round
        Button playRoundButton = (Button) gameUI.getChildren().stream()
            .filter(node -> node instanceof Button && "Play Round".equals(((Button) node).getText()))
            .findFirst().orElse(null);

        

        if (handIsEmpty) {
            conditions++;
        }

        if (allPilesHaveCards) {
            conditions++;
        }

        if (conditions == 2) {
            playRoundButton.setVisible(allPilesHaveCards && handIsEmpty);
        }
        System.out.println("conditions met: " + conditions);
    }

    public void repositionPileCards(Pane pile) {
        List<Node> cards = pile.getChildren().stream()
            .filter(node -> node instanceof StackPane)
            .collect(Collectors.toList());

        // reposition each card to stack properly
        for (int i = 0; i < cards.size(); i++) {
            StackPane card = (StackPane) cards.get(i);

            card.setLayoutY(40 + (i * 10));
        }
    }

    public void updatePileScore(Pane pile) {
        int pileIndex = ((HBox) pile.getParent()).getChildren().indexOf(pile);
        if (pileIndex >= 0 && pileIndex < pileScoreLabels.size()) {
            Label scoreLabel = pileScoreLabels.get(pileIndex);
            
            int newScore = 0;

            for (Node node : pile.getChildren()) {
                if (node instanceof StackPane) {
                    StackPane cardPane = (StackPane) node;
                    newScore += getCardValue(cardPane);
                }
            }
            
            // update the score label
            scoreLabel.setText(String.valueOf(newScore));
            System.out.println("Updated pile " + pileIndex + " score to: " + newScore);
        
            if (pile == selectedPlayerPile && playerPileScoreLabel != null) {
                playerPileScoreLabel.setText("" + newScore);
            }
        }
    }

    // this code checks if both piiles are selected, then displays the start round button
    public void checkRoundStart() {
        startRoundButton = new Button("Play Hand");
        startRoundButton.setStyle("-fx-font-size: 16; -fx-text-fill: black;");
        startRoundButton.setPrefSize(120,30);

        // centering button to be in the middle y coordinate
        startRoundButton.setLayoutY(180);
        startRoundButton.setLayoutX(20);

        Pane selectedComputerPile = computer.getSelectedComputerPile();
        if (selectedPlayerPile != null && selectedComputerPile != null) {
            System.out.println("both piles have been selected");
            gameUI.getChildren().add(startRoundButton);
        } else {
            System.out.println("both piles have not been selected yet");
        }

        startRoundButton.setOnMouseClicked(e -> {
            roundCalculation();
            gameUI.getChildren().remove(startRoundButton);
            gameUI.getChildren().removeAll(playerCardArea, computerCardArea);

            // holding the cards to be animated
            List<StackPane> playerCards = new ArrayList<>();
            List<StackPane> computerCards = new ArrayList<>();

            for (Node node : selectedPlayerPile.getChildren()) {
                if (node instanceof StackPane) {
                    playerCards.add((StackPane) node);
                }
            }
            
            for (Node node : computer.selectedComputerPile.getChildren()) {
                if (node instanceof StackPane && "card".equals(node.getUserData())) {
                    computerCards.add((StackPane) node);
                }
            }

            // create card row areas
            playerCardArea = new HBox(2);
            playerCardArea.setAlignment(Pos.CENTER);
            playerCardArea.setPrefWidth(700);
            playerCardArea.setLayoutY(200);
            
            computerCardArea = new HBox(2);
            computerCardArea.setAlignment(Pos.CENTER);
            computerCardArea.setPrefWidth(700);
            computerCardArea.setLayoutY(130);
            
            gameUI.getChildren().addAll(playerCardArea, computerCardArea);
            
            List<StackPane> playerCardsToMove = new ArrayList<>(playerCards);
            List<StackPane> computerCardsToMove = new ArrayList<>(computerCards);

            // remove the cards from their piles
            for (StackPane card : playerCards) {
                selectedPlayerPile.getChildren().remove(card);
                playerCardArea.getChildren().add(card);
            }

            // reveal the card faces of the computer cards
            for (StackPane card : computerCards) {
                computer.selectedComputerPile.getChildren().remove(card);
                
                String cardFileName = (String) card.getProperties().get("actualCard");

                card.getChildren().clear();

                File realCardFile = new File("src/resources/images/" + cardFileName);
                Image realCardImage = new Image(realCardFile.toURI().toString());
                ImageView cardFaceView = new ImageView(realCardImage);
                cardFaceView.setFitWidth(45);
                cardFaceView.setFitHeight(60);
                cardFaceView.setPreserveRatio(true);

                card.getChildren().add(cardFaceView);
                
                computerCardArea.getChildren().add(card);
            }
            
            // unselect the piles
            if (selectedPlayerPile != null) {
                // remove the highlights from player pile
                for (Node node : selectedPlayerPile.getChildren()) {
                    if (node instanceof Arc) {
                        ((Arc) node).setVisible(false);
                    }
                }
            }
            
            if (computer.selectedComputerPile != null) {
                // remove the highlights from computer pile
                for (Node node : computer.selectedComputerPile.getChildren()) {
                    if (node instanceof Arc) {
                        ((Arc) node).setVisible(false);
                    }
                }
            }

            if (playerRoundScore == 3) {
                checkWin("You Won!");
            } else if (computerRoundScore == 3) {
                checkWin("You Lost!");
            } else if (roundCount == 4) {
                checkWin("It's a Draw!");
            }
    
        });
    }

    public void roundCalculation() {
        selectedPileCards.clear();
        getPileCards(selectedPlayerPile);

        // create temp lists to calculate who wins
        List<String> tempCompCards = new ArrayList<>();
        List<String> tempPlayerCards = new ArrayList<>();

        List<String> selectedComputerPileCards = computer.getSelectedPileCards();
        if (computer.selectedComputerPile != null) {
            computer.selectedPileCards.clear();
            computer.runtest(computer.selectedComputerPile);
            selectedComputerPileCards = computer.selectedPileCards;
        }
        
        for (String card : selectedComputerPileCards) {
            System.out.println(card);
            tempCompCards.add(card);
            
        }

        for (String card: selectedPileCards) {
            System.out.println("Player " + card);
            tempPlayerCards.add(card);
        }
        System.out.println("temp player cards lists -------");
        System.out.println(tempPlayerCards);

        // ------ this part of the code checks which hand wins -------
        // first, check if any player has a jack, if so, swap hands
        boolean playerHasJack = tempPlayerCards.stream().anyMatch(card -> card.toLowerCase().startsWith("jack"));
        boolean computerHasJack = tempCompCards.stream().anyMatch(card -> card.toLowerCase().startsWith("jack"));
        
        if (playerHasJack || computerHasJack) {
            System.out.println(" ------ swapping hands ------- ");
            
            // swap the card lists
            List<String> tempList = new ArrayList<>(tempCompCards);
            tempCompCards = new ArrayList<>(tempPlayerCards);
            tempPlayerCards = tempList;

            System.out.println("Player now has: " + tempPlayerCards);
            System.out.println("Computer now has: " + tempCompCards);
        }

        // first booleans to see if a hand has a king, queen, or jack
        boolean playerHasKing = tempPlayerCards.stream().anyMatch(card -> card.toLowerCase().startsWith("king"));
        boolean computerHasKing = tempCompCards.stream().anyMatch(card -> card.toLowerCase().startsWith("king"));
        boolean playerHasQueen = tempPlayerCards.stream().anyMatch(card -> card.toLowerCase().startsWith("queen"));
        boolean computerHasQueen = tempCompCards.stream().anyMatch(card -> card.toLowerCase().startsWith("queen"));
                

        // calculates pile values
        int playerPileValue = 0;
        for (String card : tempPlayerCards) {
            playerPileValue += getPlayerCardValue(card);
        }

        int computerPileValue = 0;
        for (String card : tempCompCards) {
            computerPileValue += getPlayerCardValue(card);
        }
            

        // check if any hand has a king
        if (playerHasKing && !computerHasKing) {
            if (computerHasQueen) {
                computerRoundScore++;
                System.out.println("round result: player has king but comp has queen");    
            } else {
                playerRoundScore++;
                System.out.println("round result: player has king");    
            }

        } else if (!playerHasKing && computerHasKing) {
            if (playerHasQueen) {
                playerRoundScore++;
                System.out.println("round result: comp has king but player has queen");    
            } else {
                computerRoundScore++;
                System.out.println("round result: comp has king");    
            }
        
        } else if (playerHasKing && computerHasKing) {
            // if both have kings, check for queens still
            if (playerHasQueen && computerHasQueen) {
                if (playerPileValue > computerPileValue) {
                    playerRoundScore++;
                    System.out.println("round result: both have kings and queens but player has higher score");
                } else if (computerPileValue > playerPileValue) {
                    computerRoundScore++;
                    System.out.println("round result: both have kings and queens but comp has higher score");
                } else {
                    System.out.println("round result: both have kings, queens, and equal pile scores");
                }    
            } else if (playerHasQueen) {
                playerRoundScore++;
                System.out.println("round result: both have kings but player has queen");
            } else if (computerHasQueen) {
                computerRoundScore++;
                System.out.println("round result: both have kings but computer has queen");
            } else {
                if (playerPileValue > computerPileValue) {
                    playerRoundScore++;
                    System.out.println("round result: both have kings and queens but player has higher score");
                } else if (computerPileValue > playerPileValue) {
                    computerRoundScore++;
                    System.out.println("round result: both have kings and queens but comp has higher score");
                } else {
                    System.out.println("round result: both have kings, no queens, and equal pile scores");
                }    
            }   
        // if no king is detected, look for queens for an insta loss
        } else {
            if (playerHasQueen && computerHasQueen) {
                if (playerPileValue > computerPileValue) {
                    playerRoundScore++;
                    System.out.println("round result: no kings, both have queens but player has higher score");
                } else if (computerPileValue > playerPileValue) {
                    computerRoundScore++;
                    System.out.println("round result: no kings, both have queens but comp has higher score");
                } else {
                    System.out.println("round result: no kings or queens, both have equal pile scores");
                }    
            } else if (playerHasQueen) {
                computerRoundScore++;
                System.out.println("round result: no kings, and player has queen, insta player loss");
            } else if (computerHasQueen) {
                playerRoundScore++;
                System.out.println("round result: no kings, and comp has queen, insta comp loss");     
            // if no kings or queens, calculate round win only using score
            } else {
                if (playerPileValue > computerPileValue) {
                    playerRoundScore++;
                    System.out.println("round result: no kings or queens, but player has higher score");
                } else if (computerPileValue > playerPileValue) {
                    computerRoundScore++;
                    System.out.println("round result: no kings or queens, but comp has higher score");
                } else {
                    System.out.println("round result: no kings or queens, both have equal pile scores");
                }    
            }
        }
        System.out.println("updated player score: " + playerRoundScore);
        System.out.println("updated computer score: " + computerRoundScore);

        playerRoundScoreLabel.setText(String.valueOf(playerRoundScore));
        computerRoundScoreLabel.setText(String.valueOf(computerRoundScore));

        // add labels to the piles that indicate whether or not they won
        Label playerResultLabel = new Label(playerRoundScore > computerRoundScore ? "WIN" : "LOSE");
        playerResultLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        playerResultLabel.setTextFill(playerRoundScore > computerRoundScore ? Color.LIGHTGREEN : Color.RED);
        
        // add the label over the pile
        playerResultLabel.setLayoutX(6);
        playerResultLabel.setLayoutY(110);
        selectedPlayerPile.getChildren().add(playerResultLabel);
        
        // same thing but for the computer piles
        Label computerResultLabel = new Label(computerRoundScore > playerRoundScore ? "WIN" : "LOSE");
        computerResultLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        computerResultLabel.setTextFill(computerRoundScore > playerRoundScore ? Color.LIGHTGREEN : Color.RED);
        
        // add the label over the pile
        computerResultLabel.setLayoutX(3);
        computerResultLabel.setLayoutY(-20);
        computer.selectedComputerPile.getChildren().add(computerResultLabel);

        // disable the buttons for these piles so they can't be selected again
        for (Node node : selectedPlayerPile.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setDisable(true);
            }
        }

        for (Node node : computer.selectedComputerPile.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setDisable(true);
            }
        }

        // every round, check if a player has won or if the game is tied
        roundCount++;
    }

    public void checkWin(String result) {
        String winner = new String(result);
        Rectangle darkOverlay = new Rectangle(0, 0, gameUI.getWidth(), gameUI.getHeight());

        // make the overlay a little transparent
        darkOverlay.setFill(Color.color(0, 0, 0, 0.7));

        // create label for winner text
        Label winnerLabel = new Label(winner);
        winnerLabel.setFont(Font.font("System", FontWeight.BOLD, 50));
        winnerLabel.setTextFill(Color.WHITE); 
        
        // create the restart game button
        Button restartButton = new Button("Play Again");
        restartButton.setStyle("-fx-font-size: 20; -fx-padding: 10 20;");
        restartButton.setOnAction(e -> restartGame());
        
        // box for the screen
        VBox winnerScreen = new VBox(30);
        winnerScreen.setAlignment(Pos.CENTER);
        winnerScreen.getChildren().addAll(winnerLabel, restartButton);
        winnerScreen.setLayoutX(gameUI.getWidth()/2 - 150); 
        winnerScreen.setLayoutY(gameUI.getHeight()/2 - 100); 
        
        // add to gameUI
        gameUI.getChildren().addAll(darkOverlay, winnerScreen);

        System.out.println("checkwin says: " + playerRoundScore);
    }

    // this code will reset most every value and essentially restart the game
    public void restartGame() {
        // reset scores
        playerRoundScore = 0;
        computerRoundScore = 0;
        
        // clear everything in the UI
        gameUI.getChildren().clear();
        
        // reset selected cards and piles
        selectedCard.clear();
        cardSelected.set(0);
        selectedPlayerPile = null;
        computer.selectedComputerPile = null;
        
        // reset card lines and piles
        if (cardLine != null) {
            cardLine.getChildren().clear();
        }
        if (pileLine != null) {
            pileLine.getChildren().clear();
        }
        if (computerCardLine != null) {
            computerCardLine.getChildren().clear();
        }
        if (computerPileLine != null) {
            computerPileLine.getChildren().clear();
        }
        
        // reset the pile score labels
        pileScoreLabels.clear();
        
        // reset the card pool
        availableCardPool.clear();
        loadImageFileNames("src/resources/card_Images.txt");
        
        // create new hand
        cardLine = new HBox(0.2);
        cardLine.setAlignment(Pos.CENTER);
        cardLine.setPrefWidth(700);
        cardLine.setLayoutY(250);
        human.handMaker(cardLine);
        
        // create new piles
        human.pileMaker(gameUI, 0);
        
        gameUI.getChildren().add(cardLine);
        
        // make play round button
        Button playRoundButton = new Button("Play Round");
        playRoundButton.setStyle("-fx-font-size: 16; -fx-text-fill: black;");
        playRoundButton.setPrefSize(100,30);
        playRoundButton.setLayoutX(300);
        playRoundButton.setLayoutY(50);
        playRoundButton.setVisible(false);
        gameUI.getChildren().add(playRoundButton);
        
        playRoundButton.setOnAction(e -> {
            System.out.println("Play round clicked");
            gameUI.getChildren().remove(playRoundButton);

            computer.handMaker(computerCardLine);
            computerPileLine = computer.pileMaker(gameUI, 0); 

            // starts off screen         
            computerCardLine.setTranslateY(-140);
            computerPileLine.setTranslateY(-140);

            runtest();
            roundStartTransition();
        });
        
        // check if play round button should appear
        checkPlayRoundButton();

    }
    public static void main(String[] args) {
        launch(args);
    }

}


