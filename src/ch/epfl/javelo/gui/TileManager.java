package ch.epfl.javelo.gui;


import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static ch.epfl.javelo.gui.TileManager.TileId.isValid;

public final class TileManager {
    private static final int MAX_CAP_MEMORY = 100;
    private final Path path;
    private final String serv;
    private final LinkedHashMap<TileId, Image> cacheMemory =
            new LinkedHashMap<>(MAX_CAP_MEMORY, 1, true);

    public TileManager(Path path, String serv) {
        this.path = path;
        this.serv = serv;

    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        Preconditions.checkArgument(isValid(tileId.zoomLevel, tileId.xTile, tileId.yTile));

        String partialPath = "/" + tileId.zoomLevel + "/" + tileId.xTile + "/" + tileId.yTile + ".png";
        if (cacheMemory.containsKey(tileId)) {
            return cacheMemory.get(tileId);
        }
        Path fullPath = Path.of(path + partialPath);
        if (Files.exists(fullPath)) {
            try (FileInputStream accessImage = new FileInputStream(fullPath.toString())) {
                return new Image(accessImage);
            }
        } else {
            Files.createDirectories(Path.of(path + "/" + tileId.zoomLevel + "/" + tileId.xTile));
            URL u = new URL(
                    "https://" + serv + partialPath);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            try (InputStream i = c.getInputStream();
                 OutputStream t = new FileOutputStream(fullPath.toString())) {
                //create the directorie and add the image created thanks to the url.
                i.transferTo(t);
                Image newImage = new Image(i);
                int counter = 0;
                if (cacheMemory.size() == MAX_CAP_MEMORY) {
                    for (Map.Entry<TileId, Image> e : cacheMemory.entrySet()) {
                        ++counter;
                        if (counter == MAX_CAP_MEMORY)
                            cacheMemory.remove(e.getKey());
                    }

                }cacheMemory.put(tileId, newImage);
                return newImage;
            }
        }


    }

    public record TileId(int zoomLevel, int xTile, int yTile) {

        public static boolean isValid(int zoom, int x, int y) {
            return !(x > Math.scalb(2, zoom) || y > Math.scalb(2, zoom));
        }
    }
}
