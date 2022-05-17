package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;

import javafx.beans.property.ObjectProperty;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";

        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        CityBikeCF costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer =
                new RouteComputer(graph, costFunction);


        RouteBean rb = new RouteBean(routeComputer);

        Consumer<String> errorConsumer = new ErrorConsumer();

        ErrorManager errorManager = new ErrorManager();

        AnnotatedMapManager amm = new AnnotatedMapManager(graph, tileManager,rb,errorManager::displayError);



        BorderPane profileBorderPane = new ElevationProfileManager((ObjectProperty<ElevationProfile>) rb.getElevationProfile(),
                rb.highlightedPositionProperty()).pane();




        SplitPane sp = new SplitPane(amm.pane());
        SplitPane.setResizableWithParent(profileBorderPane,false);
        sp.setOrientation(Orientation.VERTICAL);

        rb.getElevationProfile().addListener(e-> {
            if(rb.getElevationProfile().get() == null) {
                sp.getItems().remove(profileBorderPane);
            }
            else if(!sp.getItems().contains(profileBorderPane)){
                sp.getItems().add(profileBorderPane);
            }
        });


        MenuItem menuItem = new MenuItem("Exporter GPX");
        menuItem.disableProperty().bind(rb.getRoute().isNull());
        menuItem.setOnAction(a -> {
            System.out.println("IN");
            try {
                GpxGenerator.writeGpx("javelo.gpx",rb.getRoute().get(),rb.getElevationProfile().getValue());
            } catch (Exception e) {
                throw new UncheckedIOException(new IOException());
            }
        });
        Menu menu = new Menu("Fichier",null,menuItem);
        MenuBar menuBar = new MenuBar(menu);
        menuBar.setUseSystemMenuBar(true);

        Pane errorManagerPane = errorManager.pane();
        StackPane stackPane = new StackPane( menuBar, sp, errorManagerPane);

        stackPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(stackPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}
