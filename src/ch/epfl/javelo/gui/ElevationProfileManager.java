package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;


public final class ElevationProfileManager {

    private ObjectProperty<ElevationProfile> elevationProfile;
    private ReadOnlyDoubleProperty highlightPosition;

    private BorderPane borderPane;
    private Insets insets;

    private Pane pane;//oui ou non toute celles la
    private VBox vbox;
    private Path path;
    private Group textGroup;
    private Polygon profileGraph;
    private Line highlightedPosition;
    private Text profileStats;

    private ObjectProperty<Rectangle2D> rectangle;
    private ObjectProperty<Transform> screenToWorld;
    private ObjectProperty<Transform> worldToScreen;


    /*
    private Point2D maxElevation = new Point2D(0,elevationProfile.get().maxElevation());

    private Point2D minElevation = new Point2D(elevationProfile.get().length(),elevationProfile.get().minElevation());

    private Point2D elevationAtStart = new Point2D(0,elevationProfile.get().elevationAt(0));
    private Point2D elevationAtEnd = new Point2D(elevationProfile.get().length(),elevationProfile.get().elevationAt(elevationProfile.get().length()));

    private double scaleX = elevationProfile.get().length()/borderPane.getWidth();
    private double scaleY = elevationProfile.get().maxElevation()/borderPane.getHeight();

    private double paneToBorderTranslationX = -40;
    private double paneToBorderTranslationY = -20;

    private Affine screenToWorld = new Affine();

     */




    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty position) {
        this.elevationProfile = elevationProfile;
        this.highlightPosition = position;

        //screenToWorld.prependScale(paneToBorderTranslationX,paneToBorderTranslationY);
        //screenToWorld.prependScale(scaleX,scaleY);




        textGroup = new Group();

        highlightedPosition = new Line();

        profileStats = new Text();

        path = new Path();
        path.setId("grid");

        profileGraph = new Polygon();
        profileGraph.setId("profile");

        Text horizontalGridLabel = new Text();
        horizontalGridLabel.getStyleClass().addAll("grid_label", "horizontal");

        Text verticalGridLabel = new Text();
        verticalGridLabel.getStyleClass().addAll("grid_label", "vertical");

        textGroup.getChildren().addAll(horizontalGridLabel, verticalGridLabel);

        pane = new Pane(path, textGroup, profileGraph, highlightedPosition);

        vbox = new VBox(profileStats);
        vbox.setId("profile_date");

        Text vboxText = new Text();
        vbox.getChildren().add(vboxText);

        borderPane = new BorderPane(pane, null, null, vbox, null);
        borderPane.getStyleClass().add("elevation_profile.css");
        insets = new Insets(10, 10, 20, 40);

        rectangle.set(new Rectangle2D(insets.getLeft(), insets.getBottom(),
                pane.getWidth()- insets.getRight()- insets.getLeft(),
                pane.getHeight()- insets.getTop()-insets.getBottom()));

        borderPane.setOnMousePressed(e->{
            System.out.println(e.getX()-40);
            System.out.println(pane.getHeight()+10-e.getY());
            System.out.println();
        });

        //pane.setOnMouseMoved(); // pas sur
        //pane.setOnMouseExited();

        //41.0
        //252.0
        //242.0

        createTransfo();
// listener pour create transfo
    }

    private void createTransfo() {// signe peut etre
        Affine transfo = new Affine();
        transfo.prependTranslation(-insets.getLeft(), -insets.getTop());
        transfo.prependScale(elevationProfile.get().length()/ rectangle.get().getWidth(),
                elevationProfile.get().maxElevation()-elevationProfile.get().minElevation()/ rectangle.get().getHeight());
        transfo.prependTranslation(0,elevationProfile.get().maxElevation());
        screenToWorld.set(transfo);
        try{
            worldToScreen.set(transfo.createInverse());
        }catch(NonInvertibleTransformException e){
            System.out.println(" Transformation not invertible");
        }
    }


    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile() {
        return elevationProfile;
    }

    public double getHighlightPosition() {
        return highlightPosition.doubleValue();
    }

    public BorderPane pane(){
        return borderPane;
    }

    public ReadOnlyObjectProperty<Double> mousePositionOnProfileProperty(){
        return null;
    }

}