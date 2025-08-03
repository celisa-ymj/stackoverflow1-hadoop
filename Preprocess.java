import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Preprocess {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Preprocess <input.csv> <stopwords.txt> <output.txt>");
            return;
        }

        String inputCsv = args[0];
        String stopwordsFile = args[1];
        String outputFile = args[2];

        Set<String> stopwords = loadStopwords(stopwordsFile);

        try (BufferedReader br = new BufferedReader(new FileReader(inputCsv));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);  // allows empty fields
                if (parts.length < 2) continue;

                String title = parts[0];
                String body = parts[1];

                String combined = title + " " + body;
                String cleaned = cleanText(combined);
                List<String> words = tokenize(cleaned);

                for (String word : words) {
                    word = word.toLowerCase();
                    if (!stopwords.contains(word) && word.length() > 1 && !isNumeric(word)) {
                        bw.write(word);
                        bw.newLine();
                    }
                }
            }

            System.out.println("Preprocessing complete. Output written to: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load stopwords into a HashSet
    public static Set<String> loadStopwords(String stopwordsFile) {
        Set<String> stopwords = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(stopwordsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                stopwords.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopwords;
    }

    // Remove HTML, punctuation, and special characters
    public static String cleanText(String text) {
        text = text.replaceAll("<[^>]*>", " "); // remove HTML tags
        text = text.replaceAll("&[^;]+;", " "); // remove HTML entities like &amp;
        text = text.replaceAll("[^a-zA-Z0-9 ]", " "); // remove punctuation/symbols
        text = text.replaceAll("\\s+", " "); // normalize whitespace
        return text;
    }

    // Tokenize text into individual words
    public static List<String> tokenize(String text) {
        return Arrays.asList(text.trim().split(" "));
    }

    // Check if word is numeric
    public static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }
}
