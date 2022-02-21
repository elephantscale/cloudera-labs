package was.labs.spark;

import java.io.Serializable;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import scala.collection.Iterator;

/**
 * The <code>JavaOnSparkMapReduce</code> class demonstrates some aspects of the Java Spark SQL API
 * 
 * @author Mikhail Vladimirov (Web Age Solutions)
 */
public class SparkSQLWithJava8 {

    private static void run(String inputFile) {

        System.out.println("~~~~~~~~~~~~~~~~~~~~~ Starting in JavaOnSparkMapReduce");

        JavaSparkContext sc = getSparkContext();

        System.out.println("---------------  App name from JavaSparkContext:      " + sc.appName());
        System.out.println("---------------- Master config from JavaSparkContext: " + sc.master());

        JavaRDD<String> rddFile = sc.textFile(inputFile);

        JavaRDD<SchemaBean> filesRDD = rddFile.map(e -> e.split(","))
                .map(r -> new SchemaBean(r[0], Integer.parseInt(r[1].trim()), r[2], Integer.parseInt(r[1].trim())));

        SQLContext sqlContext = new SQLContext(sc);

        // Create a data frame based on the schema backed-up by our SchemaBean 
        DataFrame filesDF = sqlContext.createDataFrame(filesRDD, SparkSQLWithJava8.SchemaBean.class);

        int LIMIT = 10;

        basicDataFrameOperations(filesDF, LIMIT);

        sqlOperations(sqlContext, filesDF, LIMIT);

        sc.stop();
        /*
         * By the way, sc.stop() is automatically
         * invoked from the JVM shutdown hook on the application's end.
         */

        System.out.println("~~~~~~~~~~~~~~~~~~~~~ Finished in JavaOnSparkMapReduce.");

    }

    private static void sqlOperations(SQLContext sqlContext, DataFrame filesDF, int LIMIT) {

        // Register the data frame as a virtual table
        filesDF.registerTempTable("tblFiles");

        //Now you can run SQL commands against the target RDD registered as a table
        DataFrame sqlDF = sqlContext
                .sql("SELECT name, month, size FROM tblFiles WHERE size >= 1024 AND size <= 10 * 1024");

        System.out.println("+++++++++++++++++ Listing the SQL query's results");

        List<String> rows = sqlDF.javaRDD().map(new Function<Row, String>() {
            public String call(Row row) {
                return "File name: " + row.getString(0) + ", month: " + row.getString(1) + ", file size: "
                        + row.getInt(2);
            }
        }).collect();

        for (String row : rows.subList(0, LIMIT)) {
            System.out.println(row);
        }
    }

    private static void basicDataFrameOperations(DataFrame filesDF, int LIMIT) {

        Row[] head = filesDF.head(LIMIT);

        System.out.println("**************** First " + LIMIT + " file records:");
        for (Row r : head) {
            System.out.println(r);
        }

        System.out.println("################### Iterating over the data frame objects converted to JSON.");

        Iterator<String> localIterator = filesDF.toJSON().toLocalIterator();
        int i = 0;
        while (i++ < LIMIT && localIterator.hasNext()) {
            String nextJSON = localIterator.next();
            System.out.println(nextJSON);
        }
    }

    private static JavaSparkContext getSparkContext() {
        JavaSparkContext sc = new JavaSparkContext(new SparkConf());
        return sc;
    }

    /**
     * The data frame schema
     */
    public static class SchemaBean implements Serializable {

        private String name;
        private Integer size;
        private String month;
        private Integer day;

        public SchemaBean() {
            this("unknown", -1, "NA", -1);
        }

        public SchemaBean(String name, Integer size, String month, Integer day) {
            super();
            this.name = name;
            this.size = size;
            this.month = month;
            this.day = day;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        @Override
        public String toString() {
            return "FileCC [name=" + name + ", size=" + size + ", month=" + month + ", day=" + day + "]";
        }
    }

    public static void main(String[] args) {
        String inputFile = args[0];
        System.out.println("@@@@@ Received " + inputFile + " for processing.");

        run(inputFile);
    }

}
