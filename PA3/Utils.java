package edu.csu.pa3;

import org.apache.spark.api.java.JavaPairRDD;

/**
 * Utility class for formatting and saving PageRank results.
 */
public class Utils {

    /**
     * Saves the PageRank results to a text file.
     *
     * @param results    RDD of (title, rank)
     * @param outputPath Path to output file
     */
    public static void saveResults(JavaPairRDD<String, Double> results, String outputPath) {
    results
        .map(entry -> formatTitle(entry._1) + "\t" + formatRank(entry._2))
        .coalesce(1) // optional: make one output file instead of many
        .saveAsTextFile(outputPath);
    }

    /**
     * Formats article titles for readability.
     *
     * @param rawTitle Raw title from input
     * @return Cleaned title
     */
    public static String formatTitle(String rawTitle) {
        return rawTitle.replace("_", " ").trim();
    }

    /**
     * Formats PageRank values to 6 decimal places.
     *
     * @param rank PageRank value
     * @return Formatted string
     */
    public static String formatRank(double rank) {
        return String.format("%.6f", rank);
    }
}
