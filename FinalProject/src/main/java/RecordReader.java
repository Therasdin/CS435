package FinalProject;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

/**
 * Simple RecordReader for Final Project.
 * Parses a CSV line into a Song object (NOT Spark / NOT Hadoop).
 */
public class RecordReader {

    private final CSVParser parser;

    public RecordReader() {
        this.parser = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .withEscapeChar('\\')
                .build();
    }

    public Song parseLine(String line) {
        try {
            String[] fields = parser.parseLine(line);

            if (fields.length < 10) return null;

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
            s.setLanguage(fields[10]);

            return s;

        } catch (Exception e) {
            return null;
        }
    }
}
