import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Preprocess {

    public static void main(String[] args) {
        String inputFile = "Questions.csv";
        String outputFile = "cleaned_Questions.txt";
        String stopwordFile = "stopwords.txt";

        Set<String> stopWords = loadStopWords(stopwordFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\",\"", -1);

                if (parts.length >= 2) {
                    String title = parts[0].replaceAll("^\"|\"$", "");
                    String body = parts[1].replaceAll("^\"|\"$", "");

                    String combined = title + " " + body;

                    // Remove HTML tags
                    combined = combined.replaceAll("<[^>]*>", " ");

                    // Lowercase
                    combined = combined.toLowerCase();

                    // Remove punctuation
                    combined = combined.replaceAll("[^a-z0-9\\s]", " ");

                    // Tokenize and remove stopwords
                    StringBuilder cleaned = new StringBuilder();
                    for (String word : combined.split("\\s+")) {
                        if (!stopWords.contains(word.trim()) && !word.trim().isEmpty()) {
                            cleaned.append(word).append(" ");
                        }
                    }

                    writer.write(cleaned.toString().trim());
                    writer.newLine();
                }
            }

            System.out.println("✅ Preprocessing complete. Output saved to " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> loadStopWords(String filename) {
        Set<String> stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String word;
            while ((word = reader.readLine()) != null) {
                stopWords.add(word.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }
}
