package edu.csu.pa3;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

import java.util.*;

/**
 * TaxationPageRank computes PageRank values using the damping factor and redistribution from dead-end pages.
 */
public class TaxationPageRank {
    private final JavaSparkContext sc;

    public TaxationPageRank(JavaSparkContext sc) {
        this.sc = sc;
    }

    /**
     * Computes PageRank values using the taxation-based algorithm.
     *
     * @param links          RDD of (sourceId, List of targetIds)
     * @param iterations     Number of iterations to run
     * @param dampingFactor  Damping factor (e.g., 0.85)
     * @return RDD of (articleId, PageRank value)
     */
    public JavaPairRDD<Integer, Double> computePageRank(JavaPairRDD<Integer, List<Integer>> links, int iterations, double dampingFactor) {
        // Get all unique article IDs
        JavaRDD<Integer> allIds = links.keys()
                .union(links.values().flatMap(List::iterator))
                .distinct()
                .cache();

        long totalPages = allIds.count();
        if (totalPages == 0) {
            return sc.emptyRDD().mapToPair(x -> new Tuple2<>(x, 0.0));
        }

        // Initialize each page with equal rank
        JavaPairRDD<Integer, Double> ranks = allIds.mapToPair(id -> new Tuple2<>(id, 1.0 / totalPages));

        // Ensure every page has an entry in the links RDD
        JavaPairRDD<Integer, List<Integer>> completeLinks = allIds.mapToPair(id -> new Tuple2<>(id, Collections.emptyList()))
                .union(links)
                .reduceByKey((a, b) -> {
                    List<Integer> combined = new ArrayList<>(a);
                    combined.addAll(b);
                    return combined;
                });

        for (int i = 0; i < iterations; i++) {
            // Identify dead-end pages (no outbound links)
            Set<Integer> deadEnds = new HashSet<>(completeLinks
                    .filter(pair -> pair._2.isEmpty())
                    .keys()
                    .collect());

            // Compute total rank from dead ends
            double deadEndRank = ranks
                    .filter(pair -> deadEnds.contains(pair._1))
                    .values()
                    .reduce((a, b) -> a + b);

            // Distribute rank contributions
            JavaPairRDD<Integer, Double> contributions = completeLinks.join(ranks)
                    .flatMapToPair(pair -> {
                        List<Integer> targets = pair._2._1;
                        Double rank = pair._2._2;

                        List<Tuple2<Integer, Double>> results = new ArrayList<>();
                        if (!targets.isEmpty()) {
                            double share = rank / targets.size();
                            for (Integer target : targets) {
                                results.add(new Tuple2<>(target, share));
                            }
                        }
                        return results.iterator();
                    });

            // Sum contributions and apply damping + dead-end redistribution
            double redistribution = dampingFactor * deadEndRank / totalPages;
            ranks = contributions
                    .reduceByKey(Double::sum)
                    .mapValues(contrib -> (1 - dampingFactor) / totalPages + dampingFactor * contrib + redistribution);
        }

        return ranks;
    }
}
