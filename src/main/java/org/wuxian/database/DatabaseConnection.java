package main.java.org.wuxian.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;

public class DatabaseConnection {
    static Connection conn;
    public static Connection getConnection()  {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/simple_access","root","");
            //System.out.println("Connected");
        } catch (ClassNotFoundException e) {
          // e.printStackTrace();
        } catch (SQLNonTransientConnectionException e) {
          // e.printStackTrace();
            System.out.println("Connection issues");
        } catch (SQLException e) {
          // e.printStackTrace();
        }
        return conn;
    }

    //public static void main(String[] args) {
    //    DatabaseConnection.getConnection();
    //}
}
