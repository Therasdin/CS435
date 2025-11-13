package edu.csu.pa3.constants;

/**
 * Config holds global constants used across the Wikipedia PageRank project.
 */
public class Config {

    // Default damping factor for taxation-based PageRank
    public static final double DEFAULT_DAMPING_FACTOR = 0.85;

    // Default number of iterations for PageRank computation
    public static final int DEFAULT_ITERATIONS = 25;

    // Default output file names
    public static final String IDEAL_OUTPUT_PATH = "output/ideal_pagerank.txt";
    public static final String TAXED_OUTPUT_PATH = "output/taxed_pagerank.txt";
}
