package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
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
/**
 * The program's main class.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class JaVelo extends Application {

    /**
     * The path to access the files containing data from the real world.
     */
    private static final String GRAPH_PATH = "javelo-data";
    /**
     * The path to access the files containing the tiles used to draw the map.
     */
    private static final String CACHE_BASE_PATH = "osm_cache";
    /**
     * The address of the server hosting our tiles.
     */
    private static final String TILE_SERVER_HOST_ADDRESS = "tile.openstreetmap.org";
    /**
     * The title of the file we create when clicking on the menuItem.
     */
    private static final String GPX_FILE_NAME = "javelo.gpx";
    /**
     * The css style sheet used for the map.
     */
    private static final String MAIN_PANE_STYLESHEET = "map.css";
    /**
     * The label of the first menuItem.
     */
    private static final String MENU_ITEM_LABEL_1 = "Exporter GPX";
    /**
     * The label of the first menu in the menuBar.
     */
    private static final String MENU_LABEL_1 = "Fichier";
    /**
     * The title of the window containing the map and the itinerary's information.
     */
    private static final String WINDOW_TITLE = "JaVelo";
    /**
     * The width of the window.
     */
    private static final int WINDOW_WIDTH = 800;
    /**
     * The height of the window.
     */
    private static final int WINDOW_HEIGHT = 600;

    /**
     * The program's main method used to run it.
     *
     * @param args Java command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The method that starts the program and defines every necessary elements.
     *
     * @param primaryStage The stage containing the map and the itinerary's information.
     * @throws IOException Throws an error when encountering one that has not been handled.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        Graph graph = Graph.loadFrom(Path.of(GRAPH_PATH));
        Path cacheBasePath = Path.of(CACHE_BASE_PATH);
        TileManager tileManager = new TileManager(cacheBasePath, TILE_SERVER_HOST_ADDRESS);
        CityBikeCF costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(routeComputer);
        ErrorManager errorManager = new ErrorManager();

        //The map.
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorManager::displayError);

        //The profile.
        ElevationProfileManager profile = new ElevationProfileManager(
                routeBean.getElevationProfile(), routeBean.highlightedPositionProperty());

        //The pane containing the map and the profile.
        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        SplitPane.setResizableWithParent(profile.pane(), false);
        splitPane.setOrientation(Orientation.VERTICAL);
        routeBean.getElevationProfile().addListener((p, oldS, newS) -> {
            if (oldS == null && newS != null) splitPane.getItems().add(1, profile.pane());
            else if (oldS != null && newS == null) splitPane.getItems().remove(1);
        });

        //Beginning of the menu's creation process.
        MenuItem menuItem = new MenuItem(MENU_ITEM_LABEL_1);
        menuItem.disableProperty().bind(routeBean.getRoute().isNull());
        menuItem.setOnAction(a -> {
            try {
                GpxGenerator.writeGpx(GPX_FILE_NAME, routeBean.getRoute().get(),
                        routeBean.getElevationProfile().getValue());
            } catch (IOException e) {
                throw new UncheckedIOException(new IOException());
            }
        });
        Menu menu = new Menu(MENU_LABEL_1, null, menuItem);
        MenuBar menuBar = new MenuBar(menu);
        //End of the menu's creation process.

        //The pane displaying the errors.
        Pane errorManagerPane = errorManager.vbox();

        //The pane containing both the SplitPane, containing the map and the profile,
        // and the pane displaying the errors.
        StackPane stackPane = new StackPane(splitPane, errorManagerPane);

        BorderPane mainPane = new BorderPane(stackPane);
        mainPane.setTop(menuBar);
        mainPane.getStylesheets().add(MAIN_PANE_STYLESHEET);

        //Binding the shown position with the mouse's position on screen.
        routeBean.highlightedPositionProperty().bind(Bindings.when(annotatedMapManager.mousePositionOnRouteProperty()
                        .greaterThanOrEqualTo(0)).
                then(annotatedMapManager.mousePositionOnRouteProperty()).
                otherwise(profile.mousePositionOnProfileProperty()));

        //Initialization of the stage.
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.show();
    }
}
