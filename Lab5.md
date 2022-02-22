# Lab 5 - Getting Started with Apache Pig

In this lab, we will learn about the basics of running the Pig's interactive shell called Grunt. Grunt is started by executing the pig start-up script. When the initialization step is complete, the user is given the grunt> command prompt for entering and executing Pig Latin statements.

In most cases, Apache Pig processes Pig Latin statements as follows:

 *  First, the syntax and semantics of all statements is validated.

 *  Next, whenever a DUMP or STORE statements are encountered, Pig will execute all the preceding statements by building the corresponding MapReduce job and submitting it for execution to Hadoop.

Note: Grunt supports auto-completion of keywords, operator and variable names with the Tab key pressed on the partially entered names.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 2 - The Lab Working Directory

All the steps in this lab will be performed in the /home/cloudera/Works directory.

1. In the terminal window, type in the following command:

```bash
cd ~/Works
```

Note: The terminal window supports the folder and file name auto-completion feature which allows you to type the initial part of the directory or file name (e.g. W) and complete it by pressing the Tab key.

## Part 3 - Create a Data Set

For this lab, we will manually create a file with a simple structure (schema). It will have three fields: the first one will hold numeric values of type int, the second will be for text, and the last one will hold long values. The fields will be separated from each other by a Tab character. The contents of the file is as follows (don't forget to hit Tab as the field delimiter!):

```console
1

one

100



 2

3

4

5

6

two three four five six

200

300

400

500

600
```

Note: Make sure that you hit Enter after the last record (which ends in 600). That way we can simulate an empty record when we perform dump of the file contents on console. The file will be named num_tab_chars.dat.

1. Create this file using the editor of your choice.

## Part 4 - Run Grunt (Pig Command Shell)

To start Grunt (Pig command shell), you need to run the Grunt start-up script, named quite creatively, pig.

If you are interested where this start-up script is located and what is inside it, you can issue the following commands:

To get the location of the pig script: which pig To get the contents of the script: cat `which pig` Note: The ` is the backtick character that is typically located in the top left corner of the keyboard sharing the key with the tilde (~).

You can also quickly find out the version of Apache Pig installed on your machine by running this command:

```bash
pig --version
```

OK, we have enough information about Pig and are ready to make the pig fly.

We will not load the input file on HDFS and will run pig in local mode where it will be able to read the input file from the local file system. You can use this mode for quick development work and prototyping.

1. Run the pig start-up script in local mode:

```bash
pig -x local
```

After a number of informational messages (and, possibly, warnings which you can safely ignore), you will get the Grunt shell prompt.

```console
grunt>
```



First off, let's see how to use the sh command that allows us execute operating system commands from inside Grunt.

2. Enter the following command at the grunt> command prompt:

```console
sh ls -l
```

You should see the contents of your current working directory.

After the forked shell returns, you get your Grunt prompt back.

## Part 5 - Load and Work with the Input File

1. Enter the following command at the grunt> command prompt:

```console
n = LOAD 'num_tab_chars.dat' AS (ids:int, descr:chararray, values:long);
```

Since the input file uses the default Tab field delimiter, you can enjoy a simpler syntax of the LOAD command (without the USING PigStorage() function).

The AS clause of the LOAD operator specifies the schema of the n relation as having three fields named ids of type int, desc of type chararray (used for holding text / string type of values), and values of type long. You can give your own field names as long as they comply with Pig Latin parameter name syntax.

Note: Don't forget the closing ; (semi-colon) after each Pig Latin statement.

2. Enter the following command:

```console
DESCRIBE n;
```

This command will print the relation schema you defined during the LOAD operation.

```console
n: {ids: int,descr: chararray,values: long}
```

3. Enter the following command:

```console
DUMP n;
```

This command will take several seconds to complete and the progress of the triggered execution is printed out on the console.



46 You should get the following output containing 7 tuples (records), the last one being empty (it holds a single line feed (\n) character):

```console
(1,one,100) (2,two,200) (3,three,300) (4,four,400) (5,five,500) (6,six,600) (,,)
```

The DUMP command triggers the execution of all previously entered statements as a functionally equivalent MapReduce job.

The progress of the MapReduce job is indicated by the following output messages emitted by the org.apache.pig.backend.hadoop.executionengine.

```console
mapReduceLayer.MapReduceLauncher:

. . . 0% complete . . . . . .

. . . 50% complete . . . . . .

. . . 100% complete
```

Now, if we want to limit the number of printed rows (tuples) based on some logical condition, we can to this by using the FITLER BY operator.

4. Enter the following command:

```pig
filtered = FILTER n BY SIZE(descr) >= 4;
```

Here we use the SIZE function that computes the number of elements based on any Pig data type, the length of the descr column, in our case.

See http://pig.apache.org/docs/r0.12.0/func.html#size for more information on SIZE(). 5. Enter the following command:

```pig
DUMP filtered;
```

You should get the following output:

```console
(2,two,200) (3,three,300) (4,four,400) (5,five,500)
```



The last (7th ) row in our data set, which has nulls in all fields, is not included in the output as logical operations with a null value are evaluated to false.

Now let's see how we can perform simple arithmetic operations with numeric values. 6. Enter the following command:

```pig
diff = FOREACH n GENERATE values - ids;
```

Notice the implicit conversion warning of ids (int) to values (long)

``console
… [main] WARN

org.apache.pig.PigServer - Encountered Warning IMPLICIT_CAST_TO_LONG ...
```

7. Enter the following command:

```pig
DUMP diff;
```

The following output will be produced:

```console
(99)
(198)
(297)
(396)
(495)
(594)
()
```

Notice the empty parentheses at the end of the output that denote a null transaction; that means that Pig is tolerant of possible conversion errors and silently ignores them leaving it to the developer to interpret the results.

Now, let's say we are only interested in the first two fields (ids and descr) of our data set and we want to drop the trailing field (values).

Pig is very accommodating in this regards: you just need to create a new schema omitting the last field(s).

8. Enter the following command:

```pig
nShort = LOAD 'num_tab_chars.dat' AS (ids:int, descr:chararray);
```

9. Enter the following command:

```pig
DUMP nShort;
```

You should get the following output:



```pig

(1,one) (2,two) (3,three) (4,four) (5,five) (6,six) (,)
```

We are almost done in this lab.

10. Type in quit and press Enter to close the Grunt command shell. You should be returned to the system command prompt.

## Part 6 - Working Area Clean-up

1. Enter the following command:

```bash
cd /home/cloudera/Works
```

2. Enter the following command:

```bash
pwd
```

You should see the following output:

```console
/home/cloudera/Works
```

3. Enter the following command:

```bash
rm -fr *
```

## Part 7 - Ending the Working Session

1. Type in exit and press Enter to close your terminal window. This is the last step in this lab.

## Part 8 - Review

In this lab, we reviewed the basic operations related to working with Grunt, Pig's interactive command shell.

We ran Pig in local mode which allowed us to read the input file from local file system.
