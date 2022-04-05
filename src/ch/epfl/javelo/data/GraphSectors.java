package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.Math2.clamp;

/**
 * Record GraphSector which represents all the sectors contained in the Switzerland cartography.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 * @param buffer - ByteBuffer : buffer containing the graph's information.
 */
public record GraphSectors(ByteBuffer buffer) {

    /**
     * Numbers of sector by side of the Swiss.
     */
    private static final int SECTOR_BY_SIDE = 128;

    /**
     * Offset to find the first node of a sector.
     */
    private static final int OFFSET_NODE1 = 0;

    /**
     * Offset to find the length of a sector
     */
    private static final int OFFSET_LENGTH = OFFSET_NODE1 + Integer.BYTES;

    /**
     * The number of elements in the buffer used to represent a sector.
     */
    private static final int SECTOR_INTS = OFFSET_LENGTH + Short.BYTES;

    /**
     * Width of a sector.
     */
    private static final double SectorsLength = SwissBounds.WIDTH / SECTOR_BY_SIDE;

    /**
     * Height of a sector.
     */
    private static final double SectorsHeight = SwissBounds.HEIGHT / SECTOR_BY_SIDE;

    /**
     * Find all Sectors contained in a squared Area.
     *
     * @param center - PointCh :  the center of the square.
     * @param distance - double : the distance from the center to the side of a square.
     * @return a list containing all sectors includes into the square ( dimensions distance*2 and centered on center).
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        if (distance == 0) {
            distance = 1;
        }

        double XMax = clamp(SwissBounds.MIN_E, center.e() + distance, SwissBounds.MAX_E);
        double XMin = clamp(SwissBounds.MIN_E, center.e() - distance, SwissBounds.MAX_E);
        double YMax = clamp(SwissBounds.MIN_N, center.n() + distance, SwissBounds.MAX_N);
        double YMin = clamp(SwissBounds.MIN_N, center.n() - distance, SwissBounds.MAX_N);

        int firstSectorAbs = (int) ((XMin - SwissBounds.MIN_E) / SectorsLength);
        int firstSectorOrd = (int) ((YMin - SwissBounds.MIN_N) / SectorsHeight);
        int lastSectorAbs = (int) Math.ceil((XMax - SwissBounds.MIN_E) / SectorsLength - 1);
        int lastSectorOrd = (int) Math.ceil((YMax - SwissBounds.MIN_N) / SectorsHeight - 1);

        List<Sector> list = new ArrayList<>();

        for (int i = firstSectorOrd; i <= lastSectorOrd; i++) {
            for (int j = firstSectorAbs; j <= lastSectorAbs; j++) {
                int sectorIn = i * SECTOR_BY_SIDE + j;
                int node1 = buffer.getInt(sectorIn * SECTOR_INTS + OFFSET_NODE1);
                int length = Short.toUnsignedInt(buffer.getShort(sectorIn * SECTOR_INTS + OFFSET_LENGTH));
                list.add(new Sector(node1, node1 + length));
            }
        }
        return list;

    }

    /**
     * Record of a sector with the first node and the one after the last node in attributes.
     * @param startNodeId - int : Identity of the first node.
     * @param endNodeId - int : Identity of the node after the last of the sector.
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}
