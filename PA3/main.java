package edu.csu.pa3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaPairRDD;

import scala.Tuple2;

import java.util.List;

/**
 * Entry point for the Wikipedia PageRank program.
 *
 * Responsibilities:
 *  - Initialize Spark context
 *  - Load input data (titles and links)
 *  - Run both IdealPageRank and TaxationPageRank
 *  - Save sorted output to files
 */
public class Main {
    public static void main(String[] args) {
        // Validate input arguments
        if (args.length < 3) {
            System.err.println("Usage: Main <titlesFile> <linksFile> <outputDir>");
            System.exit(1);
        }

        String titlesFile = args[0];
        String linksFile = args[1];
        String outputDir = args[2];

        // Initialize Spark configuration and context
        SparkConf conf = new SparkConf().setAppName("WikipediaPageRank");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load graph data from input files
        GraphLoader loader = new GraphLoader(sc);
        JavaPairRDD<Integer, String> titles = loader.loadTitles(titlesFile);
        JavaPairRDD<Integer, List<Integer>> links = loader.loadLinks(linksFile);

        // Run Ideal PageRank algorithm
        IdealPageRank idealPR = new IdealPageRank(sc);
        JavaPairRDD<Integer, Double> idealRanks = idealPR.computePageRank(links, 25);

        // Join with titles and sort by rank descending
        JavaPairRDD<String, Double> idealResults = idealRanks.join(titles)
                .mapToPair(pair -> new Tuple2<>(pair._2._2, pair._2._1))
                .sortByKey(false);

        // Save Ideal PageRank results
        Utils.saveResults(idealResults, outputDir + "/ideal_pagerank");

        // Run Taxation-based PageRank algorithm
        TaxationPageRank taxedPR = new TaxationPageRank(sc);
        JavaPairRDD<Integer, Double> taxedRanks = taxedPR.computePageRank(links, 25, 0.85);

        // Join with titles and sort by rank descending
        JavaPairRDD<String, Double> taxedResults = taxedRanks.join(titles)
                .mapToPair(pair -> new Tuple2<>(pair._2._2, pair._2._1))
                .sortByKey(false);

        // Save Taxation PageRank results
        Utils.saveResults(taxedResults, outputDir + "/taxed_pagerank");

        // Stop Spark context
        sc.stop();
    }
}
