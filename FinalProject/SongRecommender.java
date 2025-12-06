package FinalProject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SongRecommender - recommends top K similar songs based on lyric content.
 * Uses TF vectors and cosine similarity.
 */
public class SongRecommender {

    // ------------------------
    // 1. Compute TF map
    // ------------------------
    private static final Set<String> STOPWORDS = Set.of(
        "the", "and", "is", "a", "to", "in", "of", "on", "for", "with"
    );

    private static Map<String, Integer> termFrequency(String text) {
        Map<String, Integer> tf = new HashMap<>();
        if (text == null) return tf;

        String[] tokens = text.toLowerCase()
                              .replaceAll("[^a-z0-9 ]", " ")
                              .split("\\s+");

        for (String t : tokens) {
            if (t.isBlank() || STOPWORDS.contains(t)) continue;
            tf.put(t, tf.getOrDefault(t, 0) + 1);
        }
        return tf;
    }

    // ------------------------
    // 2. Build vocabulary
    // ------------------------
    private static Set<String> buildVocab(List<Song> songs) {
        Set<String> vocab = new HashSet<>();
        for (Song s : songs) {
            if (s.getLyrics() != null) {
                vocab.addAll(termFrequency(s.getLyrics()).keySet());
            }
        }
        return vocab;
    }

    // ------------------------
    // 3. Convert TF maps into TF vectors
    // ------------------------
    private static double[] buildTFVector(Map<String, Integer> tf, List<String> vocabList) {
        double[] vec = new double[vocabList.size()];
        for (int i = 0; i < vocabList.size(); i++) {
            vec[i] = tf.getOrDefault(vocabList.get(i), 0);
        }
        return vec;
    }

    // ------------------------
    // 4. Cosine similarity
    // ------------------------
    private static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    // ------------------------
    // 5. Recommend top K similar songs
    // ------------------------
    public static List<Song> recommend(Song target, List<Song> allSongs, int k) {
        if (target == null || target.getLyrics() == null) return Collections.emptyList();

        // Build vocabulary across all songs
        Set<String> vocab = buildVocab(allSongs);
        List<String> vocabList = new ArrayList<>(vocab);

        // Target vector
        double[] targetVec = buildTFVector(termFrequency(target.getLyrics()), vocabList);

        // Compute similarity for every song
        List<Map.Entry<Song, Double>> scored = new ArrayList<>();
        for (Song s : allSongs) {
            if (s.getId() != null && s.getId().equals(target.getId())) continue;
            if (s.getLyrics() == null) continue;

            double[] vec = buildTFVector(termFrequency(s.getLyrics()), vocabList);
            double sim = cosineSimilarity(targetVec, vec);
            scored.add(Map.entry(s, sim));
        }

        // Sort by similarity descending
        return scored.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Optional: return scores alongside songs
    public static List<Map.Entry<Song, Double>> recommendWithScores(Song target, List<Song> allSongs, int k) {
        if (target == null || target.getLyrics() == null) return Collections.emptyList();

        Set<String> vocab = buildVocab(allSongs);
        List<String> vocabList = new ArrayList<>(vocab);

        double[] targetVec = buildTFVector(termFrequency(target.getLyrics()), vocabList);

        List<Map.Entry<Song, Double>> scored = new ArrayList<>();
        for (Song s : allSongs) {
            if (s.getId() != null && s.getId().equals(target.getId())) continue;
            if (s.getLyrics() == null) continue;

            double[] vec = buildTFVector(termFrequency(s.getLyrics()), vocabList);
            double sim = cosineSimilarity(targetVec, vec);
            scored.add(Map.entry(s, sim));
        }

        return scored.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(k)
                .collect(Collectors.toList());
    }
}
