package FinalProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * PredictAndRecommend - Driver class that loads songs from a CSV,
 * trains the genre classifier, predicts genre for a new song,
 * and recommends similar songs.
 */
public class PredictAndRecommend {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: PredictAndRecommend <csv_file_path> [new_song_lyrics]");
            System.exit(1);
        }

        String csvPath = args[0];
        String newLyrics = args.length > 1 ? args[1] : "I'm walking down the lonely road...";

        List<Song> dataset = new ArrayList<>();
        RecordReader reader = new RecordReader();

        // ------------------------
        // Load dataset from CSV
        // ------------------------
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

        System.out.println("Loaded " + dataset.size() + " songs from dataset.");

        // ------------------------
        // Train genre classifier
        // ------------------------
        SongGenreClassifier classifier = new SongGenreClassifier();

        if (dataset.isEmpty()) {
            System.err.println("Dataset is empty. Cannot train classifier.");
            System.exit(3);
        }

        classifier.train(dataset);
        System.out.println("Genre classifier trained on dataset.");

        // ------------------------
        // Predict genre for new song
        // ------------------------
        Song newSong = new Song();
        newSong.setId("NEW");
        newSong.setTitle("New Song");
        newSong.setArtist("Unknown");
        newSong.setLyrics(newLyrics);

        String predictedGenre = classifier.predict(newSong);
        System.out.println("\nPredicted genre for new song: " + predictedGenre);

        // ------------------------
        // Recommend similar songs
        // ------------------------
        List<Song> recommendations = SongRecommender.recommend(newSong, dataset, 5);

        System.out.println("\nTop 5 recommended songs:");
        for (Song rec : recommendations) {
            System.out.println("- " + rec.getTitle() + " by " + rec.getArtist() + " [" + rec.getTag() + "]");
        }
    }
}

