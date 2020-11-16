import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class TSPSolver {

    private static double temperature = 3;
    private static double coolingRate = 0.99;

    public static ArrayList<City> readFile(String filename) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = null;
            while((line = in.readLine()) != null) {
                String[] blocks = line.trim().split("\\s+");
                if (blocks.length == 3) {
                    City c = new City();
                    c.city = Integer.parseInt(blocks[0]);
                    c.x = Double.parseDouble(blocks[1]);
                    c.y = Double.parseDouble(blocks[2]);
                    //System.out.printf("City %s %f %f\n", c.city, c.x, c.y);
                    cities.add(c);
                } else {
                    continue;
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        City.distances = new double[cities.size()][cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            City ci = cities.get(i);
            for (int j = i; j < cities.size(); j++) {
                City cj = cities.get(j);
                City.distances[i][j] = City.distances[j][i] = Math.sqrt(Math.pow((ci.x - cj.x),2) + Math.pow((ci.y - cj.y),2));
            }
        }
        return cities;
    }

    public static ArrayList<City> solveProblem(ArrayList<City> citiesToVisit) {
        ArrayList<City> routine = new ArrayList<City>();
        City start = null;
        City current = null;
        // get city 0;
        for (int i = 0; i < citiesToVisit.size(); i++) {
            if (citiesToVisit.get(i).city == 0) {
                start = current = citiesToVisit.remove(i);
                routine.add(current);
                break;
            }
        }
        if (current == null) {
            System.out.println("Your problem instance is incorrect! Exiting...");
            System.exit(0);
        }
        // visit cities
        while (!citiesToVisit.isEmpty()) {
            double minDist = Double.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < citiesToVisit.size(); i++) {
                double distI = current.distance(citiesToVisit.get(i));
                // index == -1 is needed in case the distance is really Double.MAX_VALUE.
                if (index == -1 || distI < minDist) {
                    index = i;
                    minDist = distI;
                }
            }
            //int index = 0;

            current = citiesToVisit.remove(index);
            routine.add(current);
        }
        routine.add(start); // go back to 0
        return routine;
    }

    public static double printSolution(ArrayList<City> routine) {
        double totalDistance = 0.0;
        for (int i = 0; i < routine.size(); i++) {
            if (i != routine.size() - 1) {
                System.out.print(routine.get(i).city + "->");
                totalDistance += routine.get(i).distance(routine.get(i+1));
            } else {
                System.out.println(routine.get(i).city);
            }
        }
        return totalDistance;
    }

    /*
        Just evaluate the total distance. A simplified version of printSolution()
     */
    public static double evaluateRoutine(ArrayList<City> routine) {
        double totalDistance = 0.0;
        for (int i = 0; i < routine.size() - 1; i++) {
            totalDistance += routine.get(i).distance(routine.get(i+1));
        }
        return totalDistance;
    }

    /*
        Moves the city at index "from" to index "to" inside the routine
     */
    private static void moveCity(ArrayList<City> routine, int from, int to) {
        // provide your code here.
        if (from < to) {
            routine.add(to, routine.get(from));
            routine.remove(from);
        } else {
            City swap = routine.get(from);
            routine.remove(from);
            routine.add(to, swap);
        }
    }

    /*
        Evaluate the relocation of city and returns the change in total distance.
        The return value is (old total distance - new total distance).
        As a result, a positive value means that the relocation of city results in routine improvement;
        a negative value means that the relocation leads to worse routine. A zero value means same quality.
     */
    public static double evalMove(ArrayList<City> routine, int from, int to) {
        // your implementation goes here
        // Unchanged case
        if (from == to || from == to - 1) {
            return 0.0;
        }
        // Inefficient naive approach for other special cases
        if (from == 0 || to == 0 || from == routine.size() - 1) {
            double oldDistance, newDistance;
            if (from < to) {
                oldDistance = evaluateRoutine(routine);
                moveCity(routine, from, to);
                newDistance = evaluateRoutine(routine);
                moveCity(routine, to - 1, from);
            } else {
                oldDistance = evaluateRoutine(routine);
                moveCity(routine, from, to);
                newDistance = evaluateRoutine(routine);
                moveCity(routine, to, from + 1);
            }
            return oldDistance - newDistance;
        }
        // Normal cases: only calculate the distance between affected elements
        double delta = 0.0;
        if (from < to) {
            delta += routine.get(from).distance(routine.get(from - 1));
            delta += routine.get(from).distance(routine.get(from + 1));
            delta += routine.get(to - 1).distance(routine.get(to));
            moveCity(routine, from, to);
            delta -= routine.get(from).distance(routine.get(from - 1));
            delta -= routine.get(to - 1).distance(routine.get(to - 2));
            delta -= routine.get(to - 1).distance(routine.get(to));
            moveCity(routine, to - 1, from);
        } else {
            delta += routine.get(from).distance(routine.get(from - 1));
            delta += routine.get(from).distance(routine.get(from + 1));
            delta += routine.get(to).distance(routine.get(to - 1));
            moveCity(routine, from, to);
            delta -= routine.get(from).distance(routine.get(from + 1));
            delta -= routine.get(to).distance(routine.get(to - 1));
            delta -= routine.get(to).distance(routine.get(to + 1));
            moveCity(routine, to, from + 1);
        }
        return delta;
    }

    /*
        This function iterate through all possible moving positions of cities.
        if a city move is found to lead to shorter travelling distance, that move action
        will be applied and the function will return true.
        If there is no good city move found, it will return false.
     */
    public static boolean moveFirstImprove(ArrayList<City> routine) {
        // your implementation goes here
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 1; j < routine.size(); j++) {
                double diff = evalMove(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    moveCity(routine, i, j);
                    return true;
                }
            }
        }
        for (int i = routine.size() - 2; i > 0; i--) {
            for (int j = i - 1; j > 0; j--) {
                double diff = evalMove(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    moveCity(routine, i, j);
                    return true;
                }
            }
        }
        return false;
    }

    /*
        Swaps the city at index "index1" and index "index2" inside the routine
     */
    public static void swapCity(ArrayList<City> routine, int index1, int index2) {
        // your implementation goes here
        City swap = routine.get(index1);
        routine.set(index1, routine.get(index2));
        routine.set(index2, swap);
    }

    /*
        Can you improve the performance of this method?
        You are allowed to change the implementation of this method and add other methods.
        but you are NOT allowed to change its method signature (parameters, name, return type).
     */
    public static double evalSwap(ArrayList<City> routine, int index1, int index2) {
        // Check for invalid swapping: swapping start/destination
        // Inefficient naive approach for special cases
        if (index1 == 0 || index2 == 0 || index1 == routine.size() - 1 || index2 == routine.size() - 1) {
            double oldDistance = evaluateRoutine(routine);
            swapCity(routine, index1, index2);
            double newDistance = evaluateRoutine(routine);
            swapCity(routine, index1, index2);
            return oldDistance - newDistance;
        }

        // Normal cases: only calculate the distance between affected elements
        double delta = 0.0;
        delta += routine.get(index1).distance(routine.get(index1 - 1));
        delta += routine.get(index1).distance(routine.get(index1 + 1));
        delta += routine.get(index2).distance(routine.get(index2 - 1));
        delta += routine.get(index2).distance(routine.get(index2 + 1));
        swapCity(routine, index1, index2); // swap
        delta -= routine.get(index1).distance(routine.get(index1 - 1));
        delta -= routine.get(index1).distance(routine.get(index1 + 1));
        delta -= routine.get(index2).distance(routine.get(index2 - 1));
        delta -= routine.get(index2).distance(routine.get(index2 + 1));
        swapCity(routine, index1, index2); // swap back
        return delta;
    }

    /*
        This function iterate through all possible swapping positions of cities.
        if a city swap is found to lead to shorter travelling distance, that swap action
        will be applied and the function will return true.
        If there is no good city swap found, it will return false.
     */
    public static boolean swapFirstImprove(ArrayList<City> routine) {
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 1; j < routine.size() - 1; j++) {
                double diff = evalSwap(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    swapCity(routine, i, j);
                    return true;
                }
            }
        }
        return false;
    }

    /*
        This method iterates from left to right through the given routine.
        Every time a city move is found to lead to shorter travelling distance,
        that move action will be applied, and the search continues until
        the loop finishes.
     */
    public static void keepMoving(ArrayList<City> routine) {
        for (int i = 1; i < routine.size(); i++) {
            for (int j = i + 1; j < routine.size(); j++) {
                double diff = evalMove(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    moveCity(routine, i, j);
                }
            }
        }
    }

    /*
        This method iterates from right to left through the given routine.
        Every time a city move is found to lead to shorter travelling distance,
        that move action will be applied, and the search continues until
        the loop finishes.
     */
    public static void keepMovingReverse(ArrayList<City> routine) {
        for (int i = routine.size() - 2; i > 0; i--) {
            for (int j = i; j > 0; j--) {
                double diff = evalMove(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    moveCity(routine, i, j);
                }
            }
        }
    }

    /*
        This method applies 2-opt algorithm to the routine.
     */
    public static void twoOpt(ArrayList<City> routine) {
        int from, to;
        from = (int) (Math.random() * (routine.size() - 2) + 1);
        to = (int) (Math.random() * (routine.size() - 2) + 1);
        if (from > to) {
            int swap = from;
            from = to;
            to = swap;
        }
        double delta = routine.get(from).distance(routine.get(from - 1));
        delta += routine.get(to).distance(routine.get(to + 1));
        delta -= routine.get(from).distance(routine.get(to + 1));
        delta -= routine.get(to).distance(routine.get(from - 1));
        if (delta - 0.00001 > 0) {
            City[] swap = new City[to - from + 1];
            for (int i = 0; i < swap.length; i++) {
                swap[i] = routine.get(from + i);
            }
            for (int i = 0; i < swap.length; i++) {
                routine.set(to - i, swap[i]);
            }
        }
    }

    /*
        This method initializes the parameters for simulated annealing.
     */
    public static void initSA() {
        temperature = 3;
        coolingRate = 0.99;
    }

    /*
        This method applies 2-opt algorithm with simulated annealing.
     */
    public static void twoOptWithSA(ArrayList<City> routine) {
        int from, to;
        from = (int) (Math.random() * (routine.size() - 2) + 1);
        to = (int) (Math.random() * (routine.size() - 2) + 1);
        if (from > to) {
            int swap = from;
            from = to;
            to = swap;
        }
        double delta = routine.get(from).distance(routine.get(from - 1));
        delta += routine.get(to).distance(routine.get(to + 1));
        delta -= routine.get(from).distance(routine.get(to + 1));
        delta -= routine.get(to).distance(routine.get(from - 1));
        if (delta - 0.00001 > 0 || (temperature > 0.3 && Math.pow(Math.E, delta / temperature) > Math.random())) {
            City[] swap = new City[to - from + 1];
            for (int i = 0; i < swap.length; i++) {
                swap[i] = routine.get(from + i);
            }
            for (int i = 0; i < swap.length; i++) {
                routine.set(to - i, swap[i]);
            }
        }
    }

    /*
        This method aims to improve the solution path resulted from solveProblem().
     */
    public static ArrayList<City> improveRoutine(ArrayList<City> routine) {
        // Can you improve this simple algorithm a bit?

        // Algorithm overview:
        // ((Stage 1 2-opt + SA) -> (Stage 1 hybrid improve) ->
        // (Stage 2 2-opt + SA) -> (Stage 2 hybrid improve)) * repeat 10 times =>
        // best result as solution

        // Set the timer
        long start = System.currentTimeMillis();
        // Set the loop counter for 2-opt algorithm with simulated annealing
        int loopTOSA = (routine.size() * 100);
        // Set the loop counter for hybrid improve algorithm
        int loopHybrid = (routine.size() * routine.size() / 2);

        // Stores original routine and best routine for compare and improve
        ArrayList<City> originalRoutine = new ArrayList<>(routine);
        ArrayList<City> bestRoutine = new ArrayList<>(routine);
        double bestDistance = evaluateRoutine(bestRoutine);

        // Repeat 10 times
        for (int numRepeat = 0; numRepeat < 10; numRepeat++) {
            routine = new ArrayList<>(originalRoutine);

            // Stage 1 2-opt + simulated annealing
            initSA();
            while (TSPSolver.swapFirstImprove(routine)) {
                if (System.currentTimeMillis() - start > 290000) break;
                for (int i = 0; i < loopTOSA; i++) {
                    twoOptWithSA(routine);
                }
                temperature *= coolingRate;
            }

            // Stage 1 hybrid improve
            keepMoving(routine);
            keepMovingReverse(routine);
            while (swapFirstImprove(routine)) {
                if (System.currentTimeMillis() - start > 290000) break;
                for (int i = 0; i < loopHybrid; i++) {
                    twoOpt(routine);
                }
                keepMoving(routine);
                keepMovingReverse(routine);
            }

            // Stage 2 simulated annealing
            initSA();
            temperature = 1.0;
            if (System.currentTimeMillis() - start > 290000) break;
            for (int i = 0; i < loopTOSA; i++) {
                twoOptWithSA(routine);
            }
            temperature *= coolingRate;
            while (TSPSolver.swapFirstImprove(routine)) {
                if (System.currentTimeMillis() - start > 290000) break;
                for (int i = 0; i < loopTOSA; i++) {
                    twoOptWithSA(routine);
                }
                temperature *= coolingRate;
            }

            // Stage 2 hybrid improve
            keepMoving(routine);
            keepMovingReverse(routine);
            while (swapFirstImprove(routine)) {
                if (System.currentTimeMillis() - start > 290000) break;
                for (int i = 0; i < loopHybrid; i++) {
                    twoOpt(routine);
                }
                keepMoving(routine);
                keepMovingReverse(routine);
            }

            // Compares current solution with previous best solution
            double thisDistance = evaluateRoutine(routine);
            if (bestDistance - thisDistance > 0.00001) {
                bestRoutine = new ArrayList<>(routine);
                bestDistance = thisDistance;
            }
        }

        // Return the best solution
        return bestRoutine;
    }

    // TODO: REMOVE DEBUG METHOD
    public static void debug(ArrayList<City> routine, int from, int to) {
        moveCity(routine, from, to);
    }

    public static ArrayList<City> originalImprove(ArrayList<City> routine) {
        swapFirstImprove(routine);
        moveFirstImprove(routine);
        return routine;
    }

    public static void randomSwapping(ArrayList<City> routine) {
        int index1, index2;
        int coeff = routine.size() - 3;
        index1 = (int) (Math.random() * coeff + 1);
        index2 = (int) (Math.random() * coeff + 1);
        double diff = evalSwap(routine, index1, index2);
        if (diff - 0.00001 > 0) { // I really mean diff > 0 here
            swapCity(routine, index1, index2);
        }
    }

    public static void randomMoving(ArrayList<City> routine) {
        int index1, index2;
        int coeff = routine.size() - 3;
        index1 = (int) (Math.random() * coeff + 1);
        index2 = (int) (Math.random() * coeff + 1);
        double diff = evalMove(routine, index1, index2);
        if (diff - 0.00001 > 0) { // I really mean diff > 0 here
            moveCity(routine, index1, index2);
        }
    }

    public static void shuffle(ArrayList<City> routine) {
        City swap;
        for (int i = 0; i + (routine.size() / 2) < routine.size() - 1; i++) {
            swap = routine.get(i);
            routine.set(i, routine.get(i + (routine.size() / 2)));
            routine.set(i + (routine.size() / 2), swap);
        }
    }

    /*
        This method iterates from left to right through the given routine.
        Every time a city swap is found to lead to shorter travelling distance,
        that swap action will be applied, and the search continues until
        the loop finishes.
     */
    public static void keepSwapping(ArrayList<City> routine) {
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 1; j < routine.size() - 1; j++) {
                double diff = evalSwap(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    swapCity(routine, i, j);
                }
            }
        }
    }
}
