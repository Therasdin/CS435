package edu.csu.pa3;

import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
        List<Tuple2<String, Double>> collected = results.collect();

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            for (Tuple2<String, Double> entry : collected) {
                String title = formatTitle(entry._1);
                String rank = formatRank(entry._2);
                writer.println(title + "\t" + rank);
            }
        } catch (IOException e) {
            System.err.println("Error writing results to " + outputPath);
            e.printStackTrace();
        }
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
