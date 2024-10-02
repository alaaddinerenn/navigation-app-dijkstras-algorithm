import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
/**
 * This program is a navigation app to find the shortest route between to cities by implementing Dijkstra's algorithm.
 * @author Alaaddin Eren Namlı
 * @since Date: 04.04.2024
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        // File reading
        String fileName1 = "city_coordinates.txt"; // file name 1
        String fileName2 = "city_connections.txt"; // file name 2

        File file1 = new File(fileName1); // Creates the first file object
        File file2 = new File(fileName2); // Creates the second file object

        if (!file1.exists()) { // If file does not exist exit the program
            System.out.printf("%s can not be found.", fileName1); // Prints error message
            System.exit(1);
        }

        if (!file2.exists()) { // If file does not exist exit the program
            System.out.printf("%s can not be found.", fileName2); // Prints error message
            System.exit(1);
        }

        Scanner inputFile1 = new Scanner(file1); // Creates the scanner object for the file1
        Scanner inputFile2 = new Scanner(file2); // Creates the scanner object for the file2

        ArrayList<City> cities = new ArrayList<City>(); // An arraylist that contains cities as city objects
        ArrayList<String> cityNames = new ArrayList<String>(); // An arraylist that contains city names as strings
        ArrayList<ArrayList<String>> cityConnections = new ArrayList<ArrayList<String>>(); // An arraylist that contains city connections as strings

        while (inputFile1.hasNextLine()) { // Reads city_coordinates(file1)
            String line = inputFile1.nextLine(); // Reads the next line
            String[] lineParts = line.split(", "); // Split the line
            String cityName = lineParts[0]; // First element is city name
            int xCoordinate = Integer.parseInt(lineParts[1]); // Second element is the x coordinate of the city
            int yCoordinate = Integer.parseInt(lineParts[2]); // Third element is the y coordinate of the city
            cityNames.add(cityName); // Add the city name to the cityNames arraylist
            City city = new City(cityName,xCoordinate,yCoordinate); // Create new city object with those parameters from the file
            cities.add(city); // Add the city to the cities arraylist
        }
        inputFile1.close(); // Close the file1

        while (inputFile2.hasNextLine()) { // Reads city_connections(file2)
            String line = inputFile2.nextLine(); // Reads the next line
            String[] lineParts = line.split(","); // Split the line
            ArrayList<String> producer = new ArrayList<String>(); // Create a new arraylist to produce matrix
            producer.add(lineParts[0]);  // Add the first city to the producer arraylist
            producer.add(lineParts[1]); // Add the second city to the producer arraylist
            cityConnections.add(producer); // Add the producer arraylist to the cityConnections arraylist
        }
        inputFile2.close(); // Close the file2

        // Create matrix that contains distances between the cities and their neighbours
        // This matrix is size of number of cities times number of cities
        // In this matrix cities are represented by their indexes from cities arraylist
        // If there is road between the two cities there would be a distance value
        // If there is no road there would be 0.0

        // First create an empty matrix.
        // To make empty-like arraylists use 0.0. In other words, 0.0 means empty
        ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<ArrayList<Double>>(); // Create new arraylist to hold distances between the cities and their neighbours
        for (int i = 0; i < cities.size(); i++) { // This for loop creates an arraylist matrix that contains empty arraylists
            ArrayList<Double> emptyList = new ArrayList<Double>();
            for (int j = 0; j < cities.size(); j++) {
                emptyList.add(0.0); // Add 0.0 to empty list
            }
            distanceMatrix.add(emptyList); // Add empty lists to the matrix
        }

        // This for loop creates a matrix that contains distances between the cities and their neighbours
        for (int i = 0; i < cityConnections.size(); i++) {
            String city1 = cityConnections.get(i).get(0); // Get city1 string from cityConnections
            String city2 = cityConnections.get(i).get(1); // Get city2 string from cityConnections
            int indexOfCity1 = cityNames.indexOf(city1); // Get index of city1 from cityNames
            int indexOfCity2 = cityNames.indexOf(city2); // Get index of city2 from cityNames

            distanceMatrix.get(indexOfCity1).set(indexOfCity2,distanceCalculator(city1,city2,cities,cityNames)); // Replace the value 0.0 at the index of city1 in distanceMatrix with the distance between city1 and city2
            distanceMatrix.get(indexOfCity2).set(indexOfCity1,distanceCalculator(city1,city2,cities,cityNames)); // Replace the value 0.0 at the index of city2 in distanceMatrix with the distance between city1 and city2
        }

        //STD DRAW

        //Input taking and printing on console
        Scanner input1 = new Scanner(System.in); // Create input1 scanner object
        Scanner input2 = new Scanner(System.in); // Create input2 scanner object
        System.out.print("Enter starting city: "); // Print the command message for starting city input
        String startingCity = input1.nextLine(); // Read the input

        // This while loop checks if the starting city input is valid
        while (true) {
            boolean isValid1 = cityNames.contains(startingCity); // Create a boolean variable that checks if the input is a valid city name
            if (isValid1) { // If it is valid exit the loop
                break;
            } else { // If it is not valid print the error message and ask for another city name until it is valid
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n", startingCity); // Print the error message
                System.out.print("Enter starting city: "); // Print the command message for starting city input
                startingCity = input1.nextLine(); // Read the input
            }
        }

        System.out.print("Enter destination city: "); // Print the command message for destination city input
        String destinationCity = input2.nextLine(); // Read the input

        // This while loop checks if the destination city input is valid
        while (true) {
            boolean isValid2 = cityNames.contains(destinationCity); // Create a boolean variable that checks if the input is a valid city name
            if (isValid2) { // If it is valid exit the loop
                break;
            } else { // If it is not valid print the error message and ask for another city name until it is valid
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n", destinationCity); // Print the error message
                System.out.print("Enter destination city: "); // Print the command message for destination city input
                destinationCity = input2.nextLine(); // Read the input
            }
        }

        int startingCityIndex = cityNames.indexOf(startingCity); // An integer that holds startingCityIndex
        int destinationCityIndex = cityNames.indexOf(destinationCity); // An integer that holds destinationCityIndex
        int numberOfCities = cities.size(); // An integer that holds number of cities

        // Dijkstra algorithm. Its return value assigned to citiesOnPath
        ArrayList<String> citiesOnThePath = dijkstra(distanceMatrix,startingCityIndex,destinationCityIndex,numberOfCities,cityNames);
        // citiesOnPath is an arraylist that contains cities on the path as strings

        ArrayList<City> route = new ArrayList<City>(); // An arraylist that contains the cities on the path as city objects
        route = returnTheRoute(citiesOnThePath,cities,cityNames); // returnTheRoute method returns the arraylist that contains the cities on the path

        //Drawing
        int width = 2377; // Width of the map
        int height = 1055; // Height of the map

        if (!citiesOnThePath.isEmpty()) {
            StdDraw.setCanvasSize(width/2,height/2);
            StdDraw.setXscale(0.0,2377.0); // Sets x scale from 0.0 to 2377.0
            StdDraw.setYscale(0.0,1055.0); // Sets y scale from 0.0 to 2377.0
            StdDraw.picture(width/2.0,height/2.0,"map.png",2377.0,1055.0); // Draws the map
            StdDraw.enableDoubleBuffering(); // Enables double buffering

            for (int k = 0; k < cities.size(); k++) { // Draws city dots and names
                StdDraw.setPenColor(StdDraw.GRAY); // Set pen color to gray
                StdDraw.filledCircle(cities.get(k).x,cities.get(k).y,5.0); // Draw the city dot
                StdDraw.setFont(new Font("Arial",Font.BOLD,12)); // Set font to Arial Bold size of 12
                StdDraw.text(cities.get(k).x,cities.get(k).y + 15.0,cities.get(k).cityName); // Write the name of the city
            }

            for (int i = 0; i < cityConnections.size(); i++) { // Draws roads between cities
                String city1 = cityConnections.get(i).get(0); // Get city1 string
                String city2 = cityConnections.get(i).get(1); // Get city2 string
                int index1 = cityNames.indexOf(city1); // Get index of city1
                int index2 = cityNames.indexOf(city2); // Get index of city2
                StdDraw.line(cities.get(index1).x,cities.get(index1).y,cities.get(index2).x,cities.get(index2).y); // Draw the line between city1 and city2
            }

            if(citiesOnThePath.size() == 1) { // If there is one city on the route
                StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); // Set pen color to light blue
                StdDraw.filledCircle(cities.get(startingCityIndex).x, cities.get(startingCityIndex).y, 5.0); // Draw the city dot
                StdDraw.text(cities.get(startingCityIndex).x, cities.get(startingCityIndex).y + 15.0, cities.get(startingCityIndex).cityName); // Write the name of the city
                StdDraw.show(); // Show the drawing
            } else { // If there is more than one cities on the route
                for (int i = 0; i < route.size() - 1; i++) { // For each road on the route draw it on the map
                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); // Set pen color to light blue
                    StdDraw.filledCircle(route.get(i).x, route.get(i).y, 5.0); // Draw the city dot
                    StdDraw.setFont(new Font("Arial", Font.BOLD, 12)); // Set font to Arial Bold size of 12
                    StdDraw.text(route.get(i).x, route.get(i).y + 15.0, route.get(i).cityName); // Write the name of the city
                    StdDraw.setPenRadius(0.008); // Set pen radius to 0.008
                    StdDraw.line(route.get(i).x, route.get(i).y, route.get(i + 1).x, route.get(i + 1).y); // Draw the line between cities
                }
                StdDraw.filledCircle(route.getLast().x, route.getLast().y, 5.0); // Draw the last city dot
                StdDraw.text(route.getLast().x, route.getLast().y + 15.0, route.getLast().cityName); // Write the name of the last city
                StdDraw.show(); // Show the drawing
            }
        }

    }

    /**
     * Calculates the distance between two cities
     * @param cityName1 Name of the city1
     * @param cityName2 Name of the city1
     * @param cities An arraylist that contains city objects
     * @param cityNames An arraylist of strings that contains the names of the cities
     * @return The distance between two cities
     */
    public static double distanceCalculator(String cityName1,String cityName2,ArrayList<City> cities,ArrayList<String> cityNames  ) {
        int city1Index = cityNames.indexOf(cityName1); // Get the index of city1
        int city2Index = cityNames.indexOf(cityName2); // Get the index of city2
        City city1 = cities.get(city1Index); // Get the city1 from cities arraylist
        City city2 = cities.get(city2Index); // Get the city2 from cities arraylist
        double xCoordinateOfCity1 = city1.x; // x coordinate of city1
        double yCoordinateOfCity1 = city1.y; // y coordinate of city1
        double xCoordinateOfCity2 = city2.x; // x coordinate of city2
        double yCoordinateOfCity2 = city2.y; // y coordinate of city2
        double distance = Math.sqrt(Math.pow(xCoordinateOfCity1 - xCoordinateOfCity2,2) + Math.pow(yCoordinateOfCity1 - yCoordinateOfCity2,2)); // The distance between city1 and city2
        return distance; // return the distance
    }

    /**
     * A method that implements Dijkstra's single source shortest path algorithm for a graph represented using a matrix
     * @param matrix A matrix that contains distances between the cities and their neighbours
     * @param source The index of the starting city
     * @param target The index of the destination city
     * @param numberOfCities Number of cities
     * @param cityNames An arraylist of strings that contains the names of the cities
     * @return An arraylist of strings that contains the cities on the path
     */
    public static ArrayList<String> dijkstra(ArrayList<ArrayList<Double>> matrix, int source, int target, int numberOfCities, ArrayList<String> cityNames) {
        ArrayList<Integer> result = new ArrayList<Integer>(); // Create the result arraylist
        ArrayList<Integer> parent = new ArrayList<Integer>(); // Create the parent arraylist

        // To make empty-like arraylists use 0.0. In other words, 0.0 means empty
        for (int i = 0; i < numberOfCities; i++) {
            parent.add(0);
        }

        double[] distances = new double[numberOfCities]; // The array that contains minimum distances from the source
        // distances[i] will hold
        // the shortest distance from source to i

        // isInSpt[i] will true if city it is included in shortest path tree or shortest distance from source to i is finalized
        Boolean[] isInSpt = new Boolean[numberOfCities];

        for (int i = 0; i < numberOfCities; i++) { // Initialize all distances as INFINITE and isInSpt[] as false
            distances[i] = Double.MAX_VALUE;
            isInSpt[i] = false;
        }

        distances[source] = 0.0; // Distance of source city from itself is always 0
        parent.set(source,-1); // Set the value -1 at the index of the source

        // Find shortest path for all cities
        for (int count = 0; count < numberOfCities - 1; count++) {
            // Pick the minimum distance city from the set of cities not yet processed. minIndex is always equal to source in first iteration.
            int minIndex = getMinDistance(distances, isInSpt,numberOfCities);

            isInSpt[minIndex] = true; // Mark the picked city as processed

            // Update distance value of the adjacent cities of the picked city.
            for (int v = 0; v < numberOfCities; v++) {
                // Update distance[v] only if it is not in isInSpt, there is an edge from minIndex to v, and total weight of path from source to v through minIndex is smaller than current value of distance[v]
                if (!isInSpt[v] && matrix.get(minIndex).get(v) != 0 && distances[minIndex] != Double.MAX_VALUE && distances[minIndex] + matrix.get(minIndex).get(v) < distances[v]) {
                    // Set the value of distances[v] to the distance between minIndex and v + the total distance from the source
                    distances[v] = (distances[minIndex] + matrix.get(minIndex).get(v));
                    parent.set(v,minIndex); // Set the value at the index v to minIndex to store the parent of v
                }
            }
        }
        printDistance(distances,target); // Print the distance on the console
        ArrayList<Integer> zeros = new ArrayList<Integer>(); // Initialize the zeros arraylist
        ArrayList<Integer> pathList = getPath(parent,target,result,zeros); // Store an arraylist that contains the indexes of the cities on the path in the reverse order and -1,the destination city,.
        return printPath(pathList,target,distances,cityNames); // Return an arraylist of strings that contains the cities on the path
    }

    /**
     * A method to find the index of the city with minimum distance value, from the list of cities not yet included in shortest path tree
     * @param distances An array that holds minimum distances from the starting city
     * @param isInSpt An array of booleans that check if a city included in shortest path tree or shortest distance from starting city to destination city is finalized
     * @param numberOfCities Number of cities
     * @return The index of city with minimum distance value from the starting city
     */
    public static int getMinDistance(double[] distances, Boolean[] isInSpt,int numberOfCities) {
        double minimumValue = Double.MAX_VALUE; // Initialize the minimum value
        int minIndex = -1; // Initialize minimum index

        for (int v = 0; v < numberOfCities; v++) {
            // If the city is not included in shortest path tree and the distance between the starting city to that city is the minimum distance,
            // return that city's index
            if (isInSpt[v] == false && distances[v] <= minimumValue) {
                minimumValue = distances[v]; // Set minimum to new value
                minIndex = v; // Store the index of the city
            }
        }
        return  minIndex;
    }

    /**
     * Prints the total distance of the path
     * @param distances An array that holds minimum distances from the starting city
     * @param target The index of the destination city
     */
    public static void printDistance(double[] distances,int target) {
        if (distances[target] != Double.MAX_VALUE) { // If the distance between the two city is not infinite, print the distance
            System.out.printf(Locale.US,"Total Distance: %.2f.",distances[target]); // Print the distance
        }
    }

    /**
     * A recursive method to find the cities on the path
     * @param parent An array list that contains the indexes of the parent cities of each city
     * @param target The index of the destination city
     * @param result An arraylist that contains the indexes of the cities on the path in the reverse order and -1,the destination city,.
     * @param zeros An arraylist that stores the instances of zero values
     * @return the result arraylist
     */
    public static ArrayList<Integer> getPath(ArrayList<Integer> parent,int target,ArrayList<Integer> result,ArrayList<Integer> zeros) {
        if (zeros.size() == 2) { // If there are two zeros return empty arraylist
            ArrayList<Integer> empty = new ArrayList<Integer>(); // Create empty arraylist
            return empty;
        }

        if (parent.get(target) == 0) { // If there is an instance of 0 value, add 1 to the zeros arraylist
            zeros.add(1); // Add 1 to the zeros arraylist
        }

        if (parent.get(target) == -1) { // If there is -1, it means it is the starting city. Add -1 to the result arraylist and finish the recursion
            result.add(-1); // Add -1 to the result arraylist
            return result;
        }
        result.add(parent.get(target)); // Add the index of the parent city
        return getPath(parent,parent.get(target),result,zeros); // Call getPath method for recursion
    }

    /**
     * Prints the cities on the path with arrows between two cities and returns an arraylist of strings that contains those cities
     * @param pathList An arraylist that contains the indexes of the cities on the path in the reverse order and -1,the destination city,.
     * @param target The index of the destination city
     * @param distances An array that holds minimum distances from the starting city
     * @param cityNames An arraylist of strings that contains the names of the cities
     * @return An arraylist of strings that contains the cities on the path
     */
    public static ArrayList<String> printPath(ArrayList<Integer> pathList,int target,double[] distances,ArrayList<String> cityNames) {
        ArrayList<String> citiesOnTheRoute = new ArrayList<String>(); // Create arraylist that will store the cities on the path
        if (distances[target] == Double.MAX_VALUE || pathList.isEmpty()) { // If there is no path from starting city to destination city, print the message
            System.out.println("No path could be found."); // Print the message
        } else {
            System.out.print(" Path: ");
            for (int i = pathList.size()-2; i > -1 ; i--) { // For each city in the pathList arraylist
                String cityName = cityNames.get(pathList.get(i)); // Get the city name as a string
                citiesOnTheRoute.add(cityName); // Add the city to the citiesOnTheRoute arraylist
                System.out.print(cityName + " -> "); // Print cities with arrows
            }
            String targetCityName = cityNames.get(target); // Get the name of the destination city as a string
            citiesOnTheRoute.add(targetCityName); // Add the destination city to the citiesOnTheRoute arraylist
            System.out.println(targetCityName); // Print the name of the destination city
        }
        return citiesOnTheRoute;
    }

    /**
     * Gets cities on the path as an arraylist of strings and returns those cities as an arraylist of city objects
     * @param citiesOnTheRoute An arraylist of strings that contains cities on the path
     * @param cities An arraylist that contains city objects
     * @param cityNames An arraylist of strings that contains the names of the cities
     * @return An arraylist of city objects that contains cities on the path
     */
    public static ArrayList<City> returnTheRoute(ArrayList<String> citiesOnTheRoute,ArrayList<City> cities,ArrayList<String> cityNames) {
        ArrayList<City> theRoute = new ArrayList<City>(); // Create arraylist of city objects
        for (int i = 0; i < citiesOnTheRoute.size(); i++) {
            String cityString = citiesOnTheRoute.get(i); // Get city as a string
            int index = cityNames.indexOf(cityString); // Get index of the city from cityNames arraylist
            City city = cities.get(index); // Get index of the city from cities arraylist
            theRoute.add(city); // Add the city to the theRoute arraylist
        }
        return theRoute;
    }

}
