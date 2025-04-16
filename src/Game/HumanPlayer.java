package Game;

import javafx.scene.layout.*;

public class HumanPlayer {
    private Main mainInstance;
    
    public HumanPlayer(Main mainInstance) {
        this.mainInstance = mainInstance;
    }
    
    public void handMaker(Pane cardLine) {
        System.out.println("human used handMaker");
        mainInstance.handMaker(cardLine);
    }
    
    public HBox pileMaker(Pane gameUI, int pileScore) {
        System.out.println("human used pileMaker");
        return mainInstance.pileMaker(gameUI, pileScore);
    }
    
    public void returnCardToHand(StackPane card, Pane fromPile, Pane hand) {
        System.out.println("human used returnCardToHand");
        mainInstance.returnCardToHand(card, fromPile, hand);
    }
    
    public int getCardValue(StackPane cardFinal) {
        System.out.println("Player used getCardValue");
        return mainInstance.getCardValue(cardFinal);
    }
    
    public void runtest() {
        System.out.println("human ran a test");
        mainInstance.runtest();
    }
    
    public void addCardtoPile(Pane cardLine, Pane selectedPile) {
        System.out.println("human used addCardtoPile");
        mainInstance.addCardtoPile(cardLine, selectedPile);
    }
}