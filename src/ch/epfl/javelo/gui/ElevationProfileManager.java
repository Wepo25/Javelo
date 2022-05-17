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


public final class ElevationProfileManager {

    private static final int[] POS_STEPS = {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private static final int[] ELE_STEPS = {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

    private final static Insets insets = new Insets(10, 10, 20, 40);

    private static final double TOP_INSET = insets.getTop();
    private static final double BOTTOM_INSET = insets.getBottom();
    private static final double LEFT_INSET = insets.getLeft();
    private static final double RIGHT_INSET = insets.getRight();
    private static final double WIDTH_INSET = LEFT_INSET + RIGHT_INSET;
    private static final double HEIGHT_INSET = TOP_INSET + BOTTOM_INSET;






    private final ObjectProperty<ElevationProfile> elevationProfile;
    private ReadOnlyDoubleProperty highlightPosition;
    private ReadOnlyDoubleProperty highlightPositionOnScreen;

    private final DoubleProperty mousePositionOnProfileProperty = new SimpleDoubleProperty();




    private final Path path;
    private final Group textGroup;
    private final Polygon profileGraph;
    private final Line highlightedPosition;
    private final Pane pane;



    private final Text profileStats;
    private final VBox vbox;

    private final BorderPane borderPane;

    private final ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>();

    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty position) {

        this.elevationProfile = elevationProfile;
        this.highlightPosition = position;

        textGroup = new Group();

        highlightedPosition = new Line();

        profileStats = new Text();

        path = new Path();
        path.setId("grid");

        profileGraph = new Polygon();
        profileGraph.setId("profile");

        Text horizontalGridLabel = new Text();
        horizontalGridLabel.getStyleClass().setAll("grid_label", "horizontal");

        Text verticalGridLabel = new Text();
        verticalGridLabel.getStyleClass().setAll("grid_label", "vertical");

        textGroup.getChildren().setAll(horizontalGridLabel, verticalGridLabel);

        pane = new Pane(path, textGroup, profileGraph, highlightedPosition);

        vbox = new VBox(profileStats);
        vbox.setId("profile_date");

        Text vboxText = new Text();
        vbox.getChildren().add(vboxText);

        borderPane = new BorderPane(pane, null, null, vbox, null);
        borderPane.getStylesheets().setAll("elevation_profile.css");

        pane.widthProperty().addListener(l-> operationsSequence());
        pane.heightProperty().addListener(l-> operationsSequence());

        rectangle.bind(Bindings.createObjectBinding(() -> new Rectangle2D(LEFT_INSET, TOP_INSET,
                Math.max(0,pane.getWidth()-WIDTH_INSET),
                Math.max(0, pane.getHeight()-HEIGHT_INSET)),pane.widthProperty(), pane.heightProperty()
        ));



        borderPane.setOnMouseMoved(e -> {
            if(e.getX() <= rectangle.get().getMaxX() && e.getX() >= rectangle.get().getMinX()
                    && e.getY() <= rectangle.get().getMaxX() && e.getY() >= rectangle.get().getMinX()){
                mousePositionOnProfileProperty.setValue(e.getX());
            }
        });
        borderPane.setOnMouseExited(event -> mousePositionOnProfileProperty.setValue(Double.NaN));

        elevationProfile.addListener(e -> operationsSequence());
    }

    public BorderPane pane(){
        return borderPane;
    }

    private void operationsSequence(){
        if(elevationProfile.get() != null) {
            createTransformation();
            line();
            textGroup.getChildren().clear();
            path.getElements().clear();
            createGrid();
            createProfile();
        }

    }

    private void createGrid(){

        int horizontalSpace = createHorizontalSpace();
        int verticalSpace = createVerticalSpace();
        int horizontalIndex = 0;
        int firstLine = Math2.ceilDiv((int) elevationProfile.get().minElevation(), horizontalSpace)*horizontalSpace;

        while(horizontalIndex * horizontalSpace + firstLine < elevationProfile.get().maxElevation()){
            Point2D fromPoint = worldToScreen.get().transform(0, horizontalIndex * horizontalSpace + firstLine);
            Point2D toPoint = worldToScreen.get().transform(elevationProfile.get().length(), horizontalIndex * horizontalSpace + firstLine);
            path.getElements().addAll(new MoveTo(fromPoint.getX(), fromPoint.getY()), new LineTo(toPoint.getX(), toPoint.getY()));
            createHorizontalLabel(fromPoint.getX(), fromPoint.getY(), String.valueOf(horizontalIndex * horizontalSpace + firstLine ));
            horizontalIndex++;
        }

        int verticalIndex = 0;
        while(verticalSpace * verticalIndex < elevationProfile.get().length()){
            Point2D fromPoint = worldToScreen.get().transform(verticalSpace*verticalIndex,elevationProfile.get().minElevation());
            Point2D toPoint = worldToScreen.get().transform(verticalSpace*verticalIndex,elevationProfile.get().maxElevation());
            path.getElements().addAll(new MoveTo(fromPoint.getX() ,fromPoint.getY()), new LineTo(toPoint.getX(), toPoint.getY()));
            createVerticalLabel(fromPoint.getX(), fromPoint.getY(), String.valueOf(verticalIndex));
            verticalIndex++;
        }

    }

    private int createHorizontalSpace(){
        int horizontalSpace = 0;
        for (int eleStep : ELE_STEPS) {
            if (worldToScreen.get().deltaTransform(0, -eleStep).getY() >= 25) {
                horizontalSpace = eleStep;
                break;
            }
        }
        return (horizontalSpace == 0)? ELE_STEPS[ELE_STEPS.length-1] : horizontalSpace;
    }

    private int createVerticalSpace(){
        int verticalSpace = 0;
        for (int posStep : POS_STEPS) {
            if (worldToScreen.get().deltaTransform(posStep, 0).getX() >= 50) {
                verticalSpace = posStep;
                break;
            }
        }
        return (verticalSpace == 0)? POS_STEPS[POS_STEPS.length-1] : verticalSpace;
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
        label.setY(y);
        textGroup.getChildren().add(label);
    }

    private void line(){
        highlightedPosition.startXProperty().bind(new SimpleDoubleProperty(worldToScreen.get().deltaTransform(highlightPosition.get(),0).getX()+LEFT_INSET));
        highlightedPosition.endXProperty().bind(new SimpleDoubleProperty(worldToScreen.get().deltaTransform(highlightPosition.get(),0).getX()+LEFT_INSET));
        highlightedPosition.startYProperty().bind(new SimpleDoubleProperty(TOP_INSET));
        highlightedPosition.endYProperty().bind(new SimpleDoubleProperty(rectangle.get().getHeight()+TOP_INSET));
        highlightedPosition.visibleProperty().bind(new SimpleBooleanProperty(highlightPosition.get()>=0));
    }

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

    private void createTransformation() {
        Affine transformation = new Affine();
        transformation.prependTranslation(-rectangle.get().getMinX(), -rectangle.get().getMinY());
        transformation.prependScale(elevationProfile.get().length()/ rectangle.get().getWidth(),
                -(elevationProfile.get().maxElevation()-elevationProfile.get().minElevation())/
                        rectangle.get().getHeight());
        transformation.prependTranslation(0,elevationProfile.get().maxElevation());
        screenToWorld.set(transformation);
        try{
            worldToScreen.set(transformation.createInverse());
        } catch(NonInvertibleTransformException e){
            System.out.println(" Transformation not invertible");
        }
    }

}