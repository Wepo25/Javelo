package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {

    private final VBox pane;
    private final SequentialTransition transition;

    public ErrorManager(){
        this.pane = new VBox();
        this.pane.getStylesheets().add("error.css");
        this.pane.setMouseTransparent(true);


        FadeTransition firstTransition = new FadeTransition(new Duration(200), pane);
        PauseTransition secondTransition = new PauseTransition(new Duration(2000));
        FadeTransition lastTransition = new FadeTransition(new Duration(500), pane);

        firstTransition.setFromValue(0);
        firstTransition.setToValue(0.8);
        firstTransition.setCycleCount(1);
        firstTransition.setAutoReverse(true);

        lastTransition.setFromValue(0.8);
        lastTransition.setToValue(0);
        lastTransition.setCycleCount(1);
        lastTransition.setAutoReverse(true);

        this.transition = new SequentialTransition(firstTransition, secondTransition, lastTransition);

    }

    public VBox pane(){
        return pane;
    }

    public void displayError(String errorMessage){
        pane.getChildren().clear();
        pane.getChildren().add(new Text(errorMessage));
        java.awt.Toolkit.getDefaultToolkit().beep();
        transition.play();
        //IMPLEMENT STOP

    }
}
