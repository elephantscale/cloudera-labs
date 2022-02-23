# Spark Shell On Hadoop

### Overview
Get familiar with Spark shell  

### Run time
approx. 20-30 minutes

### Notes

## Step 1 : Login to Hadoop Node
Follow instructions for your environment.

## Step 2 : Start Spark shell

```bash
    $    spark-shell
```

### Controlling the UI options
Spark Shell by default publishes a UI on port number 4040.  
How ever when multiple apps are running, and port 4040 already taken, Spark Shell will try to find an open port (4041, 4042 ..etc)

Specifying a custom port
```bash
    $   spark-shell  --conf spark.ui.port=4060
```

Turn off UI altogether  
```bash
    $   spark-shell  --conf spark.ui.enabled=false
```

## Step 3 : Set the log level to WARN
Type this in Spark Shell
```scala
    sc.setLogLevel("WARN")
```

## Step 4 : Inspect the Shell UI
Look at the console log to identify the Spark Shell UI port.

```console
INFO Utils: Successfully started service 'SparkUI' on port 4042.
```

(In the above example port number of 4042).   

Access Spark Shell UI as http://hadoop_node_ip_address:port_number  

(Instructor will provide more details)


<img src="../assets/images/2a.png" style="border: 5px solid grey ; max-width:100%;" />

**==> Explore stage, storage, environment and executor tabs**

**==> Take note of 'Event Timeline', we will use this for monitoring our jobs later**

## STEP 5: Spark context
Within Spark  shell,  variable `sc` is the SparkContext.  
Type `sc` in scala prompt and see what happens.  
Your output might look like this...

```scala
    scala> sc
    res0: org.apache.spark.SparkContext = org.apache.spark.SparkContext@5019fb90
```

To see all methods in sc variable, type `sc.` (don't forget the DOT)  and type `TAB`. This will show all the available methods on `sc` variable.

Try the following:

**==> Print the name of application name**
`sc.appName`

**==> Find the 'Spark master' for the shell**
`sc.master`


## Step 6 : Load a local file
Let's load  `/etc/hosts` file in Spark Shell.  
Issue the following commands in Spark-shell

```scala
scala>
        val f = sc.textFile("file:///etc/hosts")
```

#### answer the following questions:

**==> What is the 'type' of f ?**   
hint : type `f` on the console

**==> Inspect Spark Shell UI;  Do you see any processing done?  Why (not)?**

**==> Print the first line / record from RDD**  
```scala
    f.first
```

**==> Again, inspect Spark Shell UI;  do you see any processing done?  Why (not)?**

**==> Print first 3 lines of RDD**  
hint : `f.take(???)`  (provide the correct argument to take function)

**==> Again, inspect Spark Shell UI on port 4040, do you see any processing done?  Why (not)?**

**==> Print all the content from the file**  
```scala
    f.collect()
```

**==> How many lines are in the file?**  
```scala
    f.count
```

**==> Inspect the 'Jobs' section in Shell UI (in browser)**  
Also inspect the event time line

<img src="../assets/images/2b.png" style="border: 5px solid grey; max-width:100%;" />

**==> Inspect the 'Executor' section in Shell UI (in browser)**

<img src="../assets/images/2c.png" style="border: 5px solid grey; max-width:100%;" />


## Step 7 : Load a HDFS file
Let's load  a sample data from `/data/text/twinkle/'.  
Try the following in Spark-shell

```scala
scala>
    val a = sc.textFile("/data/text/twinkle/sample.txt")

    // count the lines
    a.count

    // print the lines
    a.collect
    a.foreach(println)
```


## STEP 9:  Connecting Spark Shell to YARN

**==> Quit spark-shell session, if you haven't done so yet.. `Control + D`**  

```bash
    $ spark-shell  --master yarn  --name 'MY_NAME spark shell'
```

Once the shell starts, check the 'Resource Manager UI' on your Hadoop system.  Do you see the spark shell connected?

Try the following while the Spark shell is connected to YARN.
```scala
scala>

    val f = spark.read.textFile("/data/text/twinkle/sample.txt")
    // Note the type of f is Dataset not RDD
    // f: org.apache.spark.sql.Dataset[String] = [value: string]

    f.count

    f.collect

    f.foreach(println)
```

## Tip : Dealing With Logs
Spark Shell by default prints logs at warning (WARN) level.  If you want to change the logging level, do this at Spark shell
```scala
    sc.setLogLevel("INFO")
```

If you don't want to see any logs, you can start Spark shell as follows.  All the logs will be sent to 'logs' file.
```bash
    $    spark-shell  2> logs
```


## TIPS and TRICKS: To run Linux command without leaving the shell

```scala
    import sys.process._
    val result = "ls -al"!
```
