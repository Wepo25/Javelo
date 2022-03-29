package ch.epfl.javelo.routing.RouteComputerTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class mapReader {
    public static void main(String[] args) {
        String javelo1 = analyseFile("javelo.kml");
        String javeloPerfect = analyseFile("route_159049-117669 (1).kml");
        assertEquals(javelo1, javeloPerfect);

    }

    private static String analyseFile(String pathname){
        String file = "";
        try {
            File myObj = new File(pathname);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                file += data+"\n";
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return file;
    }
}
