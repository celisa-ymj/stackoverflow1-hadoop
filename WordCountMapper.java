import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        // --- Common English Stopwords ---
        "a", "an", "and", "are", "as", "at", "am", "be", "been", "but", "by", "can", "could",
        "did", "do", "does", "doing", "don", "down", "for", "from", "had", "has", "have",
        "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how",
        "i", "if", "in", "into", "is", "it", "its", "itself", "just", "me", "more", "most",
        "must", "my", "myself", "no", "nor", "not", "of", "on", "only", "or", "other", "our",
        "ours", "ourselves", "out", "over", "own", "same", "she", "should", "so", "some",
        "such", "than", "that", "the", "their", "theirs", "them", "themselves", "then", "there",
        "these", "they", "this", "those", "through", "to", "too", "try", "trying", "tried", "under", "until", "up", "very",
        "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why",
        "will", "with", "would", "you", "your", "yours", "yourself", "yourselves", "way",

        // --- Informal / Contractions & Personal Pronouns ---
        "want", "like", "get", "got", "see", "know", "think", "im", "ive", "u", "am",
        "dont", "doesnt", "cant", "cannot", "sure",

        // --- HTML/XML Tags & Attributes ---
        "p", "br", "div", "span", "li", "ul", "ol", "pre", "code", "var", "img", "td", "tr", "table",

        // --- HTML Entities & Encoding Artifacts ---
        "amp", "lt", "gt", "quot", "apos", "nbsp", "copy", "reg", "ltdiv", "ltdivgt", "ltinput", "ltlt", "ampamp",

        // --- Web & URL Noise ---
        "www", "com", "org", "net",

        // --- Placeholder / Low-Value Words ---
        "one", "two", "first", "last", "next", "end", "now", "also", "well", "yes", "ok", "etc", "vs",

        // --- Stack Overflow & Programming Noise ---
        "stackoverflow", "question", "answer", "post", "vote", "comment", "tag",
        "file", "line", "input", "output", "run", "print", "code", "error", "issue", "problem",
        "help", "thanks", "thank", "hello", "hi",

        // --- Generic Tech/Product Names ---
        "java", "python", "javascript", "csharp", "php", "ruby", "windows", "linux", "mac", "sql", "mysql",
        "oracle", "android", "ios", "chrome", "firefox", "edge", "git", "github",

        // --- Redundant Terms ---
        "app", "application", "project",

        // --- Junk Data / Artefacts ---
        "tznahow",

        // --- Single Letters / Likely Noise ---
        "x", "y", "s", "d", "f", "l", "e", "n", "t", "b", "v", "i", "j", "k"
    ));

    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]*>");
    private static final Pattern NON_WORD = Pattern.compile("[^a-zA-Z]");

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Remove HTML tags
        String line = HTML_TAGS.matcher(value.toString()).replaceAll(" ");

        // Tokenise and clean each word
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().toLowerCase();
            token = NON_WORD.matcher(token).replaceAll(""); // Remove digits/punctuation

            if (token.isEmpty() || STOPWORDS.contains(token)) {
                continue;
            }

            word.set(token);
            context.write(word, one);
        }
    }
}
