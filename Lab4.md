# Lab 4 - Programming Java MapReduce Jobs on Hadoop

In this lab, we will learn how to create a Java-based MapReduce job. We will work through all the steps from compiling the source Java files, packaging them into a jar file, and executing it on Hadoop. We will use a canonical MapReduce Java program (sort of a "Hello Hadoop!") for counting tokens (words and numbers) in input file(s). In the end we will analyze the log files produced by our job to get understanding of the flow of our MapReduce job.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab ## Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 2 - The Lab Working Directory

All the steps in this lab will be performed in the /home/cloudera/Works directory.

1. In the terminal window, type in the following command:


```bash
cd ~/Works
```

Note: The terminal window supports the folder and file name auto-completion feature which allows you to type the initial part of the directory or file name (e.g. W) and complete it by pressing the Tab key.

## Part 3 - Getting the Input and Source Files

Input and source files for this lab have already been created for you and stored in the /home/cloudera/LabFiles/MapReduce folder.

Let's copy them over to our working directory.

1. Enter the following command and verify that you are in the /home/cloudera/Works directory:

```bash
pwd
```

Now, let's copy the MapReduce folder under /home/cloudera/LabFiles/ into our working folder.


2. In the terminal window, run the following command:

```bash
cp -r /home/cloudera/LabFiles/MapReduce/ .
```

3. Enter the following command to change directory to `/home/cloudera/Works/MapReduce`:

```bash
cd MapReduce
```

The /home/cloudera/Works/MapReduce directory is going to be our working directory for this lab.

Let's review the content of the input files `fMapReduceNums.dat` and `fMapReduceWords.dat`.

4. Enter the following command:

```bash
cat fMapReduceNums.dat
```

You should see the following output:

```console
1 2 2 3 3 3 4 4 4 4 5 5 5 5 5
```

5. Enter the following command:

```bash
cat fMapReduceWords.dat
```

You should see the following output:

```console
one two two three three three four four four four five five five five five
```

To access these files from our MapReduce job (more on it in a moment), we need to import these files into HDFS.



## Part 4 - Import Input Files into HDFS

The MapReduce job takes a path to the directory where the input files are stored. Let's create this directory named IN. The output directory that contains the processing results will be created by the MapReduce job itself.

1. Enter the following command:

```bash
hadoop fs -mkdir IN
```

2. Run the following command:

```bash
hadoop fs -put fMapReduceNums.dat IN/
```

you can do this by using the following HDFS commands:

```bash
hadoop fs -ls IN/
```

This command will show the directory listing of IN:

```console
Found 2 items

-rw-r--r-- 1 cloudera cloudera

-rw-r--r-- 1 cloudera cloudera

30 2015-06-25 11:02 IN/fMapReduceNums.dat 75 2015-06-25 11:02 IN/fMapReduceWords.dat
````

You can also dump the contents of the files to the console, e .g:

```bash
hadoop fs -cat IN/fMapReduceWords.dat
```

The output of such commands should correspond to the original contents of the files that we saw earlier in the lab steps.

Now, let's get busy with the actual MapReduce job file.

## Part 5 - Reviewing the Source of the MapReduce Module

The native Hadoop's MapReduce job needs a mapper and a reducer classes and normally those are bundled as static classes within the same MapReduceJava "driver" file. The main method of this "driver" class sets up the run-time configuration parameters, such as the job name, designated mapper and reducer classes, etc.

Let's review the MapReducer file that was already created for you to save your time. This file is named WorkCountLab.java and is located in the current working folder, /home/cloudera/Works/MapReduce.



1. Enter the following command:

```bash
less -MN WordCountLab.java
```

This command helps with bi-directional (down and up) scrolling of the source using the keyboard navigational arrow and PgUp and PgDown keys. The -MN flags will help with showing your viewing point location in the file and printing line numbers.

The full source of the [WordCountLab.java](./LabFiles/MapRduce/WordCountLab.java) file is shown below.

```java
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
```

Let's review all the important aspect of the content from top to bottom.

The import statements at the beginning of the file help organize type imports:

```java

import java.io.IOException; 
import java.util.StringTokenizer;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.fs.Path; . . .
```



The public class `WordCountLab` is the MapReduce file which contains both the mapper (public static class Map) and the reducer (public static class Reduce) classes.

The `Map` class exposes the `map()` call-back method and the Reduce class exposes the reduce() call-back method that are called by Hadoop's MapReduce infrastructure at appropriate times.

There are logging output statements that help understand the call sequence and parameters passed on by the MapReduce engine.

The log messages are not displayed in the console when you run this job and, rather, they are redirected to a log file that we will look into later in the lab.

To help trace the log messages, we prefix them with the "====" string.

The map() method of the Map class takes a line from the input file (or a chunk of it), tokenizes (breaks down the line in separate words / tokens) it, and emits each token with the count of one (1). The reduce () method finds and sums up the token occurrences generated by the map() method.

The main method `public static void main(String[] args)` is the entry point in Java classes called by the JVM on the application start-up and WordCountLab uses it to set up all the needed run-time configuration parameters.

We will share additional information about the source of our MapReduce module as we work through the lab.

Now, that we have some initial insight into how it works, let's compile and package the WordCountLab file and run it on Hadoop's MapReduce engine.

2. Press q to exit the editor.

## Part 6 - Packaging MapReduce Job Files

We will have to manually compile and package the file before we can run it on as a MapReduce job.

We will create a script that does the compilation for us which we can re-use for other similar tasks.

1. Create the following file (using vi or the editor of your choice):

```bash
compileMapReduceJob.sh
```



2. Add the following code to compileMapReduceJob.sh, making sure you enter spaces as needed:

```bash
jars='.'

for f in /home/cloudera/Works/MapReduce/jars/*; do
  if [ -f $f ]; then
    jars="$jars:$f"
  fi
done
echo -e "Compilation jars:\n$jars\n"

javac -nowarn -classpath $jars -d compiled WordCountLab.java 
```

This script sets up the class path with the jar files stored in the /home/cloudera/Works/ MapReduce/jars/ folder and compiles our MapReduce module using the javac compiler. We will need to create the compiled folder to store the compiled Java classes which we will do in a moment.

Note: If you don't feel like typing in the above commands, you can use the compileMapReduceJob.sh file in the ~/LabFiles/Solutions folder.

3. Save the file and go back to the command line.

4. Enter the following command:

```bash
mkdir compiled
```

This command creates a root folder for MapReuce classes that we are going to compile using our compileMapReduceJob.sh script.

In order to be able to run a file, we need to make it executable.

5. Enter the following command:

```bash
chmod u+x compileMapReduceJob.sh
```

Let's run it.

6. Enter the following command:

```bash
./compileMapReduceJob.sh
```

You should see no errors or warnings and the script should only output the jars used for compilation (the classpath variable value):


```console

Compilation jars:



.:/home/cloudera/Works/MapReduce/jars/commons-logging-1.1.1.jar:/home/ cloudera/Works/MapReduce/jars/hadoop-common.jar:/home/cloudera/Works/ MapReduce/jars/hadoop-mapreduce-client-common.jar:/home/cloudera/Works/ MapReduce/jars/hadoop-mapreduce-client-core.jar

```

Let's quickly review the files that have been generated 

7. Enter the following command:

```bash
find compiled
```

You should see the following output:


```console
compiled/ 
compiled/was 
compiled/was/labs 
compiled/was/labs/mapreduce 
compiled/was/labs/mapreduce/WordCountLab$Map.class 
compiled/was/labs/mapreduce/WordCountLab.class 
compiled/was/labs/mapreduce/WordCountLab$Reduce.class
```

This hierarchy of folders is the way Java organizes its packages. The package of our WorkCountLab.java file is declared as package was.labs.mapreduce; Now we need to package the files so that we can submit them to Hadoop as a whole. 8. Enter the following command:

```bash
jar -cvf MapReduceLab.jar -C compiled/ .
```

You should see some diagnostic messages about adding files in the compiled folder to the jar file.

This command uses the jar packaging utility that is shipped with Java to build the MapReduceLab.jar file.

9. Verify the creation of the jar file by entering the following command:

```bash
ls -lh
```

You should see the `MapReduceLab.jar` file created on the file system.


```console

-rw-rw-r-- 1 cloudera cloudera 4.2K Jun 25 12:26 MapReduceLab.jar
```

Notice the file size - it is a mere 4 K. That's a much smaller price to pay to send the processing job out to the Data Nodes where the data is (stored in 64 M or 128 M blocks) compared to doing it in reverse (as per the client-server processing model).

Now we are ready to run our MapReduce job.

## Part 7 - Running the MapReduce Job

For our MapReduce job, we will use the files (fMapReduceWords.dat and fMapReduceNums.dat) in the IN HDFS folder, and the output will be generated in the OUT folder that will be transparently created by the job.

1. Enter the following command that executes the MapReduce job:

```bash
hadoop jar MapReduceLab.jar was.labs.mapreduce.WordCountLab IN OUT
```

This command instructs Hadoop that the was.labs.mapreduce.WordCountLab file (the fully qualified file name) is the MapReduce job housed in the MapReduceLab.jar.

You should see the progress of the MapReduce job and the job should finish in a minute or so.

Analyze the console output of the job. You may be overwhelmed with the details, but here are a couple of interesting pointers.

Job Counters

Launched map tasks=2 Launched reduce tasks=1

The MapReduce framework allocated two map tasks to handle the two files sitting in the IN/ directory:

```console
-rw-r--r-- 1 cloudera cloudera 30 2015-06-25 14:02 IN/fMapReduceNums.dat

-rw-r--r-- 1 cloudera cloudera 75 2015-06-25 14:02 IN/fMapReduceWords.dat
```

Data aggregation was handled by a single reducer.

Also take a note of the actual time spent in the mappers and the reducer.

Total time spent by all maps in occupied slots (ms)=8904 Total time spent by all reduces in occupied slots (ms)=3457

The overall execution time was longer than those single digit second intervals listed in the output. The extra time was spent on performing certain infrastructure activities (like launching the needed JVMs, etc.) and this is the common critique of the traditional MapReduce - its inherent initial latency in processing even very small amounts of data. Keep in mind, though, that this initial latency will be totally absorbed in longer running jobs crunching terabytes of data.

Also, you may be interested in the CPU time consumed by this job (your number may differ), e.g.:

Map-Reduce Framework CPU time spent (ms)=1830

Finally, try to figure out how the bytes read and written were calculated:

File Input Format Counters Bytes Read=105 File Output Format Counters Bytes Written=54

2. Enter the following command to verify the results of running your job:

```bash
hadoop fs -cat OUT/part-r-00000
```

You should see the following results:

```console
1 2 3 4 5 five four one three two

1

2

3

4

5

5

4

1

3

2
```

The first column lists the tokens (words and numbers) from both files, and the second column lists the number of occurrences of corresponding tokens.

## Part 8 - Reviewing the MapReduce Log files

Steps below only apply to the desktop deployment option of your Lab Server where you can use your browser.

In the job's output in your terminal, locate the link to your job tracker URL; look for a line that looks as follows:

```console
<timestamp>INFO mapreduce.Job: The url to track the job: http://quickstart.cloudera:8088/proxy/application_<jobid>/
```

1. Copy the URL into your browser (alternatively, you can also try to click the link in the terminal window with the Control key down).



2. Click the Map tasks link in the Job section.

You should see two links one for each Map task executed against our two files.

3. Click the first link (it should end in m_00000).

You should see the following page:

4. In the middle of the captions bar, locate Logs and click the logs link underneath it.

You would see the following (cut for space here) output.

```console
Map job called on object with id 277061496 on thread [main] to process [one] for key [0] Map job called on object with id 277061496 on thread [main] to process [two two] for key [4] Map job called on object with id 277061496 on thread [main] to process [three three three] for key [12] Map job called on object with id 277061496 on thread [main] to process [four four four four] for key [30] Map job called on object with id 277061496 on thread [main] to process [five five five five five] for key [50] Reduce job called on object with id 1610644915 on thread [main] to process values [11111] for key [five]; the sum is: 5 Reduce job called on object with id 1610644915 on thread [main] to process values [1111] for key [four]; the sum is: 4 Reduce job called on object with id 1610644915 on thread [main] to process values [1] for key [one]; the sum is: 1 Reduce job called on object with id 1610644915 on thread [main] to process values [111] for key [three]; the sum is: 3
```

Note that we have the Reduce job running adjacent to the Map job. This the optimization offered by the Combiner class (consult the source code of WordCountLab.java). That leaves little to do for our Reducer ...

The Combiner's role is to sort the output of the mapper processes and do some data aggregation by key before it is fed into the shuffle step; in some cases it takes over the shuffle step's job as well.

Note: The log messages are printed by the following statements in the WordCountLab.java file:

```java
logger.info("====Map job called on object with id " + this.hashCode() + " on thread [" + getCurrenttName() + "] to process [" + line + "] for key [" + key.toString() + "]");
```

and

```java
logger.info("====Reduce job called on object with id " + this.hashCode() + " on thread [" + getCurrenttName() + "] to process values [" + sb.toString() + "] for key [" + key.toString() + "]; the sum is: " + sum);
```

There are several insights that can be derived from this output.

Execution is done on the main thread indicating that each job is executed as a singlethreaded application (that's how the shared nothing design principle is achieved).

The mapper and reduce jobs are most likely executed by different instances of the Map and Reduce classes with the MapReduce engine potentially trying to re-use the same JVM for multi-pass operations to achieve certain efficiencies depending on the Hadoop cluster setup and available computing capacities.


5. Close the browser.

## Part 9 - What May Go Wrong

A number of things may go wrong. Usually, inspecting log files and using programmer's common sense should be enough to troubleshoot the problem. The console output and the log files give you sufficient information to solve some of the problems.

Let's create one of a typical problem and show how you can fix it.

1. Enter the following command:

```bash
cd /home/cloudera/Works/MapReduce
```

You should go back to the original `/home/cloudera/Works/MapReduce` working directory where you executed your MapReduce job.

Re-execute your MapReduce job.

2. Enter the following command (don't panic when you see an error, it is an expected result!):

```bash
hadoop jar MapReduceLab.jar was.labs.mapreduce.WordCountLab IN OUT
```

You should get an exception (error) printed in your console.

```console
15/06/25 17:38:43 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032 15/06/25 17:38:45 WARN security.UserGroupInformation: PriviledgedActionException as:cloudera (auth:SIMPLE) cause:org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs://quickstart.cloudera:8020/user/cloudera/OUT already exists Exception in thread "main" org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs://quickstart.cloudera:8020/user/cloudera/OUT already exists at org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.checkOutputSpecs(FileOutputFormat.java:146) at org.apache.hadoop.mapreduce.JobSubmitter.checkSpecs(JobSubmitter.java:562) at org.apache.hadoop.mapreduce.JobSubmitter.submitJobInternal(JobSubmitter.java:432) ...

at java.lang.reflect.Method.invoke(Method.java:606) at org.apache.hadoop.util.RunJar.run(RunJar.java:221) at org.apache.hadoop.util.RunJar.main(RunJar.java:136) ...
```

The root cause of the problem is shown in the bold face above - the output directory (OUT in our case) already exists.

The solution is a no-brainer: we need remove this directory.



3. Enter the following command:

```bash
hadoop fs -rm -r -skipTrash OUT
```

You should get the following confirmation of your command: Deleted OUT Now you can successfully re-run your MapReduce job.

## Part 10 - Cleaning Up and Ending the Working Session

1. Enter the following command (don't forget `/*` at the end of the command!):

```bash
hadoop fs -rm -r -skipTrash /user/cloudera/*
```

You should see a confirmation message on the deleted resources on HDFS. 2. Enter the following command:

```bash
cd /home/cloudera/Works
```

3. Enter the following command:

```bash
pwd
```

You should see the following output:

```bash
/home/cloudera/Works
```

4. Enter the following command:

```bash
rm -fr *
```

5. Type in exit and press Enter to close your terminal window. This is the last step in this lab.

## Part 11 - Review

In this lab, we learned how to create a Java-based MapReduce job. We worked through some of the typical steps of building the application: compiling the source code, packaging it into a jar file, and finally executing it on Hadoop.
package was.labs.mapreduce;


