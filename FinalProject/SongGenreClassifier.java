package FinalProject;

import java.util.*;

/**
 * SongGenreClassifier - Classifies songs by genre based on lyrics using TF-IDF and Naive Bayes.
 * This version is plain Java (no Spark), so it can be integrated into Hadoop workflows.
 */
public class SongGenreClassifier {

    private final Map<String, Map<String, Double>> classWordWeights = new HashMap<>();
    private final Map<String, Integer> classCounts = new HashMap<>();
    private int totalDocs = 0;

    /**
     * Train the classifier on a list of songs.
     */
    public void train(List<Song> songs) {
        for (Song s : songs) {
            if (s.getTag() == null || s.getLyrics() == null) continue;

            String genre = s.getTag().toLowerCase();
            classCounts.put(genre, classCounts.getOrDefault(genre, 0) + 1);
            totalDocs++;

            Map<String, Integer> tf = termFrequency(s.getLyrics());
            Map<String, Double> weights = classWordWeights.computeIfAbsent(genre, g -> new HashMap<>());

            for (Map.Entry<String, Integer> e : tf.entrySet()) {
                weights.put(e.getKey(), weights.getOrDefault(e.getKey(), 0.0) + e.getValue());
            }
        }
    }

    /**
     * Predict the genre of a new song.
     */
    public String predict(Song song) {
        if (song.getLyrics() == null) return "unknown";

        Map<String, Integer> tf = termFrequency(song.getLyrics());
        String bestClass = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (String genre : classWordWeights.keySet()) {
            double logPrior = Math.log((double) classCounts.get(genre) / totalDocs);
            double score = logPrior;

            Map<String, Double> weights = classWordWeights.get(genre);
            for (Map.Entry<String, Integer> e : tf.entrySet()) {
                double weight = weights.getOrDefault(e.getKey(), 0.0);
                score += e.getValue() * weight;
            }

            if (score > bestScore) {
                bestScore = score;
                bestClass = genre;
            }
        }
        return bestClass == null ? "unknown" : bestClass;
    }

    /**
     * Utility: compute term frequency map for lyrics.
     */
    private Map<String, Integer> termFrequency(String text) {
        Map<String, Integer> tf = new HashMap<>();
        if (text == null) return tf;

        String[] tokens = text.toLowerCase().replaceAll("[^a-z0-9 ]", " ").split("\\s+");
        for (String t : tokens) {
            if (!t.isBlank()) {
                tf.put(t, tf.getOrDefault(t, 0) + 1);
            }
        }
        return tf;
    }
}
