package org.example;

import java.sql.Connection;
import java.util.Scanner;

public class JDBC {

    public static void main(String[] args) {

        String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=ArticlesDB;" + "encrypt=true;"
                + "trustServerCertificate=true";

        String userID = "sa";
        String passID = "root";

        Scanner scanner = new Scanner(System.in);

        System.out.println("Connecting to database...");

        boolean passwordCondition = true;
        while (passwordCondition) {
            System.out.println("\nEnter User ID: ");
            userID = scanner.nextLine();
            System.out.println("Enter User password: ");
            passID = scanner.nextLine();

            if (userID.equals("sa") && passID.equals("root")) {
                passwordCondition = false;
            } else {
                System.out.println("Wrong username or password");
            }
        }

        Connection connection = null;



    }
}
