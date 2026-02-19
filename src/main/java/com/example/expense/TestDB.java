package com.example.expense;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/expense_tracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "1234";

        try {
            System.out.println("Connecting to database...");
            Connection conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("SUCCESS: Connected to MySQL successfully!");
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("ERROR: Connection failed!");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}
