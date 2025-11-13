package edu.csu.pa3;

import java.io.ObjectInputFilter.Config;
import java.util.List;

import org.jcp.xml.dsig.internal.dom.Utils;
 */
public class Main {
    public static void Main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: Main <titlesFile> <linksFile> <outputDir> [bomb]");
            System.exit(1);
        }

        String titlesFile = args[0];
        String linksFile = args[1];
        String outputDir = args[2];
        boolean runBomb = args.length >= 4 && args[3].equalsIgnoreCase("bomb");

        SparkConf conf = new SparkConf().setAppName("WikipediaPageRank");
        JavaSparkContext sc = new JavaSparkContext(conf);

        try {
            GraphLoader loader = new GraphLoader(sc);
            JavaPairRDD<Integer, String> titles = loader.loadTitles(titlesFile);
            JavaPairRDD<Integer, List<Integer>> links = loader.loadLinks(linksFile);

            // Ideal PageRank
            IdealPageRank idealPR = new IdealPageRank(sc);
            JavaPairRDD<Integer, Double> idealRanks = idealPR.computePageRank(links, 25);
            JavaPairRDD<String, Double> idealResults = idealRanks.join(titles)
                    .mapToPair(pair -> new Tuple2<>(pair._2._2, pair._2._1))
                    .sortByKey(false);
            Utils.saveResults(idealResults, Config.IDEAL_OUTPUT_PATH);

            // Taxation PageRank
            TaxationPageRank taxedPR = new TaxationPageRank(sc);
            JavaPairRDD<Integer, Double> taxedRanks = taxedPR.computePageRank(links, 25, 0.85);
            JavaPairRDD<String, Double> taxedResults = taxedRanks.join(titles)
                    .mapToPair(pair -> new Tuple2<>(pair._2._2, pair._2._1))
                    .sortByKey(false);
            Utils.saveResults(taxedResults, outputDir + "/taxed_pagerank");

            // Optional Wikipedia Bomb
            if (runBomb) {
                System.out.println("Wikipedia Bomb activated: injecting links from 'surfing' pages to 'Rocky Mountain National Park'");

                WikipediaBomb bomb = new WikipediaBomb(sc);
                JavaPairRDD<Integer, List<Integer>> bombedLinks = bomb.injectBombLinks(
                        titles,
                        links,
                        "surfing",
                        "Rocky Mountain National Park"
                );

                JavaPairRDD<Integer, Double> bombedRanks = taxedPR.computePageRank(bombedLinks, 25, 0.85);
                JavaPairRDD<String, Double> bombedResults = bombedRanks.join(titles)
                        .mapToPair(pair -> new Tuple2<>(pair._2._2, pair._2._1))
                        .filter(pair -> pair._1.toLowerCase().contains("surfing"))
                        .sortByKey(false);
                Utils.saveResults(bombedResults, outputDir + "/bombed_pagerank");
            }

        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        } finally {
            sc.stop();
        }
    }
}
