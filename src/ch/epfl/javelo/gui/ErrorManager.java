package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {

    private static final double FROM_TO_OPACITY = 0;
    private static final double TO_FROM_OPACITY = 0.8;

    private final VBox vBox;
    private final SequentialTransition transition;

    public ErrorManager() {

        this.vBox = new VBox();
        this.vBox.getStylesheets().add("error.css");
        this.vBox.setMouseTransparent(true);


        FadeTransition firstTransition = new FadeTransition(Duration.millis(200), vBox);
        PauseTransition secondTransition = new PauseTransition(Duration.millis(2000));
        FadeTransition lastTransition = new FadeTransition(Duration.millis(500), vBox);

        firstTransition.setFromValue(FROM_TO_OPACITY);
        firstTransition.setToValue(TO_FROM_OPACITY);

        lastTransition.setFromValue(TO_FROM_OPACITY);
        lastTransition.setToValue(FROM_TO_OPACITY);

        this.transition = new SequentialTransition(firstTransition, secondTransition, lastTransition);

    }

    public VBox pane() {
        return vBox;
    }

    public void displayError(String errorMessage) {
        transition.stop();
        vBox.getChildren().clear();
        vBox.getChildren().add(new Text(errorMessage));
        java.awt.Toolkit.getDefaultToolkit().beep();
        transition.play();
    }

}
