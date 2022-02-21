package was.labs.spark;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * The <code>JavaOnSparkMapReduce</code> class demonstrates some aspects of Java Spark  API
 * 
 * @author Mikhail Vladimirov (Web Age Solutions)
 */
public class JavaOnSparkMapReduce {

    private static void run(String inputFile) {

        System.out.println("~~~~~~~~~~~~~~~~~~~~~ Starting in JavaOnSparkMapReduce");

        JavaSparkContext sc = getSparkContext();

        System.out.println("---------------  App name from JavaSparkContext:      " + sc.appName());
        System.out.println("---------------- Master config from JavaSparkContext: " + sc.master());

        JavaRDD<String> rdd = sc.textFile(inputFile);

        getTop100Words(rdd);

        sc.stop();
        /*
         * By the way, sc.stop() is automatically
         * invoked from the JVM shutdown hook on the application's end.
         */

        System.out.println("~~~~~~~~~~~~~~~~~~~~~ Finished in JavaOnSparkMapReduce.");

    }

    private static void getTop100Words(JavaRDD<String> inputRDD) {
        JavaRDD<String> words = inputRDD.flatMap(line -> Arrays.asList(stripPunctuationMarks(line).split("\\s+")));
        JavaPairRDD<String, Integer> countByKey = words
                .mapToPair(w -> new Tuple2<String, Integer>(w, 1)).reduceByKey((x, y) -> x + y);

        for (Tuple2<String, Integer> keyValue : countByKey.toArray()) {
            System.out.println(keyValue._1 + " --> " + keyValue._2);
        }
        
        JavaPairRDD<Integer, String> swapped = countByKey.mapToPair(w -> w.swap());
        JavaPairRDD<Integer, String> sortedByCount = swapped.sortByKey(false); // sort in descending order

        for (Tuple2<Integer, String> countToken : sortedByCount.toArray()) {
            if (countToken._1 >= 100) {
                System.out.println(countToken._1 + " instances of '" + countToken._2 + "'");
            }
        }
    }

    private static String stripPunctuationMarks(String token) {
        String punctuationMarks = "[()\\.:\\-;,\\!?'\"]";
        return token.replaceAll(punctuationMarks, " ");

    }

    private static JavaSparkContext getSparkContext() {
        // Explicitly loading the application name and the master:
        // SparkConf setUpSparkConfObject = new SparkConf().setAppName("YourAppName").setMaster("local[*]");

        // Using the external configuration:
        JavaSparkContext sc = new JavaSparkContext(new SparkConf());

        /*
         * If you did not specify the application name, the full class name
         * would not used: was.labs.spark.JavaOnSparkMapReduce
         * 
         * If you did not specify the master configuration, the local[*] setting
         * will be used
         * 
         */

        return sc;
    }

    public static void main(String[] args) {
        String inputFile = args[0]; //e.g.  "hdfs://192.168.25.132:8020/user/hdfs/ETL/files.dat";
        System.out.println("@@@@@ Received " + inputFile + " for processing.");

        run(inputFile);
    }

}
