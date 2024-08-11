package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.DialogInterface;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Hashtable;

public class DataParser {

    private static DataParser instance;

    private DataParser() {}

    public static synchronized DataParser getInstance() {
        if (instance == null) {
            instance = new DataParser();
        }
        return instance;
    }

    private static Map<String, String> generateMap(String stringRepresentation, String... fields) {
        String[] parsedRepresentation = stringRepresentation.split(",(?![^\\[]*\\])");
        Map<String, String> map = new Hashtable<>();

        for (int i = 0; i < fields.length; i++)
            map.put(fields[i], parsedRepresentation[i]);

        if (fields.length < parsedRepresentation.length)
            map.put("additionalFields", String.join(",", Arrays.copyOfRange(parsedRepresentation, fields.length, parsedRepresentation.length)));

        return map;
    }

    public static Map<String, String> parseCustomer(String stringRepresentation) {
        return generateMap(stringRepresentation, "firstName", "lastName");
    }

    public static Map<String, String> parseEmployee(String stringRepresentation) {
        return generateMap(stringRepresentation, "firstName", "lastName", "employeeType");
    }
    // TODO: verify this actually works for all subclasses of AbstractMovie
    public static Map<String, String> parseMovie(String stringRepresentation) {
        return generateMap(stringRepresentation, "id", "movieName", "description", "mainActors", "producerName", "picture");
    }

    public static Map<String, String> parseScreeningTime(String stringRepresentation) {
        return generateMap(stringRepresentation, "id", "date", "time", "theaterId");
    }

    public static Map<String, String> parseSeat(String stringRepresentation) {
        return generateMap(stringRepresentation, "id", "isTaken");
    }

    public static Map<String, String> parseComplaint(String stringRepresentation) {
        return generateMap(stringRepresentation, "id", "title", "complaintContent", "response");
    }

}
