import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

import org.junit.rules.DisableOnDebug;

import java.io.File;
import java.io.FileInputStream;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    private HashMap<String, Point> og_points = new HashMap<>();
    private HashMap<Point, HashSet<Point>> connector = new HashMap<>();
    private ArrayList<String> names = new ArrayList<>();

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */
    public void initialize(FileInputStream file) throws Exception{
        Scanner Reader = new Scanner(file);
        String line = Reader.nextLine();
        String[] lmao = line.split(" ");

        for (int count = 0; count < Integer.parseInt(lmao[0]); count++){
            String[] point;
            if (!(Reader.hasNextLine())){
                break;
            }
            point = Reader.nextLine().split(" ");
            og_points.putIfAbsent(point[0], new Point(Double.parseDouble(point[1]), Double.parseDouble(point[2])));
            og_points.put(point[0], new Point(Double.parseDouble(point[1]), Double.parseDouble(point[2])));
            names.add(point[0]);
        }

        for (int count = 0; count < Integer.parseInt(lmao[1]); count++){
            String[] edge;
            if (!(Reader.hasNextLine())){
                break;
            }
            edge = Reader.nextLine().split(" ");
            // Add all edges
            connector.putIfAbsent(og_points.get(names.get(Integer.parseInt(edge[0]))), new HashSet<Point>());
            connector.get(og_points.get(names.get(Integer.parseInt(edge[0])))).add(og_points.get(names.get(Integer.parseInt(edge[1]))));

            connector.putIfAbsent(og_points.get(names.get(Integer.parseInt(edge[1]))), new HashSet<Point>());
            connector.get(og_points.get(names.get(Integer.parseInt(edge[1])))).add(og_points.get(names.get(Integer.parseInt(edge[0]))));
        }
        Reader.close();
    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        Point nearest = new Point(0, 0);
        double nearestdis = 0;
        
        for (String y: og_points.keySet()){                 // initializes nearest dis 
            nearestdis = og_points.get(y).distance(p);
            nearest = og_points.get(y);
            break;
        }

        for (String y: og_points.keySet()){
            double xd = og_points.get(y).distance(p);
            if (og_points.get(y).distance(p) < nearestdis){
                    nearest = og_points.get(y);
                    nearestdis = og_points.get(y).distance(p);
            }
        }
        return nearest;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        Double total_dist = 0.0;
        for (int p = 1; p < route.size(); p++){
            total_dist += route.get(p).distance(route.get(p-1));
        }
        return total_dist;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        Stack<Point> lmao = new Stack<>();
        HashSet<Point> visited = new HashSet<>();
        Point start = p1;
        lmao.push(start);
        visited.add(start);

        if (!connector.containsKey(p1) || !connector.containsKey(p2)) return false;
        while (!lmao.isEmpty()){
            Point current = lmao.pop();
            if (current.equals(p2)) return true;
            for (Point neighbor: connector.get(current)){
                if (!(visited.contains(neighbor))){
                    lmao.push(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return false;
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        HashMap<Point, Double> dist = new HashMap<>();
        //populate(dist);
        Comparator<Point> c = (a,b) -> roundUp(dist.get(a) - dist.get(b));
        PriorityQueue<Point> lmao = new PriorityQueue<>(c);
        HashMap<Point, Point> path = new HashMap<>();
        HashSet<Point> visited = new HashSet<>();
        ArrayList<Point> reversed_final_path = new ArrayList<>();
        ArrayList<Point> final_path = new ArrayList<>();

        lmao.add(start);
        visited.add(start);
        dist.put(start, 0.0);

        if (!connector.containsKey(start) || !connector.containsKey(end) || start.equals(end)) throw new InvalidAlgorithmParameterException("No path between start and end");
        while (!lmao.isEmpty()){
            Point current = lmao.remove();
            if (current == end){
                break;
            }
            for (Point neighbor: connector.get(current)){
                if (!visited.contains(neighbor) || dist.get(neighbor) > dist.get(current) + current.distance(neighbor)){
                    dist.put(neighbor, dist.get(current) + current.distance(neighbor));
                    lmao.add(neighbor);
                    visited.add(neighbor);
                    path.put(neighbor, current);
                }
            }
        }
        reversed_final_path.add(0, end);

        for (Point p: path.keySet()){
            end = path.get(end);
            reversed_final_path.add(end);
            if (end == start){
                int size = reversed_final_path.size();
                for (int k = 1; k <= reversed_final_path.size(); k++){
                    final_path.add(reversed_final_path.get(size-k));
                }
                return final_path;
            }
        }
        throw new InvalidAlgorithmParameterException("No path between start and end");
    }

    /*private void populate(HashMap<Point, Double> dist) {
        for (Point p: connector.keySet()){
            dist.putIfAbsent(p, 0.0);
        }
    }*/


    private int roundUp(double d){
        int rounder = (int) (d);
        if (rounder == d) return (int) d;
        return rounder + 1;
    }
}
