package FinalProject;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Song Genre Classifier - CS435 Final Project
 * Classifies songs by genre based on lyrics using TF-IDF and Logistic Regression
 */
public class SongGenreClassifier {
    
    public static void main(String[] args) {
        
        if (args.length < 1) {
            System.err.println("Usage: SongGenreClassifier <csv_file_path> [sample_fraction]");
            System.exit(1);
        }
        
        String csvPath = args[0];
        double sampleFraction = args.length > 1 ? Double.parseDouble(args[1]) : 0.01;
        
        System.out.println("Song Genre Classification - CS435 Final Project");
        System.out.println("================================================\n");
        
        // Set up Spark
        SparkConf conf = new SparkConf()
            .setAppName("SongGenreClassifier")
            .setMaster("local[*]");
        
        SparkSession spark = SparkSession.builder()
            .config(conf)
            .config("spark.driver.memory", "4g")
            .config("spark.executor.memory", "4g")
            .getOrCreate();
        
        spark.sparkContext().setLogLevel("WARN");
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
        
        System.out.println("Spark version: " + spark.version());
        
        try {
            // Load the data
            System.out.println("\nLoading dataset from: " + csvPath);
            JavaRDD<String> lines = jsc.textFile(csvPath);
            RecordReader reader = new RecordReader();
            JavaRDD<Song> songs = lines.flatMap(reader);
            Dataset<Row> df = spark.createDataFrame(songs, Song.class);
            
            System.out.println("Total songs loaded: " + df.count());
            
            // Filter for English songs only
            df = df.filter("language = 'en' OR language = 'english'");
            df = df.filter("lyrics IS NOT NULL AND tag IS NOT NULL AND lyrics != ''");
            System.out.println("Songs after filtering: " + df.count());
            
            // Sample the data for testing
            df = df.sample(sampleFraction);
            System.out.println("Using " + (sampleFraction * 100) + "% sample: " + df.count() + " songs\n");
            
            // Show genre distribution
            System.out.println("Top 10 genres:");
            df.groupBy("tag").count().orderBy(df.col("count").desc()).show(10, false);
            
            // Build the text processing pipeline
            System.out.println("Building preprocessing pipeline...");
            
            Tokenizer tokenizer = new Tokenizer()
                .setInputCol("lyrics")
                .setOutputCol("words");
            
            StopWordsRemover remover = new StopWordsRemover()
                .setInputCol("words")
                .setOutputCol("filtered_words");
            
            HashingTF hashingTF = new HashingTF()
                .setInputCol("filtered_words")
                .setOutputCol("raw_features")
                .setNumFeatures(10000);
            
            IDF idf = new IDF()
                .setInputCol("raw_features")
                .setOutputCol("features");
            
            StringIndexer indexer = new StringIndexer()
                .setInputCol("tag")
                .setOutputCol("label")
                .setHandleInvalid("skip");
            
            // Set up the classifier
            LogisticRegression lr = new LogisticRegression()
                .setMaxIter(100)
                .setRegParam(0.01)
                .setElasticNetParam(0.0)
                .setFamily("multinomial");
            
            // Combine everything into a pipeline
            Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[] {
                    tokenizer, remover, hashingTF, idf, indexer, lr
                });
            
            // Split data into training and test sets
            Dataset<Row>[] splits = df.randomSplit(new double[]{0.8, 0.2}, 42);
            Dataset<Row> trainingData = splits[0];
            Dataset<Row> testData = splits[1];
            
            System.out.println("\nTraining set: " + trainingData.count() + " songs");
            System.out.println("Test set: " + testData.count() + " songs");
            
            // Train the model
            System.out.println("\nTraining model...");
            long startTime = System.currentTimeMillis();
            PipelineModel model = pipeline.fit(trainingData);
            long trainingTime = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("Training completed in " + trainingTime + " seconds");
            
            // Make predictions on test set
            System.out.println("\nEvaluating model...");
            Dataset<Row> predictions = model.transform(testData);
            
            // Calculate accuracy
            MulticlassClassificationEvaluator evaluator = 
                new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("accuracy");
            
            double accuracy = evaluator.evaluate(predictions);
            
            // Calculate F1 score
            evaluator.setMetricName("f1");
            double f1Score = evaluator.evaluate(predictions);
            
            // Print results
            System.out.println("\n================================================");
            System.out.println("Results:");
            System.out.println("================================================");
            System.out.println("Accuracy: " + String.format("%.2f%%", accuracy * 100));
            System.out.println("F1 Score: " + String.format("%.4f", f1Score));
            System.out.println("Training Time: " + trainingTime + " seconds");
            System.out.println("================================================\n");
            
            // Show some example predictions
            System.out.println("Sample predictions:");
            predictions.select("title", "artist", "tag", "prediction")
                .show(10, false);
            
            // Save the model
            String modelPath = "song_genre_model";
            model.write().overwrite().save(modelPath);
            System.out.println("Model saved to: " + modelPath);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            jsc.close();
            spark.close();
        }
    }
}