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

                    // Parse the JSON response using Jackson
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response.toString());

                    // Extract the relevant information from the JSON response
                    JsonNode docsNode = jsonNode.get("response").get("docs");
                    for (JsonNode docNode : docsNode) {

                        String abstractItem = docNode.get("abstract").get("main").asText();
                        String webUrl = docNode.get("web_url").asText();
                        String snippet = docNode.get("snippet").asText();
                        String leadParagraph = docNode.get("lead_paragraph").asText();
                        String printSection = docNode.get("print_section").asText();
                        String printPage = docNode.get("print_page").asText();
                        String source = docNode.get("source").asText();

                        // Insert the relevant information into the SQL Server database using JDBC
                        Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ArticlesDB", "sa", "root");
                        PreparedStatement stmt = conn.prepareStatement("INSERT INTO docs (abstract, webUrl, snippet, leadParagraph, printSection, printPage,source) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        stmt.setString(1, abstractItem);
                        stmt.setString(2, webUrl);
                        stmt.setString(3, snippet);
                        stmt.setString(4, leadParagraph);
                        stmt.setString(5, printSection);
                        stmt.setString(6, printPage);
                        stmt.setString(7, source);
                        stmt.executeUpdate();
                        stmt.close();
                        conn.close();
                    }




                } else {
                    // If there was an error, print the response code
                    System.out.println("Error: " + responseCode);
                }

                sc.close();
            }
}

 