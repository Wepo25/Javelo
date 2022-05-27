package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * This class managed interactions with the route's profile in width.
 *
 * @author Alexandre Mourot (346365)
 * @author Gaspard Thoral (345230)
 */
public final class ElevationProfileManager {

    /**
     * Array representing the separating spaces possible between verticals lines.
     */
    private static final int[] POS_STEPS = {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};

    /**
     * Array representing the separating spaces possible between horizontals lines.
     */
    private static final int[] ELE_STEPS = {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

    private final static Insets insets = new Insets(10, 10, 20, 40);

    private static final double TOP_INSET = insets.getTop();
    private static final double BOTTOM_INSET = insets.getBottom();
    private static final double LEFT_INSET = insets.getLeft();
    private static final double RIGHT_INSET = insets.getRight();
    private static final double HEIGHT_INSET = TOP_INSET + BOTTOM_INSET;
    private static final double WIDTH_INSET = LEFT_INSET + RIGHT_INSET;
    private static final int MIN_VERTICAL_SPACE = 50;
    private static final int MIN_HORIZONTAL_SPACE = 25;

    private static final String GRID_LABEL = "grid_label";
    private static final String HORIZONTAL_DIRECTION = "horizontal";
    private static final String VERTICAL_DIRECTION = "vertical";
    private static final String LABEL_FONT = "Avenir";
    private static final int LABEL_FONT_SIZE = 10;

    private static final String PATH_ID = "grid";
    private static final String POLYGON_ID = "profile";
    private static final String VBOX_ID = "profile_data";
    private static final String BORDERPANE_STYLESHEET_FILENAME = "elevation_profile.css";

    private static final String TRANSFORMATION_ERROR_MESSAGE_1 = "Transformation non invertible";

    private static final String STATISTICS_LENGTH_MESSAGE = "Longueur : %.1f km";
    private static final String STATISTICS_ASCENT_MESSAGE = "     Montée : %.0f m";
    private static final String STATISTICS_DESCENT_MESSAGE = "     Descente : %.0f m";
    private static final String STATISTICS_ELEVATION_MESSAGE = "     Altitude : de %.0f m à %.0f m";

    private static final String LINE_STEP_MIN_Y_PROPERTY = "minY";
    private static final String LINE_STEP_MAX_Y_PROPERTY = "maxY";

    private static final int KILOMETER_IN_METERS = 1000;

    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final DoubleProperty mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);
    private final ReadOnlyDoubleProperty highlightedPosition;

    private final Path path;
    private final Group textGroup;
    private final Polygon profileGraph;
    private final Line line;
    private final Pane pane;

    private final Text vboxText = new Text();

    private final BorderPane borderPane;

    private final ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>();


    /**
     * The constructor. Initialization of the arguments and pane. Attaches events handler and listener too.
     *
     * @param elevationProfile    elevation Profile corresponding to the route.
     * @param highlightedPosition the position to highlight along the profile.
     */
    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfile,
                                   ReadOnlyDoubleProperty highlightedPosition) {

        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;

        VBox vbox = new VBox(vboxText);
        vbox.setId(VBOX_ID);

        textGroup = new Group();
        line = new Line();

        path = new Path();
        path.setId(PATH_ID);

        profileGraph = new Polygon();
        profileGraph.setId(POLYGON_ID);

        pane = new Pane(path, textGroup, profileGraph, line);

        borderPane = new BorderPane(pane, null, null, vbox, null);
        borderPane.setBottom(vbox);
        borderPane.getStylesheets().setAll(BORDERPANE_STYLESHEET_FILENAME);

        pane.widthProperty().addListener((p, oldS, newS) -> operationsSequence());
        pane.heightProperty().addListener((p, oldS, newS) -> operationsSequence());

        rectangle.bind(Bindings.createObjectBinding(() ->
                new Rectangle2D(
                        LEFT_INSET,
                        TOP_INSET,
                        Math.max(0, pane.getWidth() - WIDTH_INSET),
                        Math.max(0, pane.getHeight() - HEIGHT_INSET)
                ),
                pane.widthProperty(),
                pane.heightProperty()
        ));

        borderPane.setOnMouseMoved(e -> {
            if (rectangle.get().contains(new Point2D(e.getX(), e.getY()))) {
                Point2D position = screenToWorld.get()
                        .transform(e.getX(), e.getY());
                mousePositionOnProfileProperty.set(position.getX());
            } else mousePositionOnProfileProperty.set(Double.NaN);
        });

        borderPane.setOnMouseExited(event -> {
            if (!rectangle.get().contains(new Point2D(event.getX(), event.getY()))) {
                mousePositionOnProfileProperty.set(Double.NaN);
            }
        });

        elevationProfile.addListener((p, oldS, newS) -> operationsSequence());

    }

    /**
     * This method return the BorderPane representing the elevation on screen.
     *
     * @return the BorderPane.
     */
    public BorderPane pane() {
        return borderPane;
    }

    /**
     * This method is used to actualise the profile.
     */
    private void operationsSequence() {
        if (elevationProfile.get() != null) {
            createTransformation();
            line();
            createGrid();
            createProfileGraph();
            createStats();
        }
    }

    private void createGrid() {

        textGroup.getChildren().clear();
        path.getElements().clear();

        double minElevation = elevationProfile.get().minElevation();
        double maxElevation = elevationProfile.get().maxElevation();
        double length = elevationProfile.get().length();

        int horizontalSpace = createHorizontalSpace();
        int verticalSpace = createVerticalSpace();
        int firstStep = Math2.ceilDiv((int) Math.round(minElevation), horizontalSpace) * horizontalSpace;

        int horizontalIndex = 0;
        int verticalIndex = 0;

        while (horizontalIndex * horizontalSpace + firstStep < maxElevation) {

            Point2D start = worldToScreen.get().transform(0, horizontalIndex * horizontalSpace + firstStep);
            Point2D end = worldToScreen.get().transform(length, horizontalIndex * horizontalSpace + firstStep);
            addToPath(start, end);
            createLabel(start.getX(), start.getY(), String.valueOf(horizontalIndex++ * horizontalSpace + firstStep),
                  HORIZONTAL_DIRECTION);
        }

        while (verticalIndex * verticalSpace < length) {

            Point2D start = worldToScreen.get().transform(verticalIndex * verticalSpace, minElevation);
            Point2D end = worldToScreen.get().transform(verticalIndex * verticalSpace, maxElevation);
            addToPath(start, end);
            createLabel(start.getX(), start.getY(),
                    String.valueOf(verticalIndex++ * verticalSpace / KILOMETER_IN_METERS),
                    VERTICAL_DIRECTION);
        }
    }

    private int createHorizontalSpace() {
        for (int eleStep : ELE_STEPS) {
            if (worldToScreen.get().deltaTransform(0, -eleStep).getY() >= MIN_HORIZONTAL_SPACE) return eleStep;
        }
        return ELE_STEPS[ELE_STEPS.length - 1];
    }

    private int createVerticalSpace() {
        for (int posStep : POS_STEPS) {
            if (worldToScreen.get().deltaTransform(posStep, 0).getX() >= MIN_VERTICAL_SPACE) return posStep;
        }
        return POS_STEPS[POS_STEPS.length - 1];
    }

    private void createLabel(double x, double y, String name, String type) {
        Text label = new Text(name);
        label.getStyleClass().setAll(GRID_LABEL, type);
        label.setFont(Font.font(LABEL_FONT, LABEL_FONT_SIZE));
        label.setTextOrigin((Objects.equals(type, HORIZONTAL_DIRECTION)) ? VPos.CENTER : VPos.TOP);
        label.setX(x);
        label.setY(y);
        label.setLayoutX(- ((Objects.equals(type, HORIZONTAL_DIRECTION)) ?
                (label.prefWidth(0) + 2) : 0.5 * label.prefWidth(0)));
        textGroup.getChildren().add(label);
    }

    private void addToPath(Point2D p1, Point2D p2){
        path.getElements().addAll(
                new MoveTo(p1.getX(), p1.getY()),
                new LineTo(p2.getX(), p2.getY())
        );
    }

    /**
     * This method is used to set the line (add bindings) representing the highlighted position on the profile.
     */
    private void line() {
        line.startYProperty().bind(Bindings.select(rectangle, LINE_STEP_MIN_Y_PROPERTY));
        line.endYProperty().bind(Bindings.select(rectangle, LINE_STEP_MAX_Y_PROPERTY));
        line.visibleProperty().bind(highlightedPosition.greaterThanOrEqualTo(0));
        line.layoutXProperty().bind(Bindings.createObjectBinding(
                () -> worldToScreen.get().transform(highlightedPosition.get(), 0).getX(),
                highlightedPosition,
                worldToScreen));
    }

    /**
     * This method create the polygon representing the profile graph.
     */
    private void createProfileGraph() {

        profileGraph.getPoints().clear();
        List<Double> toAdd = new ArrayList<>();

        for (double i = rectangle.get().getMinX(); i <= rectangle.get().getMaxX(); i++) {
            Point2D pointWorld = screenToWorld.get().transform(i, 0);
            double elevation = elevationProfile.get().elevationAt(pointWorld.getX());
            Point2D pointScreen = worldToScreen.get().transform(0, elevation);
            toAdd.add(i);
            toAdd.add(pointScreen.getY());
        }
        profileGraph.getPoints().addAll(toAdd);
        profileGraph.getPoints().addAll(rectangle.get().getMaxX(), rectangle.get().getMaxY(),
                rectangle.get().getMinX(), rectangle.get().getMaxY());
    }

    /**
     * This method is used to create the transformation allowing to change coordinate between
     * the real world and the screen and vice versa.
     */
    private void createTransformation() {
        Affine transformation = new Affine();
        transformation.prependTranslation(
                -rectangle.get().getMinX(),
                -rectangle.get().getMinY()
        );
        transformation.prependScale(
                elevationProfile.get().length() / rectangle.get().getWidth(),
                -(elevationProfile.get().maxElevation() - elevationProfile.get().minElevation()) /
                        rectangle.get().getHeight()
        );
        transformation.prependTranslation(
                0,
                elevationProfile.get().maxElevation()
        );
        screenToWorld.set(transformation);
        try {
            worldToScreen.set(transformation.createInverse());
        } catch (NonInvertibleTransformException e) {
            System.out.println(TRANSFORMATION_ERROR_MESSAGE_1);
        }
    }

    /**
     * This method allows us to create the label indicating the statistics of the profile.
     */
    private void createStats() {
        ElevationProfile ele = elevationProfile.get();
        vboxText.setText(String.format(STATISTICS_LENGTH_MESSAGE +
                        STATISTICS_ASCENT_MESSAGE +
                        STATISTICS_DESCENT_MESSAGE +
                        STATISTICS_ELEVATION_MESSAGE, ele.length() / KILOMETER_IN_METERS,
                ele.totalAscent(), ele.totalDescent(),
                ele.minElevation(), ele.maxElevation())
        );
    }

    /**
     * This method returns us the mouse position on the profile.
     *
     * @return a property containing the mouse position.
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }

}