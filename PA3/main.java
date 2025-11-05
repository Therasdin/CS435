package edu.csu.pa3;

/**
 * Entry point for the Wikipedia PageRank program.
 * 
 * Responsibilities:
 *  - Initialize Spark context
 *  - Load input data (titles and links)
 *  - Run both IdealPageRank and TaxationPageRank
 *  - Save sorted output to files
 */
public class main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("WikipediaPageRank");

        // TODO: Load data using GraphLoader
        // TODO: Run IdealPageRank and TaxationPageRank
        // TODO: Save results using Utils

    }
}