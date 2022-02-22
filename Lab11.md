# Lab 11 - Using Select Statement in HiveQL

In this lab, you will learn how to use the Select statement in HiveQL and perform table joining using equality (inner) joins based on equality of values in related fields. While more than two tables can be joined in the same HiveQL query, we will work with two tables which should provide enough information on how to extend this two-table joining case to include more tables.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 2 - The Lab Working Directory

All the steps in this lab will be performed in the /home/cloudera/Works directory.

1. In the terminal window, type in the following command:

cd ~/Works

## Part 3 - Starting the Services

Before we begin, we need to make sure that Hive-related services are up and running.

1. Enter the following commands one after another:

```bash
sudo /etc/init.d/hive-metastore status 
sudo /etc/init.d/hive-server2 status
```

If you see their status as not running, start the stopped service(s) using the following commands:

```bash
sudo /etc/init.d/hive-metastore start 
sudo /etc/init.d/hive-server2 start
```

## Part 4 - The Input Files

We are going to use two comma-delimited text files from the LabFiles folder contents of which will be imported into two Hive tables; later, we will join those two Hive tables on the join field.



1. Execute the following commands one after another:

```bash
cp ~/LabFiles/sizes.dat
cp ~/LabFiles/names.dat
```


The `sizes.dat` file is a comma-delimited file with the following fields:

1. Auto-incremented id (1,2,3,4, ...)

2. File size

3. Month of creation

4. Day of creation

The `names.dat` file is a comma-delimited file with the following fields:

1. Auto-incremented id (1,2,3,4, ...)

2. File Name

Note: We will use the first auto-incremented id field in both files as the equality field in the inner join operation in Hive.

Let's confirm that we have the same number of lines in both files.

2. Enter the following command:

```bash
wc -l sizes.dat
```

You should see the following output:

1932 sizes.dat

3. Enter the following command:

```bash
wc -l names.dat
```

You should see the following output:

```console
1932 names.dat
```


The import of files from the local file system into HDFS is done transparently by Hive as part of the `LOAD DATA LOCAL INPATH <local path to the input file> INTO TABLE <hive table name>` command issued from inside the Hive shell.



## Part 5 - Creating Tables in HiveQL

1. Enter the following command to start the Hive shell:

```console
hive
```

You should be placed in the Hive shell and presented with the hive> prompt.

Note 1: When you enter Hive commands, make sure you end them with the semi-colon (;) 

Note 2: Although HiveQL commands and statements are case-insensitive, we will adhere to using the upper case for them, e.g. `SHOW TABLES;` 


2. Enter the following multi-line command pressing Enter after each line:

```console
CREATE TABLE tblSizes (id INT, fileSize INT, month STRING, day INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;
```

3. Enter the following multi-line command pressing Enter after each line:

```sql
CREATE TABLE tblFileNames (id INT, filename STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;
```

Now we should have two tables created in Hive. 4. Enter the following command:

```sql
SHOW TABLES;
```

You should see the following output:

```console
OK tblfilenames tblsizes Time taken: 0.105 seconds
```

5. Enter the following command:

```sql
DESCRIBE tblSizes;
```



87 You should see the following output:

```console
OK id int filesize int month string day int Time taken: 0.624 seconds
```

6. Repeat the DESCRIBE command for the tblFileNames table.

## Part 6 - Loading Data into the Hive Tables

To populate a Hive table with the contents of a local file, you use the LOAD DATA LOCAL INPATH … command.

1. Enter the following command:

```sql
LOAD DATA LOCAL INPATH '/home/cloudera/Works/sizes.dat' OVERWRITE INTO TABLE tblSizes;
```

This command will load the contents of the sizes.dat file into the tblSizes Hive table. In the output you should see the line containing OK.

2. Enter the following command:

```sql
LOAD DATA LOCAL INPATH '/home/cloudera/Works/names.dat' OVERWRITE INTO TABLE tblFileNames; 
```

This command will load the contents of the names.dat file into the tblFileNames Hive table.

On receiving the above commands, Hive will go ahead and upload the files from the local file system into the respective HDFS directories under the /user/hive/warehouse/ folder. 3. Enter the following command:

```console
dfs -ls -R /user/hive;
```

You should see the following output:

```console
drwxrwxrwx - hive hive drwxrwxrwx - cloudera hive

-rwxrwxrwx 1 cloudera hive drwxrwxrwx - cloudera hive

-rwxrwxrwx 1 cloudera hive

0 ... /user/hive/warehouse 0 ... /user/hive/warehouse/tblfilenames 153317 ... /user/hive/warehouse/tblfilenames/names.dat 0 ... /user/hive/warehouse/tblsizes 30425 ... /user/hive/warehouse/tblsizes/sizes.dat
```



88 Now we have everything to start joining tables using HiveQL.

## Part 7 - Using the SELECT Statement in HiveQL

Now let's apply our knowledge of SQL. 1. Enter the following SQL command:

```sql
SELECT COUNT (*) FROM tblFileNames;
```

Be patient, it may take a couple of minutes to complete this simple job.

When the job completes, you should see diagnostic messages of the MapReduce job created and executed to process the above SQL command:

```console
...

2015-07-03 14:45:15,389 Stage-1 map = 0%, reduce = 0% 2015-07-03 14:46:16,017 Stage-1 map = 0%, reduce = 0% 2015-07-03 14:46:25,248 Stage-1 map = 100%, reduce = 0%, Cumulative CPU 14.93 sec 2015-07-03 14:46:40,738 Stage-1 map = 100%, reduce = 100%, Cumulative CPU 16.9 sec MapReduce Total cumulative CPU time: 16 seconds 900 msec Ended Job = job_1435930002876_0003 MapReduce Jobs Launched:

Stage-Stage-1: Map: 1 Reduce: 1 Cumulative CPU: 16.9 sec HDFS Read: 159766 HDFS Write: 5 SUCCESS Total MapReduce CPU Time Spent: 16 seconds 900 msec OK 1932 Time taken: 158.855 seconds, Fetched: 1 row(s)
```

As you can see the number of records (1932) matches the one we calculated before using the wc tool.

Let's use HiveQL (the plain-vanilla SQL, really) to find the maximum file size created in each month.

2. Enter the following command:

```sql
SELECT MAX(fileSize), month FROM tblSizes GROUP BY month;
```

You should see the following output:

```console
<output skipped for size> OK 599121 Apr 12063 Aug 1239320 Dec 2727679 Feb
89 38701 Jan 31873 Jul 67954 Jun 136032 Mar 17968 May 94714 Nov 151051 Oct 280468 Sep Time taken: 32.739 seconds, Fetched: 12 row(s)
```

Note: If you see NULL values in your output and you would like to suppress them, use the IS NOT NULL condition in the WHERE clause, e.g. … WHERE fileSize IS NOT NULL.

## Part 8 - Joining Tables

So far we have worked with single tables only, let's try out joining tables.

HiveQL syntax for joining tables is similar to that of SQL's inner join statement.

1. Enter the following SQL command:

```sql
SELECT tblSizes.*, tblFileNames.filename FROM tblSizes JOIN tblFileNames ON (tblSizes.id = tblFileNames.id);
```

You should see diagnostic messages of the MapReduce job created and ran to process the above SQL command. As the result of the MapReduce job, you should see the following output in your console (the last portion of the output is shown below):


```console
<lines skipped for size> 1927 8478 Feb 21 /usr/lib64/python2.6/idlelib/TODO.txt 1928 3652 Feb 21 /usr/lib64/python2.6/idlelib/extend.txt 1929 793 Feb 21 /usr/lib64/python2.6/lib2to3/PatternGrammar.txt 1930 6536 Feb 21 /usr/lib64/python2.6/lib2to3/Grammar.txt 1931 31246 Feb 22 /usr/lib64/xorg/protocol.txt 1932 1254 Jan 3 /usr/lib64/xulrunner-2/README.txt Time taken: 23.54 seconds, Fetched: 1932 row(s)
```

Which corresponds to the last three lines of sizes.dat and nams.dat flat files if joined together.

The output is sorted by the id (auto-incremented) field which came in quite handy in our case. But what if we wanted to have the output sorted by the file size (the second column in the output). HiveQL also supports sorting of the content with the ORDER BY clause offered by SQL.



2. Enter the following command:

```sql
SELECT tblSizes.*, tblFileNames.filename FROM tblSizes JOIN tblFileNames ON (tblSizes.id = tblFileNames.id) ORDER BY tblSizes.filesize;
```

You should see the following output:

```console
<output skipped for size>

1757 416238 Dec 7 /usr/share/mysql/errmsg.txt 1739 512853 Apr 21 /usr/share/doc/solr-doc-...

1346 599121 Apr 21 /usr/share/doc/hadoop ...

1772 708738 Feb 21 /usr/share/perl5/unicore/EastAsianWidth.txt 1780 763795 Feb 21 /usr/share/perl5/unicore/LineBreak.txt 1762 938846 Feb 21 /usr/share/perl5/unicore/NamesList.txt 1771 1117369 Feb 21 /usr/share/perl5/unicore/UnicodeData.txt 759 1239320 Dec 31 /usr/share/doc/vmware-tools/open_source_licenses.txt 1782 1270011 Feb 21 /usr/share/perl5/Unicode/Collate/allkeys.txt 717 2727679 Feb 21 /usr/share/hwdata/oui.txt Time taken: 30.184 seconds, Fetched: 1932 row(s)
```

We are almost done in this lab.

## Part 9 - Clean-up

1. At the hive> prompt, enter the following command:

``sql
DROP TABLE tblFileNames;
```

2. Enter the following command:

```sql
DROP TABLE tblSizes;
```

Let's confirm deletion of all data related to the deleted tables. 3. Enter the following command:

```console
dfs -ls -R /user/hive/;
```

You should see the following output:

```console
drwxrwxrwx

- hive hive

0 <your time stamp> /user/hive/warehouse
```

We are done working in the Hive shell.



4. Enter the following command:

```console
quit;
```

You should be returned to the local system command prompt in the /home/cloudera/Works folder.

5. Enter the following command:

```bash
hadoop fs -rm -r -skipTrash /user/cloudera/*
```

6. Enter the following command:

```console
pwd
```

You should see the following output confirming that you are, indeed, in the working folder:

```console
/home/cloudera/Works
```

7. Enter the following command:

```bash
rm -f *
```

## Part 10 - Ending the Working Session

Type in `exit` and press Enter to close your terminal window. This is the last step in this lab.

## Part 11 - Review

In this lab you learned how to use the SELECT statement and join tables in HiveQL.
