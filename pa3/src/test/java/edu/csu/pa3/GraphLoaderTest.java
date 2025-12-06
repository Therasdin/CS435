package edu.csu.pa3.tests;

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
        SparkConf conf = new SparkConf()
                .setAppName("GraphLoaderTest")
                .setMaster("local[*]"); // Use all cores for parallelism
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

        assertEquals("Expected 4 titles", 4, values.size());
        assertEquals("First title should be PageA", "PageA", values.get(0));
        assertEquals("Last title should be PageD", "PageD", values.get(3));
    }

    @Test
    public void testLoadLinks() {
        String path = "src/test/resources/test_links.txt";
        JavaPairRDD<Integer, List<Integer>> links = loader.loadLinks(path);
        List<Integer> targets = links.lookup(1).get(0);

        assertEquals("Expected 3 links from article 1", 3, targets.size());
        assertTrue("Should contain link to article 2", targets.contains(2));
        assertTrue("Should contain link to article 3", targets.contains(3));
        assertTrue("Should contain link to article 4", targets.contains(4));
    }

    @Test
    public void testEmptyLinksLine() {
        String path = "src/test/resources/test_links_empty.txt";
        JavaPairRDD<Integer, List<Integer>> links = loader.loadLinks(path);
        List<Integer> targets = links.lookup(5).get(0); // Assuming line "5:" exists

        assertTrue("Article 5 should have no links", targets.isEmpty());
    }

    @AfterClass
    public static void tearDown() {
        if (sc != null) {
            sc.stop();
            sc.close();
        }
    }
}
