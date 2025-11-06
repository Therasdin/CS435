package edu.csu.pa3;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * GraphLoader is responsible for parsing Wikipedia titles and link structure
 * from the provided input files and converting them into Spark RDDs.
 */
public class GraphLoader {
    private final JavaSparkContext sc;

    public GraphLoader(JavaSparkContext sc) {
        this.sc = sc;
    }

    /**
     * Loads the titles file and returns an RDD mapping article ID to title.
     * Article IDs are 1-based (line number).
     *
     * @param path Path to titles-sorted.txt
     * @return JavaPairRDD of (articleId, title)
     */
    public JavaPairRDD<Integer, String> loadTitles(String path) {
        JavaRDD<String> lines = sc.textFile(path);

        return lines.zipWithIndex()
                .mapToPair(pair -> new Tuple2<>((int) (pair._2 + 1), pair._1));
    }

    /**
     * Loads the links file and returns an RDD mapping article ID to a list of linked article IDs.
     *
     * @param path Path to links-simple-sorted.txt
     * @return JavaPairRDD of (sourceArticleId, List of targetArticleIds)
     */
    public JavaPairRDD<Integer, List<Integer>> loadLinks(String path) {
        JavaRDD<String> lines = sc.textFile(path);

        return lines.mapToPair(line -> {
            String[] parts = line.split(":");
            int sourceId = Integer.parseInt(parts[0].trim());

            if (parts.length < 2 || parts[1].trim().isEmpty()) {
            return new Tuple2<>(sourceId, Collections.emptyList());
        }

            List<Integer> targets = Arrays.stream(parts[1].trim().split("\\s+"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            return new Tuple2<>(sourceId, targets);
        });
    }
}
