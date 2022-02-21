package was.labs.mapreduce;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountLab {

    private static final Log logger = LogFactory.getLog(WordCountLab.class);

    public static class Map extends Mapper<LongWritable, Text, Text, LongWritable> {
        private final static LongWritable one = new LongWritable(1L);
        private Text word = new Text();

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString();
            logger.info("====Map job called on object with id " + this.hashCode() + " on thread [" + getCurrenttName()
                    + "] to process [" + line + "] for key [" + key.toString() + "]");

            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                word.set(tokenizer.nextToken());
                context.write(word, one);
            }
        }
    }

    public static class Reduce extends Reducer<Text, LongWritable, Text, LongWritable> {
    	
        public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {

            long sum = 0L;
            StringBuilder sb = new StringBuilder();
            
            for(LongWritable v: values) {
                long i = v.get();
                sb.append(i);
                sum += i;
            }
            
            logger.info("====Reduce job called on object with id " + this.hashCode() + " on thread ["
                    + getCurrenttName() + "] to process values [" + sb.toString() + "] for key [" + key.toString()
                    + "]; the sum is: " + sum);
            context.write(key, new LongWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration ();
        Job job = Job.getInstance(conf,"wordcount_lab_job");
        job.setJarByClass(WordCountLab.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        job.setMapperClass(Map.class);
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true)? 0 : 1);
    }

    public static String getCurrenttName() {
        return Thread.currentThread().getName();
    }
}
