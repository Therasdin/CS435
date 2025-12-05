package FinalProject;

import java.util.Collections;
import java.util.Iterator;

import org.apache.spark.api.java.function.FlatMapFunction;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

/**
 * RecordReader - Converts CSV lines into Song objects
 * Handles messy CSV fields (quotes, commas inside lyrics)
 */
public class RecordReader implements FlatMapFunction<String, Song> {

    private final CSVParser parser;

    public RecordReader() {
        this.parser = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .withEscapeChar('\\')
                .build();
    }

    @Override
    public Iterator<Song> call(String line) {

        try {
            String[] fields = parser.parseLine(line);

            // Ignore malformed lines
            if (fields.length < 10) {
                return Collections.emptyIterator();
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

            return Collections.singletonList(s).iterator();

        } catch (Exception e) {
            return Collections.emptyIterator();
        }
    }
}
