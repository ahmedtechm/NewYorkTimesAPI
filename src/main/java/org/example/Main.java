package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

            public static void main(String[] args) throws Exception {
                Scanner sc = new Scanner(System.in);

                // Prompt user to enter a keyword or category
                System.out.print("Enter a keyword or category: ");
                String query = sc.nextLine();

                // Encode the user's query to be used in the API URL
                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                String urlString = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=" + encodedQuery + "&api-key=aAGt1KRSxCjQDzmvPGYMzLzPNFitIGvK";

                // Create a URL object and open a connection to it
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                // Send a GET request to the API and retrieve the response code
                int responseCode = con.getResponseCode();

                // If the response code is 200 OK, read the response and print it
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response.toString());

// Extract the relevant information from the JSON response
                    JsonNode docsNode = jsonNode.get("response").get("docs");
                    for (JsonNode docNode : docsNode) {

                        String ID = docNode.get("ID").asText();
                        String title = docNode.get("Title").asText();
                        String author = docNode.get("Author").asText();
                        String date = docNode.get("Date").asText();
                        String category = docNode.get("Category").asText();
                        String content = docNode.get("Content").asText();

                        // Insert the relevant information into the SQL Server database using JDBC
                        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ArticlesDB", "sa", "root")) {
                            PreparedStatement stmt = conn.prepareStatement("INSERT INTO articles (ID, Title, Author, Date, Category, Content) VALUES (?, ?, ?, ?, ?, ?)");
                            stmt.setString(1, ID);
                            stmt.setString(2, title);
                            stmt.setString(3, author);
                            stmt.setString(4, date);
                            stmt.setString(5, category);
                            stmt.setString(6, content);
                            stmt.executeUpdate();
                        } catch (SQLException e) {
                            // Handle the exception appropriately
                        }
                    }

                } else {
                    // If there was an error, print the response code
                    System.out.println("Error: " + responseCode);
                }

                sc.close();
            }
}

 