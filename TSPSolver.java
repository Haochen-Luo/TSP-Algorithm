package 西交利物浦.coursework;

import java.io.*;
import java.util.ArrayList;

public class TSPSolver {
    private static double distance = 0;

    public static ArrayList<City> readFile(String filename) {
        ArrayList<City> cities = new ArrayList<>();
        BufferedReader bufferedReader = null;
        //create the BufferedReader and deal with the exception that the file is not found
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //read the file line by line, create the city object and add them to the ArrayList cities
        //catch the IOE exception and print the trace in reading the file
        try {
            String temp;
            assert bufferedReader != null; //assert it is not null to avoid the exception in bufferedReader.readLine()
            while ((temp = bufferedReader.readLine()) != null) {
                String[] info = temp.split("\\s+"); //split the String and to improve the robustness, "\\s+" is used in case that there might be more space
                int num = Integer.parseInt(info[0]);
                int x = Integer.parseInt(info[1]);
                int y = Integer.parseInt(info[2]);
                City newCity = new City(num, x, y);
                City.count++;
                cities.add(newCity);
            }
            bufferedReader.close();
            if (City.count == 0) {
                throw new IllegalArgumentException("The number of cities cannot be less than 1.");
            }
            City.dist = new double[City.count][City.count];
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
        return cities;
    }

    public static ArrayList<City> solveProblem(ArrayList<City> citiesToVisit) {
        //Handle the illegal argument exceptions
        if (citiesToVisit == null) {
            throw new IllegalArgumentException("The list of cities to visit cannot be null");
        }
        if (citiesToVisit.size() == 0) {
            throw new IllegalArgumentException("The number of cities cannot be less than 1.");
        }
        ArrayList<City> routine = new ArrayList<City>();
        routine.add(citiesToVisit.get(0));
        City first = citiesToVisit.get(0);
        City currentCity = citiesToVisit.get(0);
        //find the nearest city to the current city until the size is less than or equal to 1
        while (citiesToVisit.size() > 1) {
            citiesToVisit.remove(currentCity);
            currentCity = findNearest(currentCity, citiesToVisit);
            routine.add(currentCity);
        }
        distance = distance + currentCity.distance(first);
        return routine;
    }

    //This method will find the nearest city of the current city.
    public static City findNearest(City currentCity, ArrayList<City> cities) {
        City nearest = cities.get(0);
        double min = Double.MAX_VALUE;
        for (City temp : cities) {
            double dist = currentCity.distance(temp);
            if (dist < min) {
                nearest = temp;
                min = dist;
            }
        }
        distance += min;
        return nearest;
    }

    public static double printSolution(ArrayList<City> routine) {
        //Handle the illegal argument exceptions
        if (routine == null) {
            throw new IllegalArgumentException("The routine cannot be null");
        }
        if (routine.size() == 0) {
            throw new IllegalArgumentException("The number of cities cannot be less than 1.");
        }
        double totalDistance = 0.0;
        totalDistance = distance;
        for (City city : routine) {
            System.out.print(city + "->");
        }
        System.out.println(0);
        return totalDistance;
    }

}
