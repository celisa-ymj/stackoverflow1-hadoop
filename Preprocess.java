import java.io.*;
import java.util.*;

public class Preprocess {

    // Basic English stop word list
    static Set<String> stopWords = new HashSet<>(Arrays.asList(
        "a", "an", "the", "and", "or", "in", "on", "at", "for", "with", 
        "is", "was", "are", "were", "be", "to", "from", "that", "this", 
        "it", "of", "by", "as", "but", "if", "then", "so", "not", "do"
    ));

    // Clean a text field: lowercase, remove punctuation, remove stopwords, trim
    public static String cleanText(String text) {
        if (text == null) return "";
        String[] words = text.toLowerCase()
                             .replaceAll("[^a-z0-9 ]", " ")
                             .trim()
                             .replaceAll("\\s+", " ")
                             .split(" ");

        StringBuilder cleaned = new StringBuilder();
        for (String word : words) {
            if (!stopWords.contains(word) && !word.trim().isEmpty()) {
                cleaned.append(word).append(" ");
            }
        }
        return cleaned.toString().trim();
    }

    public static void main(String[] args) {
        String inputFile = "Questions.csv";            
        String outputFile = "cleaned_Questions.csv";    

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        ) {
            String line = reader.readLine(); // Read header
            if (line == null) {
                System.out.println("Empty file.");
                return;
            }

            // Find indexes of "Title" and "Body" columns
            String[] headers = line.split(",");
            int titleIndex = -1, bodyIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                String col = headers[i].trim().toLowerCase();
                if (col.equals("title")) titleIndex = i;
                if (col.equals("body")) bodyIndex = i;
            }

            if (titleIndex == -1 || bodyIndex == -1) {
                System.out.println("Error: 'Title' or 'Body' column not found.");
                return;
            }

            // Write new header
            writer.write("Title,Body");
            writer.newLine();

            // Process each line
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);  // -1 keeps empty columns
                if (fields.length <= Math.max(titleIndex, bodyIndex)) continue;

                String rawTitle = fields[titleIndex];
                String rawBody = fields[bodyIndex];

                String cleanTitle = cleanText(rawTitle);
                String cleanBody = cleanText(rawBody);

                writer.write("\"" + cleanTitle + "\",\"" + cleanBody + "\"");
                writer.newLine();
            }

            System.out.println("✅ Preprocessing complete. Output saved to: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
