package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        Connection con = null;

        try {

            String url = "jdbc:mysql://localhost:3306/atmdb";
            String user = "root";
            String password = "aftab";

            con = DriverManager.getConnection(url, user, password);

            System.out.println("Database Connected ✅");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return con;
    }
}