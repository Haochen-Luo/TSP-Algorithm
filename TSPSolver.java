package copy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedList;


public class TSPSolver {


    public static ArrayList<City> readFile(String filename) {

        ArrayList<City> cities = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = in.readLine()) != null) {
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
                City.distances[i][j] = City.distances[j][i] = Math.sqrt(Math.pow((ci.x - cj.x), 2) + Math.pow((ci.y - cj.y), 2));
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
                totalDistance += routine.get(i).distance(routine.get(i + 1));
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
            totalDistance += routine.get(i).distance(routine.get(i + 1));
        }
        return totalDistance;
    }

    /*
        Moves the city at index "from" to index "to" inside the routine
     */
    private static void moveCity(ArrayList<City> routine, int from, int to) {
        // provide your code here.
        City temp = routine.get(from);
        if (from > to) {
            for (int i = from; i > to; i--) {
                routine.set(i, routine.get(i - 1));
            }
            routine.set(to, temp);
        } else if (from < to - 1) {
            for (int i = from; i < to - 1; i++) {
                routine.set(i, routine.get(i + 1));
            }
            routine.set(to - 1, temp);
        }
    }

    private static ArrayList<City> moveCityReturn(ArrayList<City> routine, int from, int to) {
        ArrayList<City> temp = new ArrayList<>(routine);
        moveCity(temp, from, to);
        return temp;
    }

    /*
        Evaluate the relocation of city and returns the change in total distance.
        The return value is (old total distance - new total distance).
        As a result, a positive value means that the relocation of city results in routine improvement;
        a negative value means that the relocation leads to worse routine. A zero value means same quality.
     */


    public static double evalMove(ArrayList<City> routine, int from, int to) {
        // your implementation goes here

        if (from - to == 1) {
            double oldRoutine = routine.get(from).distance(routine.get(from + 1)) + routine.get(to).distance(routine.get(to - 1));
            double newRoutine = routine.get(from).distance(routine.get(from - 2)) + routine.get(to).distance(routine.get(from + 1));
            return oldRoutine - newRoutine;
        } else if (from - to != -1 && from != to) {
            double oldRoutine = routine.get(from).distance(routine.get(from + 1)) + routine.get(from).distance(routine.get(from - 1))
                    + routine.get(to).distance(routine.get(to - 1));
            double newRoutine = routine.get(from - 1).distance(routine.get(from + 1)) +
                    routine.get(from).distance(routine.get(to)) + routine.get(from).distance(routine.get(to - 1));
            return oldRoutine - newRoutine;
        } else {
            return 0;
        }
    }

    public static boolean moveFirstImprove(ArrayList<City> routine) {
        // your implementation goes here
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 2; j < routine.size() - 1; j++) {
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

    public static boolean moveImprove(ArrayList<City> routine) {
        // your implementation goes here
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 2; j < routine.size() - 1; j++) {
                double diff = evalMove(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    moveCity(routine, i, j);
                }
            }
        }
        for (int i = routine.size() - 2; i > 0; i--) {
            for (int j = i - 1; j > 0; j--) {
                double diff = evalMove(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    moveCity(routine, i, j);
                }
            }
        }
        return false;
    }


    public static void swapCity(ArrayList<City> routine, int index1, int index2) {

        City temp = routine.get(index1);
        routine.set(index1, routine.get(index2));
        routine.set(index2, temp);
        // your implementation goes here
    }

    public static ArrayList<City> swapCityReturn(ArrayList<City> routine, int index1, int index2) {
        ArrayList<City> arrayList = new ArrayList<>(routine);
        City temp = arrayList.get(index1);
        arrayList.set(index1, routine.get(index2));
        arrayList.set(index2, temp);
        return arrayList;
        // your implementation goes here
    }


    /*
        Can you improve the performance of this method?
        You are allowed to change the implementation of this method and add other methods.
        but you are NOT allowed to change its method signature (parameters, name, return type).
     */
    public static double evalSwap(ArrayList<City> routine, int index1, int index2) {
        int a = index1;
        int b = index2;
        index1 = Math.min(a, b);
        index2 = Math.max(a, b);
        if (index2 - index1 == 1) {
            double oldDistance = routine.get(index1).distance(routine.get(index1 - 1)) + routine.get(index2).distance(routine.get(index2 + 1));
            double newDistance = routine.get(index1).distance(routine.get(index1 + 2)) + routine.get(index2).distance(routine.get(index2 - 2));
            return oldDistance - newDistance;
        }
        double oldDistance =
                routine.get(index1).distance(routine.get(index1 + 1)) + routine.get(index1).distance(routine.get(index1 - 1))
                        + routine.get(index2).distance(routine.get(index2 + 1)) + routine.get(index2).distance(routine.get(index2 - 1));
        double newDistance =
                routine.get(index2).distance(routine.get(index1 + 1)) + routine.get(index2).distance(routine.get(index1 - 1))
                        + routine.get(index1).distance(routine.get(index2 + 1)) + routine.get(index1).distance(routine.get(index2 - 1));
        return oldDistance - newDistance;
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

    public static void reverseCity(ArrayList<City> routine, int index1, int index2) {
        int length = index2 - index1;
        for (int i = 0; i <= length / 2; i++) {
            swapCity(routine, index1 + i, index2 - i);
        }
    }


    public static double evalReverse(ArrayList<City> routine, int index1, int index2) {
        int begin = Math.min(index1, index2);
        int end = Math.max(index1, index2);
        double oldRoutine = routine.get(begin).distance(routine.get(begin - 1)) + routine.get(end).distance(routine.get(end + 1));
        double newRoutine = routine.get(end).distance(routine.get(begin - 1)) + routine.get(begin).distance(routine.get(end + 1));
        return oldRoutine - newRoutine;
    }

    public static void reverseImprove(ArrayList<City> routine) {
        for (int step = 3; step < routine.size() - 2; step++) {
            for (int i = 1; i < routine.size() - step - 1; i++) {
                double diff = evalReverse(routine, i, i + step);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    reverseCity(routine, i, i + step);
                }
            }
        }
    }

    public static ArrayList<City> improveRoutine(ArrayList<City> routine) {
        // Can you improve this simple algorithm a bit?
        long start = System.currentTimeMillis();
        ArrayList<City> bestRoutine = new ArrayList<>(stableImprove(routine));
        double baseLine = evaluateRoutine(routine);
        ArrayList<City> improvedRoutine = perturbationImprove(bestRoutine, baseLine, start);
        return improvedRoutine;

    }

    public static ArrayList<City> perturbationImprove(ArrayList<City> routine, double min, long start) {
        int st;
        int ed;
        int begin;
        int end;
        double temp;
        double current = System.currentTimeMillis();
        ArrayList<City> bestRoutine = new ArrayList<>(routine);
        while (current - start <= 297000) {
            //Use random move perturbation to improve the routine
            if (System.currentTimeMillis() - start > 297000) {
                return bestRoutine;
            }
            st = (int) (Math.random() * routine.size());
            ed = (int) (Math.random() * routine.size());
            begin = Math.max(Math.min(st, ed), 1);
            end = Math.min(Math.max(st, ed), routine.size() - 1);
            routine = moveCityReturn(bestRoutine, begin, end);
            stableImprove(routine);
            temp = evaluateRoutine(routine);
            if (temp < min) {
                bestRoutine = new ArrayList<>(routine);
                min = temp;
            }
            //Use random swap perturbation to improve the routine
            if (System.currentTimeMillis() - start > 297000) {
                return bestRoutine;
            }
            st = (int) (Math.random() * routine.size());
            ed = (int) (Math.random() * routine.size());
            begin = Math.max(Math.min(st, ed), 1);
            end = Math.min(Math.max(st, ed), routine.size() - 2);
            routine = swapCityReturn(bestRoutine, begin, end);
            stableImprove(routine);
            temp = evaluateRoutine(routine);
            if (temp < min) {
                bestRoutine = new ArrayList<>(routine);
                min = temp;
            }
            current = System.currentTimeMillis();
        }
        return bestRoutine;
    }

    public static ArrayList<City> stableImprove(ArrayList<City> routine) {
        int num = 60;
        double finalResult = evaluateRoutine(routine);
        double result = 0;
        for (int j = 0; j < num; j++) {
            for (int i = 0; i < num; i++) {
                reverseImprove(routine);
                double temp = evaluateRoutine(routine);
                if (result == temp) {
                    break;
                } else {
                    result = temp;
                }
            }
            //use method swapFirstImprove to improve the routine
            for (int i = 0; i < num; i++) {
                swapFirstImprove(routine);
                double temp = evaluateRoutine(routine);
                if (result == temp) {
                    break;
                } else {
                    result = temp;
                }
            }
            //use method moveImprove to improve the routine
            for (int i = 0; i < num; i++) {
                moveImprove(routine);
                double temp = evaluateRoutine(routine);
                if (result == temp) {
                    break;
                } else {
                    result = temp;
                }
            }
            double temp = evaluateRoutine(routine);
            if (finalResult == temp) {
                break;
            } else {
                finalResult = temp;
            }
        }
        return routine;
    }
}