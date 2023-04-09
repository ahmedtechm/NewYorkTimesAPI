package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
                    JsonNode rootNode = objectMapper.readTree(response.toString());

                    // Extract the articles from the JSON response
                    JsonNode articlesNode = rootNode.path("response").path("docs");

                    // Iterate over the articles and print their titles and URLs
                    for (JsonNode articleNode : articlesNode) {
                        String title = articleNode.path("headline").path("main").asText();
                        String url1 = articleNode.path("web_url").asText();
                        System.out.println(title + " - " + url1);
                    }

                } else {
                    // If there was an error, print the response code
                    System.out.println("Error: " + responseCode);
                }

                sc.close();
            }
}

 