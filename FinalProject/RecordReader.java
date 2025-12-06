package FinalProject;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

/**
 * RecordReader - Hadoop Mapper that converts CSV lines into Song objects.
 * Handles messy CSV fields (quotes, commas inside lyrics).
 */
public class RecordReader extends Mapper<LongWritable, Text, Text, Text> {

    private CSVParser parser;

    @Override
    protected void setup(Context context) {
        this.parser = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .withEscapeChar('\\')
                .build();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        try {
            String[] fields = parser.parseLine(value.toString());

            // Ignore malformed lines
            if (fields.length < 11) {
                return;
            }

            Song s = new Song();
            s.setTitle(fields[0]);
            s.setTag(fields[1]);
            s.setArtist(fields[2]);
            s.setYear(fields[3]);

            try {
                s.setViews(Long.parseLong(fields[4]));
            } catch (Exception e) {
                s.setViews(0);
            }

            s.setFeatures(fields[5]);
            s.setLyrics(fields[6]);
            s.setId(fields[7]);
            s.setLanguage(fields[10]); // final “language” column

            // Emit songId as key, and a serialized representation as value
            context.write(new Text(s.getId()), new Text(s.toString()));

        } catch (Exception e) {
            // Skip malformed line
        }
    }
}
