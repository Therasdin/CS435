package FinalProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * PredictAndRecommend - Lightweight version from GitHub.
 */
public class PredictAndRecommend {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: PredictAndRecommend <csv_file_path> [lyrics]");
            System.exit(1);
        }

        String csvPath = args[0];
        String newLyrics = args.length > 1 ? args[1] : "I'm walking down the lonely road...";

        List<Song> dataset = new ArrayList<>();
        RecordReader reader = new RecordReader();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Song s = reader.parseLine(line);
                if (s != null && s.getLyrics() != null && !s.getLyrics().isBlank()) {
                    dataset.add(s);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading dataset: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }

        System.out.println("Loaded " + dataset.size() + " songs.");

        SongGenreClassifier classifier = new SongGenreClassifier();
        classifier.train(dataset);

        Song newSong = new Song();
        newSong.setId("NEW");
        newSong.setTitle("New Song");
        newSong.setArtist("Unknown");
        newSong.setLyrics(newLyrics);

        String predicted = classifier.predict(newSong);
        System.out.println("Predicted genre: " + predicted);

        List<Song> recs = SongRecommender.recommend(newSong, dataset, 5);

        System.out.println("\nRecommendations:");
        for (Song s : recs) {
            System.out.println("- " + s.getTitle() + " by " + s.getArtist());
        }
    }
}
