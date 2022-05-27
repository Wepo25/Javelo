package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
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

public final class JaVelo extends Application {

    private static final String GRAPH_PATH = "javelo-data";
    private static final String CACHE_BASE_PATH = "osm_cache";
    private static final String TILE_SERVER_HOST_ADDRESS = "tile.openstreetmap.org";
    private static final String GPX_FILE_NAME = "javelo.gpx";
    private static final String MAIN_PANE_STYLESHEET = "map.css";
    private static final String MENU_ITEM_LABEL_1 = "Exporter GPX";
    private static final String MENU_LABEL_1 = "Fichier";
    private static final String WINDOW_TITLE = "JaVelo";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of(GRAPH_PATH));

        Path cacheBasePath = Path.of(CACHE_BASE_PATH);

        TileManager tileManager =
                new TileManager(cacheBasePath, TILE_SERVER_HOST_ADDRESS);

        CityBikeCF costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer =
                new RouteComputer(graph, costFunction);

        RouteBean routeBean = new RouteBean(routeComputer);

        ErrorManager errorManager = new ErrorManager();

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorManager::displayError);


        ElevationProfileManager profileBorderPane =
                new ElevationProfileManager((ObjectProperty<ElevationProfile>) routeBean.getElevationProfile(),
                        routeBean.highlightedPositionProperty());


        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        SplitPane.setResizableWithParent(profileBorderPane.pane(), false);
        splitPane.setOrientation(Orientation.VERTICAL);

        routeBean.getElevationProfile().addListener((p, oldS, newS) -> {
            if (oldS == null && newS != null) splitPane.getItems().add(1, profileBorderPane.pane());
            else if (oldS != null && newS == null) splitPane.getItems().remove(1);
        });

        MenuItem menuItem = new MenuItem(MENU_ITEM_LABEL_1);
        menuItem.disableProperty().bind(routeBean.getRoute().isNull());
        menuItem.setOnAction(a -> {
            try {
                GpxGenerator.writeGpx(GPX_FILE_NAME, routeBean.getRoute().get(), routeBean.getElevationProfile().getValue());
            } catch (Exception e) {
                throw new UncheckedIOException(new IOException());
            }
        });
        Menu menu = new Menu(MENU_LABEL_1, null, menuItem);
        MenuBar menuBar = new MenuBar(menu);
        menuBar.setUseSystemMenuBar(true);

        Pane errorManagerPane = errorManager.pane();
        StackPane stackPane = new StackPane(splitPane, errorManagerPane);

        BorderPane mainPane = new BorderPane(stackPane);
        mainPane.setTop(menuBar);

        mainPane.getStylesheets().add(MAIN_PANE_STYLESHEET);

        routeBean.highlightedPositionProperty().bind(Bindings.when(annotatedMapManager.mousePositionOnRouteProperty()
                        .greaterThanOrEqualTo(0)).
                then(annotatedMapManager.mousePositionOnRouteProperty()).
                otherwise(profileBorderPane.mousePositionOnProfileProperty()));


        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.show();
    }

}
