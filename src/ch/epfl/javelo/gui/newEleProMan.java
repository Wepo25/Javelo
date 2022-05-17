package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public final class newEleProMan {

    private static final int[] POS_STEPS = {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private static final int[] ELE_STEPS = {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

    private final ObjectProperty<ElevationProfile> elevationProfile;
    private ReadOnlyDoubleProperty highlightPosition;

    private final Insets insets = new Insets(10, 10, 20, 40);

    private final Path path = new Path();
    private final Group textGroup = new Group();
    private final Polygon profileGraph = new Polygon();
    private final Line highlightedPosition = new Line();
    private final Pane pane = new Pane(path, textGroup, profileGraph, highlightedPosition);


    private final Text profileStats = new Text();
    private final VBox vbox = new VBox(profileStats);

    private final BorderPane borderPane = new BorderPane(pane, null, null, vbox, null);

    private final ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>();


    public newEleProMan(ObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty position) {

        this.elevationProfile = elevationProfile;

        path.setId("grid");
        profileGraph.setId("profile");
        vbox.setId("profile_date");
        borderPane.getStyleClass().add("elevation_profile.css");

        borderPane.widthProperty().addListener(l-> operationsSequence(position));
        borderPane.heightProperty().addListener(l-> operationsSequence(position));
        BorderPane.setMargin(pane,insets);


        borderPane.setOnMousePressed(e -> {
        });
    }

    public BorderPane pane(){
        return borderPane;
    }

    private void operationsSequence(ReadOnlyDoubleProperty pos){
        createRectangle();
        createTransformation();
        line(pos);
        path.getElements().clear();
        createGrid();
    }

    private void createGrid(){
        double horizontalSpace = 0;
        double verticalSpace = 0;
        for (int i = 0; i < ELE_STEPS.length; i++) {
            if(worldToScreen.get().transform(0,ELE_STEPS[i]).getY()>=25){
                horizontalSpace = ELE_STEPS[i];
                break;
            }
        }
        for (int i = 0; i < POS_STEPS.length; i++) {
            if(worldToScreen.get().transform(POS_STEPS[i],0).getX()>=50){
                verticalSpace = POS_STEPS[i];
                break;
            }
        }
        System.out.println(verticalSpace);
        System.out.println(horizontalSpace);
        System.out.println(worldToScreen.get().transform(horizontalSpace,0).getX());

        int horizontalIndex = (int) (pane.getHeight()/worldToScreen.get().transform(horizontalSpace,0).getX());
        while(0 < horizontalIndex){
            System.out.println(horizontalIndex);
            PathElement moveTo = new MoveTo(0,horizontalIndex * worldToScreen.get().transform(horizontalSpace,0).getX());
            PathElement lineTo = new LineTo(pane.getWidth(), horizontalIndex * worldToScreen.get().transform(horizontalSpace,0).getX());
            path.getElements().add(moveTo);
            path.getElements().add(lineTo);
            horizontalIndex--;
        }
        PathElement moveTo2 = new MoveTo(0,pane.getHeight());
        PathElement lineTo2 = new LineTo(pane.getWidth(), pane.getHeight());
        path.getElements().add(moveTo2);
        path.getElements().add(lineTo2);
        int verticalIndex = 0;
        while(worldToScreen.get().transform(verticalSpace,0).getX() * verticalIndex < pane.getWidth()){
            PathElement moveTo = new MoveTo(worldToScreen.get().transform(verticalSpace,0).getX() * verticalIndex,0);
            PathElement lineTo = new LineTo(worldToScreen.get().transform(verticalSpace,0).getX() * verticalIndex, pane.getHeight());
            path.getElements().add(moveTo);
            path.getElements().add(lineTo);
            verticalIndex++;
            createVerticalLabel(verticalIndex * worldToScreen.get().transform(verticalSpace,0).getX(), pane.getHeight(), String.valueOf(verticalIndex));
        }

    }

    private void createVerticalLabel(double x, double y, String s){
        Text label = new Text();
        label.setText(s);
        label.setX(x-label.prefWidth(0));
        label.setY(y);
        label.getStyleClass().addAll("grid_label", "vertical");
        label.setFont(Font.font("Avenir", 10));
        textGroup.getChildren().add(label);
    }

    private void createHorizontalLabel(double x, double y, String s){
        Text label = new Text();
        label.setText(s);
        label.setX(x-label.prefWidth(0));
        label.setY(y);
        label.getStyleClass().addAll("grid_label", "horizontal");
        label.setFont(Font.font("Avenir", 10));
        textGroup.getChildren().add(label);
    }

    private void line(ReadOnlyDoubleProperty pos){
        setHighlightPosition(pos.get());
        highlightedPosition.layoutXProperty().bind(highlightPosition);
        highlightedPosition.startYProperty().bind(new SimpleDoubleProperty(0));
        highlightedPosition.endYProperty().bind(new SimpleDoubleProperty(pane.getHeight()));
        highlightedPosition.visibleProperty().bind(new SimpleBooleanProperty(highlightPosition.get()>=0));
    }

    private void createRectangle(){
        rectangle.set(new Rectangle2D(insets.getLeft(), insets.getTop(),
                Math.max(0,borderPane.getWidth()-insets.getLeft()-insets.getRight()),
                Math.max(0, borderPane.getHeight()-insets.getTop()-insets.getBottom())));
    }

    private void createTransformation() {
        Affine transformation = new Affine();
        transformation.prependTranslation(-insets.getLeft(), -insets.getTop());
        transformation.prependScale(Math.round(elevationProfile.get().length()/ rectangle.get().getWidth()),
                Math.round(elevationProfile.get().maxElevation()-elevationProfile.get().minElevation()/ rectangle.get().getHeight()));
        transformation.prependTranslation(0,elevationProfile.get().maxElevation());
        screenToWorld.set(transformation);
        try{
            worldToScreen.set(transformation.createInverse());
        }catch(NonInvertibleTransformException e){
            System.out.println(" Transformation not invertible");
        }
    }

    private void setHighlightPosition(double pos){
        highlightPosition = new SimpleDoubleProperty(worldToScreen.get().transform(pos,0).getX());
    }
}

