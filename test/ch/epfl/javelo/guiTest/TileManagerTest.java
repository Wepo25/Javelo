package ch.epfl.javelo.guiTest;

import ch.epfl.javelo.gui.TileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;

public class TileManagerTest extends Application {
        public static void main(String[] args) { launch(args); }

        @Override
        public void start(Stage primaryStage) throws Exception {
            TileManager tm = new TileManager(
                    Path.of("."), "tile.openstreetmap.org");
            Image tileImage = tm.imageForTileAt(
                    new TileManager.TileId(19, 271725, 185422));
            for (int i = 185422; i <185422+100; i++) {
            Image tileImage1 = tm.imageForTileAt(
                    new TileManager.TileId(19, 271765,i ));
            }
            Platform.exit();
        }
    }

