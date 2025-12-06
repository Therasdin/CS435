package FinalProject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SongRecommender - recommends top K similar songs using TF-IDF + cosine similarity.
 */
public class SongRecommender {

    private static final Set<String> STOPWORDS = Set.of(
        "the", "and", "is", "a", "to", "in", "of", "on", "for", "with"
    );

    // ------------------------
    // 1. Term Frequency
    // ------------------------
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
    // 2. Build Vocabulary & Document Frequency
    // ------------------------
    private static Map<String, Integer> buildDocFrequency(List<Song> songs) {
        Map<String, Integer> df = new HashMap<>();
        for (Song s : songs) {
            if (s.getLyrics() == null) continue;

            Set<String> seen = termFrequency(s.getLyrics()).keySet();
            for (String word : seen) {
                df.put(word, df.getOrDefault(word, 0) + 1);
            }
        }
        return df;
    }

    // ------------------------
    // 3. Build TF-IDF Vector
    // ------------------------
    private static double[] buildTFIDFVector(
            Map<String, Integer> tf,
            List<String> vocab,
            Map<String, Integer> df,
            int N
    ) {
        double[] vec = new double[vocab.size()];

        for (int i = 0; i < vocab.size(); i++) {
            String word = vocab.get(i);
            int termCount = tf.getOrDefault(word, 0);
            int docCount = df.getOrDefault(word, 1);

            double tfValue = termCount;
            double idf = Math.log((double) N / docCount);

            vec[i] = tfValue * idf;
        }
        return vec;
    }

    // ------------------------
    // 4. Cosine Similarity
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
    // 5. Recommend Top K Songs
    // ------------------------
    public static List<Song> recommend(Song target, List<Song> allSongs, int k) {
        if (target == null || target.getLyrics() == null) return Collections.emptyList();

        // Build IDF
        Map<String, Integer> df = buildDocFrequency(allSongs);
        List<String> vocabList = new ArrayList<>(df.keySet());
        int N = Math.max(1, allSongs.size());

        // Target TF-IDF vector
        double[] targetVec = buildTFIDFVector(
                termFrequency(target.getLyrics()), vocabList, df, N);

        // Score all songs
        List<Map.Entry<Song, Double>> scored = new ArrayList<>();
        for (Song s : allSongs) {
            if (s.getId() != null && s.getId().equals(target.getId())) continue;
            if (s.getLyrics() == null) continue;

            double[] vec = buildTFIDFVector(
                    termFrequency(s.getLyrics()), vocabList, df, N);

            double sim = cosineSimilarity(targetVec, vec);
            scored.add(Map.entry(s, sim));
        }

        return scored.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // ------------------------
    // 6. Recommend with Scores (for debugging/reporting)
    // ------------------------
    public static List<Map.Entry<Song, Double>> recommendWithScores(
            Song target, List<Song> allSongs, int k) {

        if (target == null || target.getLyrics() == null) return Collections.emptyList();

        Map<String, Integer> df = buildDocFrequency(allSongs);
        List<String> vocabList = new ArrayList<>(df.keySet());
        int N = Math.max(1, allSongs.size());

        double[] targetVec = buildTFIDFVector(
                termFrequency(target.getLyrics()), vocabList, df, N);

        List<Map.Entry<Song, Double>> scored = new ArrayList<>();
        for (Song s : allSongs) {
            if (s.getId() != null && s.getId().equals(target.getId())) continue;
            if (s.getLyrics() == null) continue;

            double[] vec = buildTFIDFVector(
                    termFrequency(s.getLyrics()), vocabList, df, N);

            double sim = cosineSimilarity(targetVec, vec);
            scored.add(Map.entry(s, sim));
        }

        return scored.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(k)
                .collect(Collectors.toList());
    }
}
