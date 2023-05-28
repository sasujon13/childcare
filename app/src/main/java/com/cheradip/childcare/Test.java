package com.cheradip.childcare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        // Database connection details
        String url = "jdbc:mysql://db4free.net:3306/childcare";
        String username = "sasujon13";
        String password = "Sa@2271029867";

        // Attempt to establish a connection
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); // or handle the exception in an appropriate way
            }
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully!");
            connection.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.out.println("Connection failed. Error message: " + e.getMessage());
        }
    }
}
