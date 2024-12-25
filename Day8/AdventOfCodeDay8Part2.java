import java.io.*;
import java.util.*;

public class AdventOfCodeDay8Part2 {
    public static void main(String[] args) {
        /*
         * The task to find how many antinodes exist in a map.
         * A map is a rectangle where '.' is an empty tile and a letter or digit signifies an antenna.
         * Antinodes exist in places where there is a straight line with atleast 2 same type antennas that are some multiple
         * of a distance away from it (first is some distance, second is double that and so on). And the antinode tile
         * can't be an antenna tile, since that would extend the signal.
         * Antinodes outside the map aren't counted and multiple antinodes in the same tile must be counted multiple times.
         * */
        System.out.println(findAmountOfAntiodes(new File(args[0])));
    }

    private static int findAmountOfAntiodes(File file) {
        // First make the file into a char[][] array for ease of use.
        char[][] map;
        int howManyAntinodes = 0;
        try(InputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = inputStream.readAllBytes();
            int newlineCharAsByte = (byte)'\n';

            int lineLength = 0;
            for (byte aByte : bytes) {
                if (aByte == newlineCharAsByte)
                    break;
                lineLength++;
            }
            //                  The last newline
            map = new char[(bytes.length+1)/(lineLength+1)][];
            for (int i = 0; i < map.length; i++) {
                map[i] = new char[lineLength];
            }

            // Fill the lines
            int lineIndex = 0, columnIndex = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte currentByte = bytes[i];
                if (currentByte == newlineCharAsByte) {
                    columnIndex = 0;
                    lineIndex++;
                    continue;
                }
                if (lineIndex == map.length)
                    break;
                map[lineIndex][columnIndex] = (char)currentByte;
                columnIndex++;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Find antenna types, map all the locations for them - int[] is line, column coordinates
        Map<Character, List<int[]>> antennaLocations = new HashMap<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                char currentChar = map[i][j];
                if (currentChar != '.') {
                    int[] coordinates = new int[]{i, j};
                    List<int[]> values;
                    if (antennaLocations.containsKey(currentChar)) {
                        values = antennaLocations.get(currentChar);
                        values.add(coordinates);
                        continue;
                    }
                    values = new ArrayList<>();
                    values.add(coordinates);
                    antennaLocations.put(currentChar, values);
                }
            }
        }

        // For every antenna type, find antinodes:
        // - Check first antenna against all others and get back how many antinodes they make (0, 1 or 2)
        // - Check the second antenna against all others, calling out a helper each time. etc
        Set<Character> antennaTypes = antennaLocations.keySet();
        // columnIndex + lineLength*LineIndex uniquely identifies the coordinates in one number
        Set<Integer> antinodeLocations = new HashSet<>();
        for (Character character : antennaTypes) {
            List<int[]> locations = antennaLocations.get(character);
            int locationsListSize = locations.size();
            for (int i = 0; i < locationsListSize; i++) {
                for (int j = i+1; j < locationsListSize; j++) {
                    addAntinodes(map, character, i, j, locations, antinodeLocations);
                }
            }
        }
        // antinodeLocations saved the antinodes that aren't distance 0 from atleast 2 antennas.
        // To get all the locations at distance 0, add all the antenna positions themselves!
        int totalAntennas = 0;
        for (Character antennaType : antennaTypes) {
            totalAntennas += antennaLocations.get(antennaType).size();
        }
        return antinodeLocations.size() + totalAntennas;
    }

    private static void addAntinodes(char[][] map, Character character, int firstAntennaIndex, int secondAntennaIndex, List<int[]> locations, Set<Integer> antinodeLocations) {
        int[] firstAntennaCoordinates = locations.get(firstAntennaIndex);
        int[] secondAntennaCoordinates = locations.get(secondAntennaIndex);

        int lineDifference = firstAntennaCoordinates[0] - secondAntennaCoordinates[0];
        int columnDifference = firstAntennaCoordinates[1] - secondAntennaCoordinates[1];

        // Look at both sides of the line - first towards first antenna side of the line
        int newAntennaLineNr = firstAntennaCoordinates[0]+lineDifference,
                newAntennaColumnNr = firstAntennaCoordinates[1]+columnDifference;
        addAntinodeInDirection(map, newAntennaLineNr, newAntennaColumnNr, lineDifference, columnDifference, antinodeLocations);

        // Then towards second antenna side of the line
        newAntennaLineNr = secondAntennaCoordinates[0] - lineDifference;
        newAntennaColumnNr = secondAntennaCoordinates[1] - columnDifference;
        addAntinodeInDirection(map, newAntennaLineNr, newAntennaColumnNr, -lineDifference, -columnDifference, antinodeLocations);
    }
    private static int convertCoordsToUniqueNumber(int lineNumber, int columnNumber, int lineLength) {
        return lineLength*lineNumber + columnNumber;
    }
    private static boolean coordsInBoundsOfMap(char[][] map, int lineNumber, int columnNumber) {
        if (lineNumber < 0 || lineNumber >= map.length)
            return false;
        if (columnNumber < 0 || columnNumber >= map[lineNumber].length)
            return false;
        return true;
    }
    private static void addAntinodeInDirection(char[][] map, int antennaLineNumber, int antennaColumnNumber, int lineStep, int columnStep, Set<Integer> antiNodeUniqueLocations) {
        while(coordsInBoundsOfMap(map, antennaLineNumber, antennaColumnNumber)) {
            // We only need unique locations. Antennas themselves all have antinodes on them since it's distance 0. We add that later anyways.
            // That means we only want to add if the current location is an empty tile!
            if (map[antennaLineNumber][antennaColumnNumber] == '.') {
                int locationNumber = convertCoordsToUniqueNumber(antennaLineNumber, antennaColumnNumber, map[antennaLineNumber].length);
                antiNodeUniqueLocations.add(locationNumber);
            }
            antennaLineNumber += lineStep;
            antennaColumnNumber += columnStep;
        }
    }
}
