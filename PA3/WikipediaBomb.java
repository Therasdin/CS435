package edu.csu.pa3;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * WikipediaBomb simulates a link manipulation attack to boost the PageRank
 * of a target page using links from pages containing a specific keyword.
 */
public class WikipediaBomb {

    private final JavaSparkContext sc;

    public WikipediaBomb(JavaSparkContext sc) {
        this.sc = sc;
    }

    /**
     * Injects artificial links from all pages containing the keyword to the target page.
     *
     * @param titles    RDD of (articleId, title)
     * @param links     Original links RDD
     * @param keyword   Keyword to search in titles (e.g., "surfing")
     * @param targetTitle Exact title of the target page (e.g., "Rocky Mountain National Park")
     * @return Modified links RDD with bomb links added
     */
    public JavaPairRDD<Integer, List<Integer>> injectBombLinks(
            JavaPairRDD<Integer, String> titles,
            JavaPairRDD<Integer, List<Integer>> links,
            String keyword,
            String targetTitle
    ) {
        int targetId = titles.filter(pair -> pair._2.equalsIgnoreCase(targetTitle))
                             .keys()
                             .first();

        List<Integer> keywordIds = titles.filter(pair -> pair._2.toLowerCase().contains(keyword.toLowerCase()))
                                         .keys()
                                         .collect();

        JavaPairRDD<Integer, List<Integer>> bombLinks = sc.parallelizePairs(
                keywordIds.stream()
                          .map(id -> new Tuple2<>(id, Collections.singletonList(targetId)))
                          .collect(Collectors.toList())
        );

        return links.union(bombLinks)
                    .reduceByKey((a, b) -> {
                        List<Integer> combined = new ArrayList<>(a);
                        combined.addAll(b);
                        return combined;
                    });
    }
}
