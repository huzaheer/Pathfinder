import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
public class GraphDemo {
    public static void main(String[] args) throws FileNotFoundException, Exception {
        GraphProcessor y = new GraphProcessor();
        FileInputStream pls = new FileInputStream("./data/usa.graph");
        y.initialize(pls);
        HashMap<String, Point> cities = new HashMap<>(18);
        create(cities);
        Scanner input = new Scanner(System.in);
        System.out.println("Where are you travelling from?");
        System.out.println("Enter state name and abbreviation (in the format 'New York NY')");
        String key = input.nextLine();
        Point starter = cities.get(key);
        System.out.println("Where are you travelling to?");
        System.out.println("Enter state name and abbreviation (in the format 'New York NY')");
        Point ender = cities.get(input.nextLine());
        Visualize artist = new Visualize("./data/usa.vis", "./images/usa.png");
        long startTime = System.nanoTime();
        Point first_intersect = y.nearestPoint(starter);
        System.out.println("Get on the highway at:" + first_intersect.toString());
        Point last_intersect = y.nearestPoint(ender);
        System.out.println("Get off the highway at:" + last_intersect.toString());
        List<Point> returner = y.route(first_intersect, last_intersect);
        System.out.println("Total Distance of your journey will be:" + " " + (int) y.routeDistance(returner) + " miles");
        long elapsedNanos = System.nanoTime() - startTime;
        System.out.println("Total Time taken to run program:" + " " + elapsedNanos/1E6 + "ms");
        artist.drawRoute(returner);
        input.close();
    }

    private static void create(HashMap<String, Point> cities) throws FileNotFoundException {
        FileInputStream hey = new FileInputStream("./data/uscities.csv");
        Scanner Reader = new Scanner(hey);
        String line = Reader.nextLine();
        String[] lmao = line.split(",");
        cities.put(lmao[0].substring(1, lmao[0].length()) + " " + lmao[1], new Point(Double.parseDouble(lmao[2]), Double.parseDouble(lmao[3])));
        while (Reader.hasNextLine()){
        line = Reader.nextLine();
        lmao = line.split(",");
        cities.put(lmao[0] + " " + lmao[1], new Point(Double.parseDouble(lmao[2]), Double.parseDouble(lmao[3])));
        }
        Reader.close();
    }
}