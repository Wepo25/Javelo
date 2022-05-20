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
    private static final double WIDTH_INSET = LEFT_INSET + RIGHT_INSET;
    private static final double HEIGHT_INSET = TOP_INSET + BOTTOM_INSET;
    private static final int MIN_VERTICAL_SPACE = 50;
    private static final int MIN_HORIZONTAL_SPACE = 25;

    private final ObjectProperty<ElevationProfile> elevationProfile;

    //MODIFICATION MADE -> TO CHECK
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
     * @param elevationProfile elevation Profile corresponding to the route.
     * @param highlightedPosition the position to highlight along the profile.
     */
    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfile,
                                   ReadOnlyDoubleProperty highlightedPosition) {

        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;
        textGroup = new Group();

        line = new Line();

        path = new Path();
        path.setId("grid");

        profileGraph = new Polygon();
        profileGraph.setId("profile");

        Text horizontalGridLabel = new Text();
        horizontalGridLabel.getStyleClass().setAll("grid_label", "horizontal");

        Text verticalGridLabel = new Text();
        verticalGridLabel.getStyleClass().setAll("grid_label", "vertical");

        textGroup.getChildren().setAll(horizontalGridLabel, verticalGridLabel);

        pane = new Pane(path, textGroup, profileGraph, line);


        VBox vbox = new VBox(vboxText);
        vbox.setId("profile_data");

        borderPane = new BorderPane(pane, null, null, vbox, null);
        borderPane.setBottom(vbox);
        borderPane.getStylesheets().setAll("elevation_profile.css");

        pane.widthProperty().addListener((p, oldS, newS) -> operationsSequence());
        pane.heightProperty().addListener((p, oldS, newS) -> operationsSequence());

        rectangle.bind(Bindings.createObjectBinding(() -> new Rectangle2D(LEFT_INSET, TOP_INSET,
                Math.max(0,pane.getWidth()-WIDTH_INSET),
                Math.max(0, pane.getHeight()-HEIGHT_INSET)),pane.widthProperty(), pane.heightProperty()
        ));


        borderPane.setOnMouseMoved(e -> {
            if(rectangle.get().contains(new Point2D(e.getX(),e.getY()))) {
                Point2D pos = screenToWorld.get().transform(e.getX(), e.getY());
                //MODIFICATION MADE -> TO CHECK
                mousePositionOnProfileProperty.set(Math.round(pos.getX()));

            }else{
                mousePositionOnProfileProperty.set(Double.NaN);
                }
            }
        );
        borderPane.setOnMouseExited(event -> {
            if (!rectangle.get().contains(new Point2D(event.getX(), event.getY()))) {
                mousePositionOnProfileProperty.set(Double.NaN);
            }
        });

        elevationProfile.addListener((p, oldS, newS) -> operationsSequence());

    }

    /**
     * This method return the BorderPane representing the elevation on screen.
     * @return the BorderPane.
     */
    public BorderPane pane() {
        return borderPane;
    }

    /**
     * This method is used to actualise the profile.

     */
    private void operationsSequence() {
        if(elevationProfile.get() != null){
        createTransformation();
        line();
        createGrid();
        createProfile();
        createStats();
    }}


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

        while(horizontalIndex * horizontalSpace + firstStep < maxElevation){
            Point2D startHorizontal = worldToScreen.get().transform(0, horizontalIndex*horizontalSpace+firstStep);
            Point2D endHorizontal = worldToScreen.get().transform(length, horizontalIndex*horizontalSpace+firstStep);
            path.getElements().addAll(new MoveTo(startHorizontal.getX(), startHorizontal.getY()),
                    new LineTo(endHorizontal.getX(), endHorizontal.getY()));
            createHorizontalLabel(startHorizontal.getX(),startHorizontal.getY(),String.valueOf(horizontalIndex*horizontalSpace+firstStep));
            horizontalIndex++;
        }

        while(verticalIndex * verticalSpace < length){
            Point2D startVertical = worldToScreen.get().transform(verticalIndex*verticalSpace, minElevation);
            Point2D endVertical = worldToScreen.get().transform(verticalIndex*verticalSpace, maxElevation);
            path.getElements().addAll(new MoveTo(startVertical.getX(), startVertical.getY()),
                    new LineTo(endVertical.getX(), endVertical.getY()));
            createVerticalLabel(startVertical.getX(),startVertical.getY(),String.valueOf(verticalIndex*verticalSpace/1000));
            verticalIndex++;
        }
    }

    private int createHorizontalSpace(){
        int horizontalSpace = 0;
        for (int eleStep : ELE_STEPS) {
            if (worldToScreen.get().deltaTransform(0, -eleStep).getY() >= MIN_HORIZONTAL_SPACE) {
                horizontalSpace = eleStep;
                break;
            }
        }
        return (worldToScreen.get().deltaTransform(0, -horizontalSpace).getY() < MIN_HORIZONTAL_SPACE)? ELE_STEPS[ELE_STEPS.length-1] : horizontalSpace;
    }

    private int createVerticalSpace(){
        int verticalSpace = 0;
        for (int posStep : POS_STEPS) {
            if (worldToScreen.get().deltaTransform(posStep, 0).getX() >= MIN_VERTICAL_SPACE) {
                verticalSpace = posStep;
                break;
            }
        }
        return (worldToScreen.get().deltaTransform(verticalSpace, 0).getX() < MIN_VERTICAL_SPACE)? POS_STEPS[POS_STEPS.length-1] : verticalSpace;
    }

    private void createVerticalLabel(double x, double y, String s){
        Text label = new Text(s);
        label.setTextOrigin(VPos.TOP);
        label.setX(x-0.5 * label.prefWidth(0));
        label.setY(y);
        label.getStyleClass().addAll("grid_label", "vertical");
        label.setFont(Font.font("Avenir", 10));
        textGroup.getChildren().add(label);
    }


    private void createHorizontalLabel(double x, double y, String s){
        Text label = new Text(s);
        label.setTextOrigin(VPos.CENTER);
        label.getStyleClass().addAll("grid_label", "horizontal");
        label.setFont(Font.font("Avenir", 10));
        label.setX(x-(label.prefWidth(0)+2));
        label.setY(y-10);
        textGroup.getChildren().add(label);

    }

    /**
     * This method is used to set the line (add bindings) representing the highlighted position on the profile.
     */
    private void line() {
        line.startYProperty().bind(Bindings.select(rectangle, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle, "maxY"));

        line.visibleProperty().bind(
                highlightedPosition.greaterThanOrEqualTo(0)
        );

        line.layoutXProperty().bind(Bindings.createObjectBinding(() ->{
            return worldToScreen.get().transform(highlightedPosition.get(),0).getX();
        },highlightedPosition, worldToScreen));

    }

    /**
     * This method create the polygon representing the profile graph.
     */
    private void createProfile() {

        List<Double> toAdd = new ArrayList<>();

        for (double i = rectangle.get().getMinX(); i <= rectangle.get().getMaxX(); i++) {
            Point2D pointWorld= screenToWorld.get().transform(i, 0);
            double elevation = elevationProfile.get().elevationAt(pointWorld.getX());
            Point2D pointScreen = worldToScreen.get().transform(0,elevation);
            toAdd.add(i);
            toAdd.add( pointScreen.getY());
        }
        toAdd.add(rectangle.get().getMaxX());
        toAdd.add(rectangle.get().getMaxY());
        toAdd.add(rectangle.get().getMinX());
        toAdd.add(rectangle.get().getMaxY());

        profileGraph.getPoints().setAll(toAdd);

    }

    /**
     * This method is used to create the transformation allowing to change coordinate between
     * the real world and the screen and vice versa.
     */
    private void createTransformation() {
        Affine transformation = new Affine();
        transformation.prependTranslation(-rectangle.get().getMinX(), -rectangle.get().getMinY());
        transformation.prependScale(elevationProfile.get().length() / rectangle.get().getWidth(),
                -(elevationProfile.get().maxElevation() - elevationProfile.get().minElevation()) /
                        rectangle.get().getHeight());
        transformation.prependTranslation(0, elevationProfile.get().maxElevation());
        screenToWorld.set(transformation);
        try {
            worldToScreen.set(transformation.createInverse());
        } catch (NonInvertibleTransformException e) {
            System.out.println("Transformation non invertible");
        }
    }

    /**
     * This method allows us to create the label indicating the statistics of the profile.
     */
    private void createStats() {
    ElevationProfile ele= elevationProfile.get();
        vboxText.setText(String.format("Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",ele.length() / 1000,ele.totalAscent(), ele.totalDescent(),
                ele.minElevation(),ele.maxElevation()));

    }



    /**
     * This method returns us the mouse position on the profile.
     * @return a property containing the mouse position.
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return mousePositionOnProfileProperty;
    }

}