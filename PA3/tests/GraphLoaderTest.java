package edu.csu.pa3;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

public class GraphLoaderTest {
    private static JavaSparkContext sc;
    private GraphLoader loader;

    @BeforeClass
    public static void setupSpark() {
        SparkConf conf = new SparkConf().setAppName("Test").setMaster("local");
        sc = new JavaSparkContext(conf);
    }

    @Before
    public void setupLoader() {
        loader = new GraphLoader(sc);
    }

    @Test
    public void testLoadTitles() {
        String path = "src/test/resources/test_titles.txt";
        JavaPairRDD<Integer, String> titles = loader.loadTitles(path);
        List<String> values = titles.sortByKey().values().collect();

        assertEquals(4, values.size());
        assertEquals("PageA", values.get(0));
        assertEquals("PageD", values.get(3));
    }

    @Test
    public void testLoadLinks() {
        String path = "src/test/resources/test_links.txt";
        JavaPairRDD<Integer, List<Integer>> links = loader.loadLinks(path);
        List<Integer> targets = links.lookup(1).get(0);

        assertEquals(3, targets.size());
        assertTrue(targets.contains(2));
        assertTrue(targets.contains(3));
        assertTrue(targets.contains(4));
    }

    @AfterClass
    public static void tearDown() {
        sc.stop();
    }
}
