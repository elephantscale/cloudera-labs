package was.labs.spark;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

/**
 * The <code>JavaOnSparkIntro</code> class demonstrates the basics of running Java applications on Spark
 */
@SuppressWarnings("serial")
public class JavaOnSparkIntro implements Serializable {

    public void run() {
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~ Started in " + getClassName() );
        
        List<Integer> dataSet = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        JavaSparkContext sc = new JavaSparkContext(new SparkConf());

        System.out.println("---------------  App name from JavaSparkContext:      " + sc.appName());
        System.out.println("---------------- Master config from JavaSparkContext: " + sc.master());

        JavaRDD<Integer> rdd1 = sc.parallelize(dataSet);

        /*
         * Java 7 Take:
         */
        JavaRDD<Integer> rdd2 = rdd1.filter(new Function<Integer, Boolean>() {
            public Boolean call(Integer line) {
                return line % 2 == 0;
            }
        });

        List<Integer> evenNumbers1 = rdd2.toArray();
        System.out.println(" ******** Java 7 processing model.\nFound " + evenNumbers1 
                + " even numbers in the original list of " + dataSet + "'");

        /*
         * Java 8 Take:
         */
        JavaRDD<Integer> rdd3 = rdd1.filter(e -> (e % 2 == 0));

        List<Integer> evenNumbers2 = rdd3.toArray();
        System.out.println(" ******** Java 8 processing model.\nFound " + evenNumbers2 
                + " even numbers in the original list of " + dataSet + "'");

        sc.stop();
        
        /*
         * By the way, sc.stop() is automatically
         * invoked from the JVM shutdown hook on the application's end.
         */
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~ Finished in " + getClassName());
    }

    private String getClassName () {
        return this.getClass().getCanonicalName();
    }
    public static void main(String[] args) {
        (new JavaOnSparkIntro()).run();
    }

}

