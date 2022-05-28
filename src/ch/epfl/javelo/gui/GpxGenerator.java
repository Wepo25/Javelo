package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class GpxGenerator {

    private static final String HEAD_NAME = "Route JaVelo";

    private GpxGenerator() {
    }

    public static Document createGpx(Route route, ElevationProfile profile) {
        Document doc = newDocument();
        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent(HEAD_NAME);

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        double position = 0;
        List<PointCh> points = route.points();
        for (int i = 0; i < points.size(); i++) {
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", String.valueOf(Math.toDegrees(points.get(i).lat())));
            rtept.setAttribute("lon", String.valueOf(Math.toDegrees(points.get(i).lon())));
            Element ele = doc.createElement("ele");
            if (i != 0) position += points.get(i).distanceTo(points.get(i - 1));
            ele.setTextContent(String.valueOf(profile.elevationAt(position)));
            rtept.appendChild(ele);
            rte.appendChild(rtept);
        }
        return doc;
    }

    public static void writeGpx(String fileName, Route route, ElevationProfile profile)
            throws IOException{
        Document doc = createGpx(route, profile);
        Writer w = new FileWriter(fileName);
        try {
            Transformer transformer = null;
            transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        }
        catch(TransformerException e){
            throw new Error(e);
        }


    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}