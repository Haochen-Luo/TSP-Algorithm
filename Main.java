package 西交利物浦.coursework;

import java.io.IOException;
import java.util.ArrayList;

/*
    Feel free to change this file, but:
	!!!!!  ANY modification to this file WILL BE INGNORED  !!!!!
 */
public class Main {
    public static void main(String[] args) throws IOException {
        long s = System.currentTimeMillis();
        int[] a = new int[0];
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
        ArrayList<City> cities = TSPSolver.readFile(/*put your file path here to test with different instances*/"D:\\C110_2.TXT");
        cities = TSPSolver.solveProblem(cities);
        Double totalDistance = TSPSolver.printSolution(cities);
        System.out.printf("Distances: %f\n", totalDistance);
        long e = System.currentTimeMillis();
        System.out.println(e - s);
        // Your program should not crash after running the code above!!!
        // It should print out a correct result
    }

}

