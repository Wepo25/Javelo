package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
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

    private final ObjectProperty<ElevationProfile> elevationProfile;
    private  ReadOnlyDoubleProperty highlightPosition;
    private final ObjectProperty<Double> mousePositionOnProfileProperty = new SimpleObjectProperty<>();

    private final Insets insets;

    private final Path path;
    private final Group textGroup;
    private final Polygon profileGraph;
    private final Line highlightedPosition;
    private final Pane pane;

    private final VBox vbox;

    private final Text vboxText = new Text();
    private final Text horizontalGridLabel = new Text();
    private final Text verticalGridLabel = new Text();

    private final BorderPane borderPane;

    private final ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>();


    /**
     * The constructor. Initialization of the arguments and pane. Attaches events handler and listener too.
     * @param elevationProfile elevation Profile corresponding to the route.
     * @param position the position to highlight along the profile.
     */
    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty position) {

        this.elevationProfile = elevationProfile;
        this.highlightPosition = position;

        textGroup = new Group();

        highlightedPosition = new Line();

        path = new Path();
        path.setId("grid");

        profileGraph = new Polygon();
        profileGraph.setId("profile");

        horizontalGridLabel.getStyleClass().setAll("grid_label", "horizontal");

        verticalGridLabel.getStyleClass().setAll("grid_label", "vertical");

        textGroup.getChildren().setAll(horizontalGridLabel, verticalGridLabel);

        pane = new Pane(path, textGroup, profileGraph, highlightedPosition);


        vbox = new VBox(vboxText);
        vbox.setId("profile_data");

        borderPane = new BorderPane(pane, null, null, vbox, null);
        borderPane.setBottom(vbox);
        borderPane.getStylesheets().setAll("elevation_profile.css");
        insets = new Insets(10, 10, 20, 40);


        pane.widthProperty().addListener(l -> operationsSequence(highlightPosition));
        pane.heightProperty().addListener(l -> operationsSequence(highlightPosition));

        rectangle.bind(Bindings.createObjectBinding(() -> new Rectangle2D(insets.getLeft(), insets.getTop(),
                Math.max(0, pane.getWidth() - insets.getLeft() - insets.getRight()),
                Math.max(0, pane.getHeight() - insets.getTop() - insets.getBottom())), pane.widthProperty(), pane.heightProperty()
        ));


        borderPane.setOnMouseMoved(e -> {
            if(rectangle.get().contains(new Point2D(e.getX(),e.getY()))) {
                Point2D pos = screenToWorld.get().transform(e.getX(), e.getY());
                mousePositionOnProfileProperty.set(pos.getX());
            }else{
                mousePositionOnProfileProperty.set(Double.NaN);
                }
            }
        );
        borderPane.setOnMouseExited(event -> mousePositionOnProfileProperty.setValue(Double.NaN));

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
     * @param pos
     */
    private void operationsSequence(ReadOnlyDoubleProperty pos) {
        createTransformation();
        line(pos);
        textGroup.getChildren().clear();
        path.getElements().clear();
        createGrid();
        createProfile();
        createStats();
    }

    /**
     * This method create or re-create the grid when needed.
     */
    private void createGrid(){

        double horizontalSpace = createHorizontalSpace();
        double verticalSpace = createVerticalSpace();
        int horizontalIndex = 0;
        System.out.println(horizontalSpace);
        System.out.println(worldToScreen.get().transform(40,horizontalSpace).getY());


        while(horizontalIndex * horizontalSpace + elevationProfile.get().minElevation() < elevationProfile.get().maxElevation()){
            Point2D p0 = worldToScreen.get().transform(0, horizontalIndex * horizontalSpace + elevationProfile.get().minElevation());
            Point2D p01 = worldToScreen.get().transform(elevationProfile.get().length(),
                    horizontalIndex * horizontalSpace + elevationProfile.get().minElevation());

            PathElement horizontalLineStart = new MoveTo(p0.getX(), p0.getY());
            PathElement horizontalLineEnd = new LineTo(p01.getX(), p01.getY());
            path.getElements().addAll(horizontalLineStart, horizontalLineEnd);
            horizontalIndex++;
        }

        int verticalIndex = 0;
        while(verticalSpace * verticalIndex < elevationProfile.get().length()){
            Point2D p1 = worldToScreen.get().transform(verticalSpace*verticalIndex,elevationProfile.get().minElevation());
            Point2D p2 = worldToScreen.get().transform(verticalSpace*verticalIndex,elevationProfile.get().maxElevation());
            PathElement moveTo = new MoveTo(p1.getX() ,p1.getY());
            PathElement lineTo = new LineTo(p2.getX(), p2.getY());
            path.getElements().add(moveTo);
            path.getElements().add(lineTo);
            createVerticalLabel(p1.getX(), p1.getY()+10, String.valueOf(verticalIndex));
            verticalIndex++;

        }

    }

    /**
     * This method compute the space between horizontal lines.
     * @return the space under a double's.
     */
    private double createHorizontalSpace(){
        double horizontalSpace = 0;
        for (int i = 0; i < ELE_STEPS.length; i++) {
            if(worldToScreen.get().deltaTransform(0,-ELE_STEPS[i]).getY()>=25){
                horizontalSpace = ELE_STEPS[i];
                break;
            }
        }
        return (horizontalSpace == 0)? ELE_STEPS[ELE_STEPS.length-1] : horizontalSpace;
    }

    /**
     * This method compute the vertical space between vertical lines.
     * @return the space under a double's.
     */
    private double createVerticalSpace(){
        double verticalSpace = 0;
        for (int i = 0; i < POS_STEPS.length; i++) {
            if(worldToScreen.get().deltaTransform(POS_STEPS[i],0).getX()>=50){
                verticalSpace = POS_STEPS[i];
                break;
            }
        }
        return (verticalSpace == 0)? POS_STEPS[POS_STEPS.length-1] : verticalSpace;
    }

    /**
     * This method allows us to create the verticals label indicating the length.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param s the text to show.
     */
    private void createVerticalLabel(double x, double y, String s){
        Text label = new Text();
        label.setText(s);
        label.setX(x);
        label.setY(y);
        label.getStyleClass().addAll("grid_label", "vertical");
        label.setFont(Font.font("Avenir", 10));
        textGroup.getChildren().add(label);
    }


    /**
     * This method allows us to create the horizontal label indicating the height.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param s the text to show.
     */
    private void createHorizontalLabel(double x, double y, String s){
        Text label = new Text();
        label.setText(s);
        label.setX(x);
        label.setY(y);
        label.getStyleClass().addAll("grid_label", "horizontal");
        label.setFont(Font.font("Avenir", 10));
        textGroup.getChildren().add(label);
    }

    /**
     * This method is used to set the line (add bindings) representing the highlighted position on the profile.
     * @param pos position to be highlighted.
     */
    private void line(ReadOnlyDoubleProperty pos) {

        highlightedPosition.layoutXProperty().bind(Bindings.createObjectBinding(() ->
                worldToScreen.get().transform(highlightPosition.get(),0).getX(),highlightPosition, worldToScreen));

        highlightedPosition.startYProperty().bind(Bindings.select(rectangle, "minY"));
        highlightedPosition.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        highlightedPosition.visibleProperty().bind(
                highlightPosition.greaterThanOrEqualTo(0)
        );
    }

    /**
     * This method create the polygon representing the profile graph.
     */
    private void createProfile() {

        List<Double> toAdd = new ArrayList<>();

        for (double i = rectangle.get().getMinX(); i <= rectangle.get().getMaxX(); i++) {
            Point2D pointWorld = screenToWorld.get().transform(i, 0);
            double elevation = elevationProfile.get().elevationAt(pointWorld.getX());
            Point2D pointScreen = worldToScreen.get().transform(0, elevation);
            toAdd.add(i);
            toAdd.add(pointScreen.getY());
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
        Affine transfo = new Affine();
        transfo.prependTranslation(-rectangle.get().getMinX(), -rectangle.get().getMinY());
        transfo.prependScale(elevationProfile.get().length() / rectangle.get().getWidth(),
                -(elevationProfile.get().maxElevation() - elevationProfile.get().minElevation()) /
                        rectangle.get().getHeight());
        transfo.prependTranslation(0, elevationProfile.get().maxElevation());
        screenToWorld.set(transfo);
        try {
            worldToScreen.set(transfo.createInverse());
        } catch (NonInvertibleTransformException e) {
            System.out.println(" Transformation not invertible");
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
     * This method is used to set the highlighted position.
     * @param pos position to highlight.
     */
    private void setHighlightPosition(double pos) {
        highlightPosition = new SimpleDoubleProperty(worldToScreen.get().transform(pos, 0).getX());
    }

    /**
     * This method returns us the mouse position on the profile.
     * @return a property containing the mouse position.
     */
    public ReadOnlyObjectProperty<Double> mousePositionOnProfileProperty(){
        return mousePositionOnProfileProperty;
    }

}