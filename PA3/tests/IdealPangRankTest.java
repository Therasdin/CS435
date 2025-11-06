package edu.csu.pa3;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.junit.*;

import scala.Tuple2;

import java.util.*;

import static org.junit.Assert.*;

public class IdealPageRankTest {
    private static JavaSparkContext sc;

    @BeforeClass
    public static void setupSpark() {
        SparkConf conf = new SparkConf().setAppName("Test").setMaster("local");
        sc = new JavaSparkContext(conf);
    }

    @Test
    public void testIdealPageRankSmallGraph() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(1, Arrays.asList(2, 3));
        graph.put(2, Arrays.asList(3));
        graph.put(3, Arrays.asList(1));
        graph.put(4, Arrays.asList(1, 2, 3));

        JavaPairRDD<Integer, List<Integer>> links = sc.parallelizePairs(new ArrayList<>(graph.entrySet()));
        IdealPageRank pr = new IdealPageRank(sc);
        JavaPairRDD<Integer, Double> ranks = pr.computePageRank(links, 10);

        Map<Integer, Double> result = ranks.collectAsMap();
        assertEquals(4, result.size());
        assertTrue(result.get(1) > 0);
        assertTrue(result.get(3) > result.get(2)); // Page 3 gets more links
    }

    @AfterClass
    public static void tearDown() {
        sc.stop();
    }
}
