package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {

    private final VBox pane;
    private final SequentialTransition transition;

    private static final double FROM_TO_OPACITY = 0;

    private static final double TO_FROM_OPACITY = 0.8;

    public ErrorManager(){

        this.pane = new VBox();
        this.pane.getStylesheets().add("error.css");
        this.pane.setMouseTransparent(true);


        FadeTransition firstTransition = new FadeTransition(new Duration(200), pane);
        PauseTransition secondTransition = new PauseTransition(new Duration(2000));
        FadeTransition lastTransition = new FadeTransition(new Duration(500), pane);

        firstTransition.setFromValue(FROM_TO_OPACITY);
        firstTransition.setToValue(TO_FROM_OPACITY);

        lastTransition.setFromValue(TO_FROM_OPACITY);
        lastTransition.setToValue(FROM_TO_OPACITY);


        this.transition = new SequentialTransition(firstTransition, secondTransition, lastTransition);

    }

    public VBox pane(){
        return pane;
    }

    public void displayError(String errorMessage){
        transition.stop();
        pane.getChildren().clear();
        pane.getChildren().add(new Text(errorMessage));
        java.awt.Toolkit.getDefaultToolkit().beep();
        transition.play();
    }

}
