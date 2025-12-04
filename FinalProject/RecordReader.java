package FinalProject;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * RecordReader for parsing Genius Song Lyrics CSV
 * Handles multi-line lyrics and quoted fields
 */
public class RecordReader implements FlatMapFunction<String, Song> {
    
    private static final int EXPECTED_FIELDS = 11;
    
    @Override
    public Iterator<Song> call(String line) throws Exception {
        List<Song> songs = new ArrayList<>();
        
        // Skip header line
        if (line.startsWith("title,tag,artist")) {
            return songs.iterator();
        }
        
        try {
            // Parse CSV line using OpenCSV (handles quotes and escapes)
            CSVReader reader = new CSVReader(new StringReader(line));
            String[] fields = reader.readNext();
            reader.close();
            
            if (fields == null || fields.length < EXPECTED_FIELDS) {
                // Skip malformed lines
                return songs.iterator();
            }
            
            // Extract fields according to CSV structure:
            // title,tag,artist,year,views,features,lyrics,id,language_cld3,language_ft,language
            String title = fields[0].trim();
            String tag = fields[1].trim();           // Genre
            String artist = fields[2].trim();
            
            int year = parseIntSafe(fields[3]);
            long views = parseLongSafe(fields[4]);
            
            String featuresStr = fields[5].trim();
            List<String> features = Song.parseFeatures(featuresStr);
            
            String lyrics = fields[6].trim();
            String id = fields[7].trim();
            String languageCld3 = fields[8].trim();
            String languageFt = fields[9].trim();
            String language = fields[10].trim();
            
            // Create Song object if required fields are present
            if (!title.isEmpty() && !lyrics.isEmpty() && !tag.isEmpty()) {
                Song song = new Song(
                    title, tag, artist, year, views,
                    features, lyrics, id,
                    languageCld3, languageFt, language
                );
                songs.add(song);
            }
            
        } catch (CsvValidationException | IOException e) {
            System.err.println("Error parsing line: " + e.getMessage());
        }
        
        return songs.iterator();
    }
    
    /**
     * Safely parse integer, return 0 if invalid
     */
    private int parseIntSafe(String value) {
        try {
            return value.isEmpty() ? 0 : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Safely parse long, return 0 if invalid
     */
    private long parseLongSafe(String value) {
        try {
            return value.isEmpty() ? 0L : Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}