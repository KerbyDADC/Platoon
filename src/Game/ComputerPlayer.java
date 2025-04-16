package Game;

import java.io.*;
import java.util.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;

public class ComputerPlayer {
    private Main mainInstance;

    public List<String> cardsInHand = new ArrayList<>();
    public List<String> computerCards = new ArrayList<>();
    List<StackPane> selectedCard = new ArrayList<>();
    public HBox computerPileLine;  
    List<Pane> pileStacks = new ArrayList<>();
    public Pane selectedComputerPile = null;
    public List<String> selectedPileCards = new ArrayList<>();


    
    // @Override
    public ComputerPlayer(Main mainInstance) {
        this.mainInstance = mainInstance;
    }

    public Pane getSelectedComputerPile() {
        return selectedComputerPile;
    }

    public List<String> getSelectedPileCards() {
        return selectedPileCards;
    }
    
    public void handMaker(Pane cardLine) {
        // get the card remaining after humanPlayer draws
        List<String> remainingCards = getAvailableCards();

        // draw 10 cards for computer
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            // remove cards from draw pool as its drawing them
            int cardIndex = rand.nextInt(remainingCards.size());
            String fileName = remainingCards.remove(cardIndex);
            computerCards.add(fileName);
            cardsInHand.add(fileName);
            System.out.println(fileName);

            // actually remove it from the card pool
            removeCardFromPool(fileName);

            // create card images (face down)
            File imageFile = new File("src/resources/card_back.jpg");
            Image cardBackImage = new Image(imageFile.toURI().toString());
            ImageView cardBack = new ImageView(cardBackImage);
            cardBack.setFitWidth(45);
            cardBack.setFitHeight(45);

            StackPane cardFinal = new StackPane(cardBack);
            cardFinal.setUserData(fileName);

            cardLine.getChildren().add(cardFinal);
        }
    }
    
    public HBox pileMaker(Pane gameUI, int pileScore) {
        System.out.println("computer used pileMaker");

        computerPileLine = new HBox(40);
        computerPileLine.setAlignment(Pos.CENTER);
        computerPileLine.setPrefWidth(700);
        computerPileLine.setLayoutY(40);

        List<List<Arc>> pileCorners = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {

            Rectangle pileBorder = new Rectangle(45, 60);
            pileBorder.setArcWidth(10);
            pileBorder.setArcHeight(10);
            pileBorder.setFill(Color.web("#008000"));
            pileBorder.setStroke(Color.WHITE);
            pileBorder.setStrokeWidth(3);
            pileBorder.setLayoutX(0);
            pileBorder.setLayoutY(0);

            Button pileButton = new Button();
            pileButton.setStyle("-fx-padding: 0; -fx-background-color: transparent;"); // no padding or button background
            pileButton.setPrefSize(55, 70);
            pileButton.setLayoutX(0);
            pileButton.setLayoutY(0);

            Arc topLeft = makeCorner();
            topLeft.setRotate(270);
            topLeft.setTranslateX(-5);
            topLeft.setTranslateY(0);

            Arc topRight = makeCorner();
            topRight.setRotate(0);
            topRight.setTranslateX(44);
            topRight.setTranslateY(0);

            Arc bottomRight = makeCorner();
            bottomRight.setRotate(90);
            bottomRight.setTranslateX(44);
            bottomRight.setTranslateY(65);

            Arc bottomLeft = makeCorner();
            bottomLeft.setRotate(180);
            bottomLeft.setTranslateX(-5);
            bottomLeft.setTranslateY(65);

            // sorting the pile's corners
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
            pileFinal.getChildren().addAll(pileBorder, topLeft, topRight, bottomLeft, bottomRight, pileButton);

            computerPileLine.getChildren().add(pileFinal);
            pileStacks.add(pileFinal);

            pileButton.setOnMouseExited(event -> {
                if (pileFinal != selectedComputerPile) {
                    topLeft.setVisible(false);
                    topRight.setVisible(false);
                    bottomLeft.setVisible(false);
                    bottomRight.setVisible(false);    
                }
            });


            pileButton.setOnMousePressed(event -> {
                runtest(pileFinal);
                int pileIndex = pileStacks.indexOf(pileFinal);
                System.out.println("player selected Computer Pile " + (pileIndex + 1));

                // check if a pile was already selected, if so hide its corners
                if (selectedComputerPile != null) {
                    int prevPile = pileStacks.indexOf(selectedComputerPile);
                    if (prevPile >= 0 && prevPile < pileCorners.size()) {
                        for (Arc arc : pileCorners.get(prevPile)) {
                            arc.setVisible(false);
                        }
                    }                
                }

                topLeft.setVisible(true);
                topRight.setVisible(true);
                bottomLeft.setVisible(true);
                bottomRight.setVisible(true);

                selectedComputerPile = pileStacks.get(pileIndex);
                checkRoundStart();
            });  
        }  
        addCardtoPile(0, pileStacks.get(1));

        for (Pane pile : pileStacks) {
            runtest(pile);
        }

        gameUI.getChildren().addAll(computerPileLine);
        return computerPileLine;
    }

    public List<String> getAvailableCards() {
        System.out.println("computer used getAvailableCards");
        return mainInstance.getAvailableCards();
    }

    public void removeCardFromPool(String cardFileName) {
        mainInstance.removeCardFromPool(cardFileName);
    }
    
    public void runtest(Pane selectedPile) {
        System.out.println("--- Testing Computer Card Piles ---");

        HBox pileLine = (HBox) selectedPile.getParent();
        int pileIndex = pileLine.getChildren().indexOf(selectedPile);
        
        int pileValue = 0;
        
        // use java streams to find all stackPane objects in pane
        List<Node> cardsInPile = selectedPile.getChildren().stream()
            .filter(node -> node instanceof StackPane && "card".equals(node.getUserData()))
            .toList();
        
        System.out.println("Pile " + (pileIndex + 1));
        System.out.println(" Found " + cardsInPile.size() + " cards in this pile");
        
        // loops for each stackPane found
        for (Node cardNode : cardsInPile) {
            StackPane cardPane = (StackPane) cardNode;
            
            String cardFileName = (String) cardPane.getProperties().get("actualCard");
        
            String cardName = cardFileName;
            if (cardName.endsWith(".png")) {
                cardName = cardName.substring(0, cardName.lastIndexOf("."));
            }
            cardName = cardName.replace("%20", " ");

            // add card name to selectedPileCards for roundCalculation
            selectedPileCards.add(cardName);

                    
            // update pile value and grab card value
            int cardValue = getCardValue(cardName);
            pileValue += cardValue;
                    
            System.out.println(" - " + cardName + " (Value: " + cardValue + ")");
            
        }
    
    // update pile value
    System.out.println(" Total Pile Value: " + pileValue);
    System.out.println("--- End of Test ---");
    }

    public void addCardtoPile(int pileIndex, Pane selectedPile) {
        if (cardsInHand.isEmpty()) {
            return;
        }
    
        // sorting face cards everything not a face card into lists
        List<String> jacks = new ArrayList<>();
        List<String> queens = new ArrayList<>();
        List<String> kings = new ArrayList<>();
        List<String> numberCards = new ArrayList<>();
        
        for (String cardName : cardsInHand) {
            // clean up card name
            String cleanCardName = cardName.replace("%20", " ");
            if (cleanCardName.endsWith(".png")) {
                cleanCardName = cleanCardName.substring(0, cleanCardName.lastIndexOf("."));
            }
            
            String[] parts = cleanCardName.split(" ");
            String value = parts[0].toLowerCase();
            
            if (value.equals("jack")) {
                jacks.add(cardName);
            } else if (value.equals("queen")) {
                queens.add(cardName);
            } else if (value.equals("king")) {
                kings.add(cardName);
            } else {
                numberCards.add(cardName);
            }
        }
        
        // create 5 piles according to priority
        List<List<String>> piles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            piles.add(new ArrayList<>());
        }
        
        // jack and queen piles
        if (!jacks.isEmpty() && !queens.isEmpty()) {
            List<String> jqPile = piles.get(0);
            // only add one jack and queen to save potential dupes for other piles
            jqPile.add(jacks.remove(0));
            jqPile.add(queens.remove(0));
        }
        
        // queen piles
        if (!queens.isEmpty() && piles.get(1).isEmpty()) {
            List<String> qPile = piles.get(1);
            // only add one queen to save potential dupes for other piles
            qPile.add(queens.remove(0));
        }
        
        // king piles
        if (!kings.isEmpty() && piles.get(2).isEmpty()) {
            List<String> kPile = piles.get(2);
            // only add one king to save potential dupes for other piles
            kPile.add(kings.remove(0));
        }
        
        // low score piles (2-6)
        if (!numberCards.isEmpty() && piles.get(3).isEmpty()) {
            List<String> lowScorePile = piles.get(3);
            
            // sort numbers by value
            numberCards.sort((card1, card2) -> {
                int value1 = getCardValue(card1);
                int value2 = getCardValue(card2);
                return Integer.compare(value1, value2);
            });
            
            int currentSum = 0;
            int targetSum = 4; 
            
            // try to make a pile with a sum between 2-6
            for (int i = 0; i < numberCards.size() && lowScorePile.size() < 5; i++) {
                String card = numberCards.get(i);
                int cardValue = getCardValue(card);
                
                if (currentSum + cardValue <= 6) {
                    lowScorePile.add(card);
                    numberCards.remove(i);
                    i--; 
                    currentSum += cardValue;
                    
                    if (currentSum >= 2 && currentSum <= 6) {
                        targetSum = currentSum; 
                    }
                }
            }
        }
        
        // high score piles (18-24)
        if (!numberCards.isEmpty() && piles.get(4).isEmpty()) {
            List<String> highScorePile = piles.get(4);
            
            // sort by value
            numberCards.sort((card1, card2) -> {
                int value1 = getCardValue(card1);
                int value2 = getCardValue(card2);
                return Integer.compare(value2, value1);
            });
            
            int currentSum = 0;
            
            // try to make a pile with sum a between 18-24
            for (int i = 0; i < numberCards.size() && highScorePile.size() < 5; i++) {
                String card = numberCards.get(i);
                int cardValue = getCardValue(card);
                
                if (currentSum + cardValue <= 24) {
                    highScorePile.add(card);
                    numberCards.remove(i);
                    i--; 
                    currentSum += cardValue;
                }
            }
        }
        
        // fill any remaining empty piles with leftover cards
        List<String> remainingCards = new ArrayList<>();
        remainingCards.addAll(jacks);
        remainingCards.addAll(queens);
        remainingCards.addAll(kings);
        remainingCards.addAll(numberCards);
        
        for (int i = 0; i < piles.size(); i++) {
            if (piles.get(i).isEmpty() && !remainingCards.isEmpty()) {
                List<String> pile = piles.get(i);
                while (pile.size() < 5 && !remainingCards.isEmpty()) {
                    pile.add(remainingCards.remove(0));
                }
            }
        }
        
        // if there are still completely empty piles and no remaining cards
        List<Integer> emptyPileIndices = new ArrayList<>();
        for (int i = 0; i < piles.size(); i++) {
            if (piles.get(i).isEmpty()) {
                emptyPileIndices.add(i);
            }
        }
        
        if (!emptyPileIndices.isEmpty()) {
            // take cards from low and high score piles
            List<String> cardsToRedistribute = new ArrayList<>();
            
            // check a pile for extra cards
            if (!piles.get(3).isEmpty()) {
                cardsToRedistribute.addAll(getLowValue(piles.get(3), emptyPileIndices.size()));
            }
            
            // check a different pile for extra cards
            if (cardsToRedistribute.size() < emptyPileIndices.size() && !piles.get(4).isEmpty()) {
                int remaining = emptyPileIndices.size() - cardsToRedistribute.size();
                cardsToRedistribute.addAll(getLowValue(piles.get(4), remaining));
            }
            
            // distribute cards to empty piles
            for (int i = 0; i < Math.min(emptyPileIndices.size(), cardsToRedistribute.size()); i++) {
                piles.get(emptyPileIndices.get(i)).add(cardsToRedistribute.get(i));
            }
        }
        
        // in case there are still empty piles
        while (!remainingCards.isEmpty()) {
            boolean cardAdded = false;
            
            for (int i = 0; i < piles.size() && !remainingCards.isEmpty(); i++) {
                List<String> pile = piles.get(i);
                if (pile.size() < 5) {
                    pile.add(remainingCards.remove(0));
                    cardAdded = true;
                }
            }
            
            if (!cardAdded) {
                break; 
            }
        }
        
        // putting at least one card in each pile
        // before this block of code I had some errors with empty piles
        for (int i = 0; i < piles.size(); i++) {
            if (piles.get(i).isEmpty()) {
                boolean cardFound = false;
                // take a card from a pile
                for (int j = 0; j < piles.size() && !cardFound; j++) {
                    if (i != j && piles.get(j).size() > 1) {
                        // take the last card in the pile
                        String card = piles.get(j).remove(piles.get(j).size() - 1);
                        piles.get(i).add(card);
                        cardFound = true;
                    }
                }
            }
        }

        // now actually place the cards in the visual piles
        placeCardsInVisualPiles(piles, selectedPile);
        
        cardsInHand.clear();
    }
    
    // helper method to get lowest value cards from a pile
    private List<String> getLowValue(List<String> pile, int count) {
        List<String> cardsCopy = new ArrayList<>(pile);
        List<String> lowestCards = new ArrayList<>();
        
        // sort by card value 
        cardsCopy.sort((card1, card2) -> {
            int value1 = getCardValue(card1);
            int value2 = getCardValue(card2);
            return Integer.compare(value1, value2);
        });
        
        for (int i = 0; i < Math.min(count, cardsCopy.size()); i++) {
            String card = cardsCopy.get(i);
            lowestCards.add(card);
            pile.remove(card); 
        }
        
        return lowestCards;
    }
    
    private int getCardValue(String cardFileName) {
        // clean up filename
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
    
    // add cards to the visual piles in the UI
    private void placeCardsInVisualPiles(List<List<String>> piles, Pane selectedPile) {
        // get HBox that gets all piles
        HBox pileLine = (HBox) selectedPile.getParent();
        
        for (int i = 0; i < piles.size(); i++) {
            List<String> pile = piles.get(i);
                        
            // get the visual pile container
            Pane visualPile = (Pane) pileLine.getChildren().get(i);

            Button pileButton = null;
            for (Node node : visualPile.getChildren()) {
                if (node instanceof Button) {
                    pileButton = (Button) node;
                    visualPile.getChildren().remove(pileButton); // remove temporarily
                    break;
                }
            }

            
            // add each card to pileVisual
            if (!pile.isEmpty()) {
                for (int j = 0; j < pile.size(); j++) {
                    String cardFileName = pile.get(j);
                    double offsetY = j * 10; 

                    // loading the real card image just not showing it
                    File realCardFile = new File("src/resources/images/" + cardFileName);
                    Image realCardImage = new Image(realCardFile.toURI().toString());
                    ImageView hiddenView = new ImageView(realCardImage);
                    hiddenView.setVisible(false); 

                    // Load card back image
                    File backFile = new File("src/resources/card_back.jpg");
                    Image backImage = new Image(backFile.toURI().toString());
                    ImageView backView = new ImageView(backImage);
                    backView.setFitWidth(45);
                    backView.setFitHeight(60);
                    backView.setPreserveRatio(true);
                    
                    // create the card as a StackPane
                    StackPane cardFinal = new StackPane(backView);
                    cardFinal.setUserData("card");
                    // store as a property so we can grab later
                    cardFinal.getProperties().put("actualCard", cardFileName);
                    cardFinal.setLayoutX(2);
                    cardFinal.setLayoutY(offsetY);
                    
                    visualPile.getChildren().add(cardFinal);
                    
                }
            }
            // re add the button so its on top of the cards
            if (pileButton != null) {
            visualPile.getChildren().add(pileButton);
            }
        }
        
        // remove all cards from hand visually
        mainInstance.computerCardLine.getChildren().clear();
    }

    public Arc makeCorner() {
        return mainInstance.makeCorner();
    }

    public void checkRoundStart() {
        mainInstance.checkRoundStart();
    }
}
