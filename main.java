package edu.csu.pa3;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;

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
        SparkConf conf = new SparkConf().setAppName("WikipediaPageRank");

        // TODO: Load data using GraphLoader
        // TODO: Run IdealPageRank and TaxationPageRank
        // TODO: Save results using Utils

    }
}