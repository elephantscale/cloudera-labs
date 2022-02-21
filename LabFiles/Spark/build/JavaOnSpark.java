package was.labs.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

/**
 * The <code>JavaOnSpark</code> class demonstrates some aspects of Java Spark  API
 */
public class JavaOnSpark {

    private static void run(String inputFile, String searchToken) {

        System.out.println("~~~~~~~~~~~~~~~~~~~~~ Starting in JavaOnSpark");

        JavaSparkContext sc = getSparkContext();

        System.out.println("---------------  App name from JavaSparkContext:      " + sc.appName());
        System.out.println("---------------- Master config from JavaSparkContext: " + sc.master());

        JavaRDD<String> rdd = sc.textFile(inputFile);

        searchAndCount(searchToken, rdd);

        extractAndArrangeColumns(searchToken, rdd);

        sc.stop();
        /*
         * By the way, sc.stop() is automatically
         * invoked from the JVM shutdown hook on the application's end.
         */

        System.out.println("~~~~~~~~~~~~~~~~~~~~~ Finished in JavaOnSpark.");

    }

    private static void searchAndCount(String searchToken, JavaRDD<String> rdd) {
        java7processing(searchToken, rdd);
        java8processing(searchToken, rdd);
    }

    /**
     * Java 8 style processing (lambda-based)
     * @param searchToken
     * @param inputRDD
     */
    private static void java8processing(String searchToken, JavaRDD<String> inputRDD) {
        //  Compact syntax: JavaRDD<String> rdd3 = rdd1.filter(line -> line.contains(searchToken));

        JavaRDD<String> rdd = inputRDD.filter(line -> filterClause(line, searchToken));

        long count = rdd.count();
        System.out.println(" ******** Java 8 processing model\nFound " + count + " occurrences of search token '"
                + searchToken + "'");
    }

    /**
     * Java 7 processing style (inner class)
     * @param searchToken
     * @param inputRDD input RDD
     */
    private static void java7processing(String searchToken, JavaRDD<String> inputRDD) {
        JavaRDD<String> rdd = inputRDD.filter(new Function<String, Boolean>() {
            public Boolean call(String line) {
                boolean isHit = line.contains(searchToken);
                if (isHit) {
                   // System.out.print("....." + line);
                }
                return isHit;
            }
        });

        long count = rdd.count();
        System.out.println(" ******** Java 7 processing model\nFound " + count + " occurrences of search token '"
                + searchToken + "'");
    }

    private static boolean filterClause(String line, String searchToken) {
        boolean isHit = line.contains(searchToken);
        if (isHit) {
         //   System.out.print(",,,,," + line);
        }
        return isHit;
    }

    private static void extractAndArrangeColumns(String searchToken, JavaRDD<String> inputRDD) {

        JavaRDD<String> rdd = inputRDD.filter(line -> filterClause(line, searchToken));

        JavaRDD<String[]> map1 = rdd.map(e -> e.split(","));

        // Printing to console
        map1.foreach(e -> System.out.println("++++++++++" + e[2] + "," + e[3] + "," + e[0]));

        // Transforming to another RDD
        JavaRDD<String> map2 = map1.map(e -> (e[2] + "|" + e[3] + "|" + e[0]));

        for (String line : map2.toArray()) {
            System.out.println("-----------" + line);
        }
    }

    private static JavaSparkContext getSparkContext() {
        // Explicitly loading the application name and the master:
        // SparkConf setUpSparkConfObject = new SparkConf().setAppName("YourAppName").setMaster("local[*]");

        // Using the external configuration:
        JavaSparkContext sc = new JavaSparkContext(new SparkConf());

        /*
         * If you did not specify the application name, the full class name
         * would not used: was.labs.spark.JavaOnSpark
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

        String searchToken = args[1];// e.g. "May";
        System.out.println("@@@@@ Received " + searchToken + " token (string) for processing.");

        run(inputFile, searchToken);
    }

}
