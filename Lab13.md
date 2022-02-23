# Lab 13 - Data Import and Export with Sqoop

Sqoop is a command-line tool for two-way data transfer data between relational database management systems (RDBMS) and the Hadoop Distributed File System (HDFS). For import and export of the data, Sqoop uses MapReduce to parallelize task execution and achieve job fault tolerance (failed tasks are transparently restarted). Hadoop-centric systems, such as Hive and HBase, use Sqoop to import data from most relational databases.

In this lab, you will learn about Sqoop's data import and export capabilities for data transfer between MySQL database and HDFS.

## Part 1 - Sqoop Import / Export Terminology

In Sqoop's refers to import / export operations from the HDFS's point of view, such that:

 * import refers to extracting data from a relational database and loading it into HDFS

 *  export is the reverse operation, where data is extracted from HDFS and inserted into a relational database

This is in contrast with the relational databases' point of view where the above operations are the opposite:

 * Sqoop's import operation is seen as the database export operation

 *  Sqoop's export operation (extraction of from HDFS into the target relational database) is seen as the import operation by the database

## Part 2 - Connect to the Lab Environment

Note: Skip this Lab ## Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 3 - The Lab Working Directory

All the steps in this lab will be performed in the /home/cloudera/Works directory.

1. In the terminal window, type in the following command:

```bash
cd ~/Works
```

## Part 4 - Working in MySQL

For import / export operations using sqoop we will be working against MySQL RDBMS that has been installed on your Lab server.

First, we need to check if the MySQL service has already been started on the Lab server. 1. Enter the following command:

```bash
sudo service --status-all
```

If you see this line:

```console
mysqld is stopped
```

you need to start the service.

2. Enter the following command to start the mysqld service:

```bash
sudo service mysqld start
```

You should see the following output:

```console
Starting mysqld:
[ OK ]
```

The LABDB database you are going to work with in MySQL has already been created for you.

3. Enter the following command to login to the LABDB database:

```bash
mysql LABDB -u root -p
```

4. Enter cloudera for password, when prompted.

You should see the following output and the mysql> shell command prompt at the end:

```console
Welcome to the MySQL monitor. Commands end with ; or \g. Your MySQL connection id is 25 Server version: 5.1.66 Source distribution
Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
Oracle is a registered trademark of Oracle Corporation and/or its affiliates. Other names may be trademarks of their respective owners.
Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
mysql>
```


Note: In this lab part, commands issued below are to be executed at the mysql> command prompt.

5. Enter the following command:

```sql
SELECT * FROM EMPS;
```

You should see the following output:

```console
+----+------------+--------------+---------------------+---------+--------+
 | id | FIRST_NAME | LAST_NAME | HIRE_DATE | JOB_CAT | SALARY | 
+----+------------+--------------+---------------------+---------+--------+
 | 1 | Joe | Doe | 2008-11-29 00:00:00 | 7 | 45000 |
 | 2 | Susan | Boghart | 1998-12-31 00:00:00 | 9 | 167000 | 
| 3 | Alim | Khan | 2010-07-31 00:00:00 | 7 | 49000 | 
| 4 | Rose | Dale | 1998-12-31 00:00:00 | 6 | 35000 |
 | 5 | Bill | Stahl | 2014-01-25 00:00:00 | 6 | 37000 |
 | 6 | Kyle | Moore | 2009-10-29 00:00:00 | 7 | 75000 |
 | 7 | Liz | Lee | 1999-12-31 00:00:00 | 9 | 169000 |
 | 8 | Mark | Wojzakhowski | 2011-02-11 00:00:00 | 7 | 99000 |
 | 9 | Mohamad | Dahl | 2000-12-31 00:00:00 | 8 | 85000 |
 | 10 | Bob | Stern | 2011-05-12 00:00:00 | 6 | 47000 | 
+----+------------+--------------+---------------------+---------+--------+ 10 rows in set (0.00 sec)
```

This is our data set that we are going to transfer into HDFS using sqoop. 
6. Enter the following command to exit the MySQL command shell:

```console
quit
```

You should be logged off from MySQL and dropped back at the local system command prompt.

## Part 5 - Getting to Know sqoop

1. Enter the following command:

```bash
sqoop help
```

You should see the following output (cut for space):

```console
usage: sqoop COMMAND [ARGS]

Available commands:

```
To get help on a specific command, enter `sqoop help <command_name>`. Let's try out sqoop.

2. Enter the following command:

```bash
sqoop list-tables --connect jdbc:mysql://localhost/LABDB --username root -P
```

3. When prompted for password, enter cloudera You should see the following output listing our EMPS table:

```console
<your timestamp>INFO manager.MySQLManager: Preparing to use a MySQL streaming resultset.

EMPS
```

Note: The MySQL JDBC connector of sqoop uses the default MySQL listener port of 3306, so the above command's connection string is equivalent to this one:

```console
... --connect jdbc:mysql://localhost:3306/LABDB
```
```

## Part 6 - Data Import with sqoop


Now that know a bit about sqoop, let's do the actual import of records from the EMPS table into HDFS.

1. Enter the following command:

```bash
sqoop import --connect jdbc:mysql://localhost/LABDB --table EMPS -m 1 --username root -P
```

2. When prompted for password, enter cloudera You should see the MaprReduce job started using 1 mapper as evidenced by the
Launched map tasks=1 line (configured by the -m 1 import command argument)

The final line in the output should be

```console
<Your time stamp> INFO mapreduce.ImportJobBase: Retrieved 10 records.
```

Note: When data serialization of the EMPS table is happening, sqoop uses auto-generated the Java serialization bean for the database records. The Java file is named after the target table name, EMPS.java in our case. Sqoop leaves this file for your reference in the working directory.

3. Enter the following command:

```bash
ls
```

You should see the following output:

```console
EMPS.java
```

Review the contents of the file.

Now let's look at what sqoop import command actually created in HDFS.

4. Enter the following command:

```console
hadoop fs -ls -R
```

You should see the following output (some information is replaced with … for space):

```console
drwxr-xr-x - cloudera cloudera EMPS 0 ... 
-rw-r--r-- 1 cloudera cloudera EMPS/_SUCCESS 456 ... 
-rw-r--r-- 1 cloudera cloudera EMPS/part-m-00000
```

5. Sqoop created a folder under /home/cloudera/ named EMPS that corresponds to the name of the imported table. Our imported data is stored in the EMPS/part-m-00000 file. Enter the following command:

```bash
hadoop fs -cat EMPS/part-m-00000
```

You should see the following output:

```console
1,Joe,Doe,2008-11-29 00:00:00.0,7,45000.0
2,Susan,Boghart,1998-12-31 00:00:00.0,9,167000.0 
3,Alim,Khan,2010-07-31 00:00:00.0,7,49000.0 
4,Rose,Dale,1998-12-31 00:00:00.0,6,35000.0 
5,Bill,Stahl,2014-01-25 00:00:00.0,6,37000.0 
6,Kyle,Moore,2009-10-29 00:00:00.0,7,75000.0 
7,Liz,Lee,1999-12-31 00:00:00.0,9,169000.0 
8,Mark,Wojzakhowski,2011-02-11 00:00:00.0,7,99000.0 
9,Mohamad,Dahl,2000-12-31 00:00:00.0,8,85000.0 
10,Bob,Stern,2011-05-12 00:00:00.0,6,47000.0
```
This is our file all right.

## Part 7 - Using Imported Data in Hive

Once we got the file imported into HDFS, we can use it in Hive (or in any other HDFScentric processing system for that matter). Let's see how we can do this.

First, we need to make sure that Hive-related services are up and running.

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

2. Launch the Hive shell:

```bash
hive
```

You should get the `hive>` command prompt. Subsequent commands are executed at this prompt.

3. Enter the following command line by line submitting each line with Enter:

``sql
CREATE TABLE hiveEMPS (empid INT, first_name STRING, last_name STRING, hire_date TIMESTAMP, job_cat INT, salary FLOAT)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE LOCATION '/user/cloudera/EMPS/';
```

Location parameter points to the HDFS folder where the imported file (part-m-00000) is located.



4. Confirm creation of the table with following command (which is remarkably similar to the MySQL's SHOW TABLES command):

```sql
SHOW TABLES;
```

You should see the following output listing our table in the lower case:

```console
OK 
hiveemps 
Time taken: 0.223 seconds, Fetched: 1 row(s)
```

Now you can run HiveQL queries against this Hive table.

5. Enter the following command:

```sql
SELECT * FROM hiveEmps;
```

You should see the content matching the content of MySQL's EMPS table and /user/cloudera/EMPS/part-m-00000 file in HDFS:

``console
OK 

1 Joe Doe 2008-11-29 00:00:00 7 45000.0
2 Susan Boghart 1998-12-31 00:00:00 9 167000.0
3 Alim Khan 2010-07-31 00:00:00 7 49000.0
4 Rose Dale 1998-12-31 00:00:00 6 35000.0
5 Bill Stahl 2014-01-25 00:00:00 6 37000.0
6 Kyle Moore 2009-10-29 00:00:00 7 75000.0
7 Liz Lee 1999-12-31 00:00:00 9 169000.0
8 Mark Wojzakhowski 2011-02-11 00:00:00 7 99000.0
9 Mohamad Dahl 2000-12-31 00:00:00 8 85000.0
10 Bob Stern 2011-05-12 00:00:00 6 47000.0
Time taken: 0.126 seconds, Fetched: 10 row(s)
```

6. Enter the following command to find all employees hired after Joe Doe (who was hired on 2008-11-29 00:00:00):

```sql
SELECT * FROM hiveEmps WHERE hire_date < to_utc_timestamp ('2008-11-29 00:00:00', '');
```


You should see the following output:

```console
OK 
2 Susan Boghart 1998-12-31 00:00:00 9 167000.0
4 Rose Dale 1998-12-31 00:00:00 6 35000.0
7 Liz Lee 1999-12-31 00:00:00 9 169000.0
9 Mohamad Dahl 2000-12-31 00:00:00 8 85000.0
Time taken: 0.241 seconds, Fetched: 4 row(s)

As you can see, we have a fully operational data set which is based on the original records of the EMPS table in the LABDB MySQL database.

For now, we are done working in the Hive shell.

7. Enter the following command to exist the Hive shell:

```sql
quit;
```

You should be returned back to the local system prompt.

## Part 8 - Using Pig to Craft the Data

Now, suppose you need to export data from the hiveEmps table (or, more exactly, from the /user/cloudera/EMPS/part-m-00000 file) into MySQL with certain fields dropped (e.g. for security or confidentiality reasons). You can use the Pig scripting platform to perform this operation. Let's see how you can do this.

1. Enter the following command to start Grunt, the Pig's shell:

```bash
pig
```

You should get the `Grunt`> prompt.

2. Enter the following command line by line pressing Enter after each line:

```pig
E = LOAD '/user/cloudera/EMPS/part-m-00000' USING PigStorage (',') AS (id:int, fname:chararray, lname:chararray, hire_date:chararray, job_cat:int, sal:float);
```

3. Enter the following command:

``pig
DUMP E;
```

A MapReduce job will be created and started, and when it is done, you should get a console dump of the E relation reference to the source file on HDFS (the output below is trimmed for space).

```console
(1,Joe,Doe,2008-11-29 00:00:00.0,7,45000.0) 
(2,Susan,Boghart,1998-12-31 00:00:00.0,9,167000.0) 
(3,Alim,Khan,2010-07-31 00:00:00.0,7,49000.0) (4,Rose,Dale,1998-12-31 00:00:00.0,6,35000.0) (5,Bill,Stahl,2014-01-25 00:00:00.0,6,37000.0) (6,Kyle,Moore,2009-10-29 00:00:00.0,7,75000.0) (7,Liz,Lee,1999-12-31 00:00:00.0,9,169000.0)
 (8,Mark,Wojzakhowski,2011-02-11 00:00:00.0,7,99000.0) (9,Mohamad,Dahl,2000-12-31 00:00:00.0,8,85000.0) (10,Bob,Stern,2011-05-12 00:00:00.0,6,47000.0)
```

The fields that you are required to drop before the export operation are: hire_date and salary. You can achieve this by running the following Pig Latin command.

4. Enter the following command:

```console
eShort = FOREACH E GENERATE $0, $1, $2, $4;

```

Here we are referencing fields by their positional indexes, which are 0-based: hire_date is the 3 rd field and salary is the 5 th which we skip in the output.

5. Enter the following command:

```pig
DUMP eShort;
```

After the generated MapReduce job completes, you should see the following output in your console:

```console
(1,Joe,Doe,7) (2,Susan,Boghart,9) (3,Alim,Khan,7) (4,Rose,Dale,6) (5,Bill,Stahl,6) (6,Kyle,Moore,7) (7,Liz,Lee,9) (8,Mark,Wojzakhowski,7) (9,Mohamad,Dahl,8) (10,Bob,Stern,6)
```

We need to store this output on HDFS rather than printing it to console.

6. Enter the following command:

```pig
STORE eShort INTO 'TO_EXPORT' USING PigStorage (',');
```

When the command completes, you should see the following output (abridged for space):

```console
Input(s):

Successfully read 10 records (838 bytes) from: "/user/cloudera/EMPS/part-m-00000"

Output(s):



Successfully stored 10 records (154 bytes) in: "hdfs://quickstart.cloudera:8020/user/cloudera/TO_EXPORT"

Counters:

Total records written : 10 Total bytes written : 154 Spillable Memory Manager spill count : 0 Total bags proactively spilled: 0 Total records proactively spilled: 0

Job DAG: job_1436274345046_0004

2015-07-07 10:47:27,991 [main] INFO org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.MapReduceL auncher - Success!
```

This command redirected the output to a file created under the TO_EXPORT directory. The fields in the file will be comma-delimited as opposed to the tab-delimited default format when the USING PigStorage () clause is omitted.

Pig interfaces with HDFS using the fs command. Let's verify creation of the TO_EXPORT folder.

7. Enter the following command:

```pig
fs -ls TO_EXPORT
```

You should see the following output:

```console
Found 2 items

-rw-r--r-- 1 cloudera cloudera

-rw-r--r-- 1 cloudera cloudera 00000

0 2015-07-07 10:47 TO_EXPORT/_SUCCESS 154 2015-07-07 10:47 TO_EXPORT/part-m-
```

The result is stored in the TO_EXPORT/part-m-00000 file. 8. Enter the following command:

```console
fs -cat TO_EXPORT/part-m-00000
```

You should see the following output:

```console
1,Joe,Doe,7 2,Susan,Boghart,9 3,Alim,Khan,7 4,Rose,Dale,6
5,Bill,Stahl,6 6,Kyle,Moore,7 7,Liz,Lee,9 8,Mark,Wojzakhowski,7 9,Mohamad,Dahl,8 10,Bob,Stern,6
```

Everything seems to be OK.

We are ready to export data using sqoop back to our MySQL database. 9. Enter the following command to exit Grunt:

```console
quit
```

You should be dropped at the local system prompt.

## Part 9 - Data Export with sqoop

Before we do data export, the destination table in MySQL (or other supported relational databases) must exist, so we need to create one.

1. Enter the following command at the local system command prompt:

```bash
mysql LABDB -u root -p
```

2. Provide cloudera for password, when prompted.

You should be placed in the MySQL command shell.

3. Enter the following command submitting each line by pressing Enter:

```sql
CREATE TABLE EMPS_SHORT ( id INT NOT NULL PRIMARY KEY, FIRST_NAME VARCHAR(32), LAST_NAME VARCHAR(32), JOB_CAT INT );
```

This command will create the EMPS_SHORT table for receiving data we are going to export from HDFS using sqoop.

We are done with MySQL for now.

4. Enter the following command to exit the MySQL command shell:



quit

You should be dropped at the local system prompt.

5. Enter the following command at the local system prompt:

sqoop export --connect jdbc:mysql://localhost/LABDB \

--table EMPS_SHORT \

--export-dir /user/cloudera/TO_EXPORT --username root -P

6. Provide cloudera for password, when prompted.

When the process completes, you should see the following output (abridged for space):

...INFO mapreduce.ExportJobBase: Exported 10 records.

Let's confirm the results of the export operation.

7. Enter the following command at local system command prompt; provide cloudera for password, when prompted.

```bash
sqoop eval --connect jdbc:mysql://localhost/LABDB -e 'SELECT * FROM EMPS_SHORT;' --username root -P
```

You should see the following output:

```console
--------------------------------------------------------------------------| id | FIRST_NAME | LAST_NAME | JOB_CAT |

--------------------------------------------------------------------------| 9 | Mohamad | Dahl | 8 | | 10 | Bob | Stern | 6 | | 1 | Joe | Doe | 7 | | 2 | Susan | Boghart | 9 | | 3 | Alim | Khan | 7 | | 4 | Rose | Dale | 6 | | 5 | Bill | Stahl | 6 | | 6 | Kyle | Moore | 7 | | 7 | Liz | Lee | 9 | | 8 | Mark | Wojzakhowski | 7 |

---------------------------------------------------------------------------
```

These are the records from the /user/cloudera/TO_EXPORT/part-m-00000 file. We are almost done in this lab.



## Part 10 - Clean-up

1. Enter the following command:

```bash
hive
```

2. At the `hive>` prompt, enter the following command:


```sql
DROP TABLE hiveEMPS;
```

3. Enter the following command:

```console
dfs -rm -r -skipTrash /user/cloudera/* ;
```

We are done working in the Hive shell.

4. Enter the following command to exit Hive:

```console
quit;
```

You should be returned to the local system command prompt in the /home/cloudera/Works folder.

5. Enter the following command:

```bash
pwd
```

You should see the following output: /home/cloudera/Works 

6. Enter the following command to delete unneeded files:


```bash
rm -f *
```

We also need to delete MySQL tables we created in this lab.

7. Enter the following command and provide cloudera for password, when prompted:

mysql LABDB

-u root -p



8. At the mysql> command prompt enter the following commands:

```sql
DROP TABLE EMPS_SHORT;
```

9. Enter the following command to exit the MySQL command prompt:

```sql
quit
```

You should be dropped back at the local system command prompt in the /home/cloudera/ Works folder.

10. Enter the following command:

```bash
sudo service mysqld stop
```

To conserve memory on your Lab Server, you will need to shut down the Hive services.

11. Enter the following commands one after another:

```bash
sudo /etc/init.d/hive-server2 stop 
sudo /etc/init.d/hive-metastore stop
```


12. Type in exit and press Enter to close your terminal window.

This is the last step in this lab.

## Part 11 - Review

In this lab, you learned how to run sqoop for data import and export between MySQL and HDFS. We also used Hive and Pig to show how these systems can be effectively used to aid you in solving your practical problems.
