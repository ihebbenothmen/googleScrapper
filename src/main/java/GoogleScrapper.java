import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class GoogleScrapper {

    public static String makeHttpRequest(String searchQuery) {
        StringBuilder output = new StringBuilder();
        try {
            String url = "https://www.google.com/search?q=" + searchQuery.replace(" ", "+");
            System.out.println("Fetching URL: " + url);

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")
                    .timeout(100000)
                    .get();

            Elements searchResults = doc.select("h3"); 
            Elements links = doc.select("a[href]"); 

            for (int i = 0; i < Math.min(searchResults.size(), 10); i++) {
                String title = searchResults.get(i).text();
                String link = links.get(i).attr("href");

                output.append("Title: ").append(title).append("\n");
                output.append("Link: ").append(link).append("\n\n");
            }
        } catch (IOException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            e.printStackTrace();
        }
        return output.toString();
    }

    public static void appendToFile(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Results saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String stripHtml(String html) {
        return html.replaceAll("<[^>]*>", "").trim();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a search query: ");
        String userData = scanner.nextLine();
        scanner.close();

        System.out.println("Searching for: " + userData);
        String results = makeHttpRequest(userData);

        if (!results.isEmpty()) {
            appendToFile("search_results.json", results);
        } else {
            System.out.println("No results found or an error occurred.");
        }
    }
}
