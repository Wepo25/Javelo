package ch.epfl.javelo.gui;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static ch.epfl.javelo.gui.TileManager.TileId.isValid;
import javafx.scene.image.Image;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;


/**
 * This method allows us to manage our Tiles representing our map. It can download and stock the Tile into
 * a cache memory and a cache disk.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class TileManager {

    /**
     * Maximum memory for the LinkedHashMap representing the cache-memory.
     */
    private static final int MAX_CAP_MEMORY = 100;

    /**
     * Load Factor of the LinkedHashMap representing the cache-memory.
     */
    private static final float LOAD_FACTOR = 0.75f;
    /**
     * Minimum value for a Tile coordinate.
     */
    private static final int MIN_VALUE_TILE_COORDINATE = 0;
    /**
     * String specifying the file extension.
     */
    private static final String FILE_EXTENSION = ".png";

    private final Path path;
    private final String serv;
    private final LinkedHashMap<TileId, Image> cacheMemory;


    /**
     * Constructor of TileManager with the disk cache's path and the server hosting the Tiles online.
     *
     * @param path path leading to the disk cache.
     * @param serv string representing the server name.
     */
    public TileManager(Path path, String serv) {
        this.cacheMemory = new LinkedHashMap<>(MAX_CAP_MEMORY, LOAD_FACTOR, true);
        this.path = path;
        this.serv = serv;
    }

    /**
     * This method allows us to get a Tile (format Image) from a TileId. And register it if it's not done yet.
     *
     * @param tileId tileId from the Tile to get.
     * @return Image representing the Tile.
     * @throws IOException if the TileId is not valid.
     */
    public Image imageForTileAt(TileId tileId) throws IOException {

        checkArgument(isValid(tileId.zoomLevel, tileId.xTile, tileId.yTile));

        Path fullPath = path.
                resolve(String.valueOf(tileId.zoomLevel)).
                resolve(String.valueOf(tileId.xTile)).
                resolve(tileId.yTile + FILE_EXTENSION);

        if (!cacheMemory.containsKey(tileId)) {
            if (!Files.exists(fullPath)) {
                diskTileRegister(tileId, fullPath);
            }
            memoryTileRegister(tileId, fullPath);
        }
        return cacheMemory.get(tileId);
    }

    /**
     * Register the tile into the cache memory.
     */
    private void memoryTileRegister(TileId tileId, Path fullPath) throws IOException {
        try (InputStream i = new FileInputStream(fullPath.toString())) {
            Image newImage = new Image(i);
            if (cacheMemory.size() == MAX_CAP_MEMORY) {
                cacheMemory.remove(cacheMemory.keySet().iterator().next());
            }
            cacheMemory.put(tileId, newImage);
        }
    }

    /**
     * Register the tile into the cache disk.
     */
    private void diskTileRegister(TileId tileId, Path fullPath) throws IOException {
        Files.createDirectories(fullPath.getParent());
        URL u = new URL("https://" +
                serv + "/" +
                tileId.zoomLevel + "/" +
                tileId.xTile + "/" +
                tileId.yTile + FILE_EXTENSION);
        URLConnection urlConnection = u.openConnection();
        urlConnection.setRequestProperty("User-Agent", "JaVelo");

        try (InputStream i = urlConnection.getInputStream();
             OutputStream t = new FileOutputStream(fullPath.toString())) {
            i.transferTo(t);
        }
    }

    /**
     * This record represent a TileId useful to identify a Tile thanks to zoomLevel, x and y coordinate.
     *
     * @param zoomLevel the zoomLevel.
     * @param xTile     the x coordinate of the Tile.
     * @param yTile     the y coordinate of the Tile.
     */
    public record TileId(int zoomLevel, int xTile, int yTile) {

        public TileId {
            checkArgument(isValid(zoomLevel, xTile, yTile));
        }

        /**
         * This static method allow us to check if whether the Tile coordinates are valid.
         *
         * @param zoom of the TileId.
         * @param x    coordinate x of the TileId.
         * @param y    coordinate y of the TileId.
         * @return a boolean on the validity of the arguments.
         */
        public static boolean isValid(int zoom, int x, int y) {
            return !((x > (1 << zoom)) || y > (1 << zoom))
                    && (x > MIN_VALUE_TILE_COORDINATE) && (y > MIN_VALUE_TILE_COORDINATE);
        }
    }
}