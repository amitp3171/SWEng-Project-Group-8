package il.cshaifasweng.OCSFMediatorExample.client;
import java.sql.Connection;
import java.sql.DriverManager;

public class CheckConnectivity {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/myfirstdatabase?serverTimezone=UTC";
        String username = "root";
        String password = "Gamal385";
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connection successful!");
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



