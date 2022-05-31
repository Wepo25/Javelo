package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * A class displaying an error message when the user performs an action that lead to an error.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class ErrorManager {

    /**
     * The opacity of the first transition at the beginning and of the last transition at the end.
     */
    private static final double FROM_TO_OPACITY = 0;
    /**
     * The opacity of the last transition at the beginning and of the first transition at the end.
     */
    private static final double TO_FROM_OPACITY = 0.8;

    private final VBox vBox;
    private final SequentialTransition transition;

    /**
     * The constructor. Initialization of the vBox and of the transitions.
     */
    public ErrorManager() {

        this.vBox = new VBox();
        vBox.getStylesheets().add("error.css");
        vBox.setMouseTransparent(true);

        FadeTransition firstTransition = new FadeTransition(Duration.millis(200), vBox);
        PauseTransition secondTransition = new PauseTransition(Duration.millis(2000));
        FadeTransition lastTransition = new FadeTransition(Duration.millis(500), vBox);

        firstTransition.setFromValue(FROM_TO_OPACITY);
        firstTransition.setToValue(TO_FROM_OPACITY);

        lastTransition.setFromValue(TO_FROM_OPACITY);
        lastTransition.setToValue(FROM_TO_OPACITY);

        this.transition = new SequentialTransition(firstTransition, secondTransition, lastTransition);
    }

    /**
     * This method gives the vBox
     *
     * @return the vBox.
     */
    public VBox vbox() {
        return vBox;
    }

    /**
     * This method launches a sequence of transitions
     * informing the user that there has been an error with the action he tried to perform.
     *
     * @param errorMessage The message to display.
     */
    public void displayError(String errorMessage) {
        transition.stop();
        vBox.getChildren().clear();
        java.awt.Toolkit.getDefaultToolkit().beep();
        vBox.getChildren().add(new Text(errorMessage));
        transition.play();
    }
}
