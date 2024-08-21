package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.util.ArrayList;

public class UserDataManager {

    private static UserDataManager instance;

    private static String id;

    private static String firstName;
    private static String lastName;

    // customer data
    private static String govId;

    // employee
    private static String employeeUserName;
    private static String employeeType;

    private static String additionalFields;

    private UserDataManager() {}

    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            resetData();
            instance = new UserDataManager();
        }
        return instance;
    }

    public static void logoutUser() {
        if (!isGuest()) {
            try {
                CinemaClient.sendToServer("logout user", CinemaClient.getUserDataManager().getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isCustomer() {
        return govId != null;
    }

    public static boolean isEmployee() {
        return employeeType != null;
    }

    public static boolean isGuest() {
        return (!isCustomer() && !isEmployee());
    }

    public static String getId() {
        return id;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getGovId() {
        return govId;
    }

    public static String getEmployeeUserName() {
        return employeeUserName;
    }

    public static String getEmployeeType() {
        return employeeType;
    }

    public static String getAdditionalFields() {
        return additionalFields;
    }

    public static void setId(String newId) {
        id = newId;
    }

    public static void setFirstName(String newFirstName) {
        firstName = newFirstName;
    }

    public static void setLastName(String newLastName) {
        lastName = newLastName;
    }

    public static void setGovId(String newGovId) {
        govId = newGovId;
    }

    public static void setEmployeeUserName(String newEmployeeUserName) {
        employeeUserName = newEmployeeUserName;
    }

    public static void setEmployeeType(String newEmployeeType) {
        employeeType = newEmployeeType;
    }

    public static void setAdditionalFields(String newAdditionalFields) {
        additionalFields = newAdditionalFields;
    }

    public static void resetData() {
        firstName = null;
        lastName = null;
        govId = null;
        employeeUserName = null;
        employeeType = null;
    }
}
