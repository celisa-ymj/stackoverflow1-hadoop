package stubs;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if (key.get() == 0 && line.startsWith("Id,")) return; // Skip header

        try {
            String[] fields = line.split("\",\"", -1);

            String title = fields[5].replaceAll("<[^>]*>", "").toLowerCase(); // remove HTML
            String body = fields[6].replaceAll("<[^>]*>", "").toLowerCase(); // remove HTML

            StringTokenizer tokenizer = new StringTokenizer(title + " " + body);

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().replaceAll("[^a-zA-Z]", "");
                if (token.length() > 2) {
                    word.set(token);
                    context.write(word, one);
                }
            }
        } catch (Exception e) {
            // Skip malformed lines
        }
    }
}
