package edu.csu.pa3;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

import java.util.*;

/**
 * IdealPageRank computes PageRank values assuming every page has at least one outbound link.
 * It does not handle dead ends or apply damping.
 */
public class IdealPageRank {
    private final JavaSparkContext sc;

    public IdealPageRank(JavaSparkContext sc) {
        this.sc = sc;
    }

    /**
     * Computes PageRank values using the idealized algorithm (no dead ends, no damping).
     *
     * @param links      RDD of (sourceId, List of targetIds)
     * @param iterations Number of iterations to run
     * @return RDD of (articleId, PageRank value)
     */
    public JavaPairRDD<Integer, Double> computePageRank(JavaPairRDD<Integer, List<Integer>> links, int iterations) {
        // Get all unique article IDs
        JavaRDD<Integer> allIds = links.keys().union(links.values().flatMap(List::iterator)).distinct();

        long totalPages = allIds.count();

        // Initialize each page with equal rank
        JavaPairRDD<Integer, Double> ranks = allIds.mapToPair(id -> new Tuple2<>(id, 1.0 / totalPages));

        // Ensure every page has an entry in the links RDD (even if it has no outbound links)
        JavaPairRDD<Integer, List<Integer>> completeLinks = allIds.mapToPair(id -> new Tuple2<>(id, new ArrayList<>()))
                .union(links)
                .reduceByKey((a, b) -> {
                    List<Integer> combined = new ArrayList<>(a);
                    combined.addAll(b);
                    return combined;
                });

        // Iteratively compute PageRank
        for (int i = 0; i < iterations; i++) {
            // Distribute rank contributions
            JavaPairRDD<Integer, Double> contributions = completeLinks.join(ranks)
                    .flatMapToPair(pair -> {
                        Integer sourceId = pair._1;
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

            // Sum contributions to get new ranks
            ranks = contributions.reduceByKey(Double::sum);
        }

        return ranks;
    }
}
