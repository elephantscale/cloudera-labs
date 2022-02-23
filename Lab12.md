# Lab 12 - Table Partitioning in Hive

Hive has the notion of partitioned tables that helps with distributing data processing and organizing data into hierarchical folder structures.

In this lab, you will learn how to work with single and then with two-level partitioning which, once understood, can be extended to support multi-layer table partitioning hierarchies.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 2 - The Lab Working Directory

All the steps in this lab will be performed in the /home/cloudera/Works directory.

1. In the terminal window, type in the following command:

```bash
cd ~/Works
```

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

## Part 4 - Single Level Partitioning System Design

We will be building a data warehouse system for a fictitious international bank (let's name it just bank) that will store all bank's transactions done in three cities: London, New York and Toronto; the table will be partitioned by the city column.

Our table structure will need to capture the following attributes of files in every city:

 * txnID
 * Transaction id
 * txnDateTime
 * txnAmount
 * Date/time transaction was made
 * Amount ($$) of transaction

## Part 5 - The Input Files

We will use three comma-delimited text files each of which will be placed into a separate partition representing a city in our partitioned table.

1. Enter the following commands one after another:

```bash
cp ~/LabFiles/London.dat 
cp ~/LabFiles/New_York.dat 
cp ~/LabFiles/Toronto.dat
```


2. Enter the following command:

```bash
cat Toronto.dat
```

You should see the following output:

```console
1,2015-07-03 16:19:11,100000 
2,2015-07-03 16:19:12,200000 
3,2015-07-03 16:19:13,300000 
4,2015-07-03 16:19:14,400000 
5,2015-07-03 16:19:15,500000 
6,2015-07-03 16:19:16,600000 
7,2015-07-03 16:19:17,700000
```

The middle column is a timestamp value in the "YYYY-MM-DD HH:MM:SS" format that is understood by Hive's TIMESTAMP data type.

Note: Hive's TIMESTAMP format is compliant with Java's java.sql.Timestamp format "YYYY-MM-DD HH:MM:SS[.fffffffff]", where fffffffff represents optional nanosecond precision on systems supporting it.

Note: You can save yourself time typing in commands in the terminal window by calling and then updating previous commands from the terminal buffer with keyboard navigation arrows (Up and Down).



## Part 6 - Creating a Table in HiveQL

Start the interactive Hive shell by running the hive command from the system prompt.

1. Enter the following command to start the Hive shell:

```bash
hive
```

You should be placed in the Hive shell and presented with the hive> prompt.

Note 1: When you enter Hive commands, make sure you end them with the semi-colon (;)

Note 2: You can save yourself time typing in commands in the Hive shell window by calling previous commands from the buffer with keyboard navigation arrows (Up and Down).

2. Enter the following commands at the hive> prompt (press Enter after each line):

```sql
CREATE TABLE bank (txnID STRING, txnDateTime TIMESTAMP, txnAmount BIGINT) PARTITIONED BY (city STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;
```

As you are entering commands, you will be prompted with the interim '>' prompt. After the last line, the whole command will be submitted to Hive for execution (which is triggered by the closing semi-column ';').

Note: Hive does not have the LONG data type, so we use BIGINT which is an 8 byte signed integer instead. We need it really big as our bank is capable of transferring really big amounts of money.

The city field is the partition attribute of our data set that will hold data from corresponding input files to be loaded from the local file system.



3. Enter the following command to see the current extended definition of the bank table:

```sql
DESCRIBE EXTENDED bank;
```

You should see the following output:

```console
OK 
txnid txndatetime txnamount city

string timestamp bigint string

# Partition Information # col_name city string <output skipped for space ...>

data_type

comment
```

As you see, city is included in the extended table definition all right. 

4. Enter the following command:

```console
dfs -ls -R /user/hive;
```

You should see the following output (your time stamp will differ):

```console
drwxrwxrwx drwxrwxrwx

- hive hive

- cloudera hive

0 2015-07-03 16:25 /user/hive/warehouse 0 2015-07-03 16:25 /user/hive/warehouse/bank

```

## Part 7 - Loading Data into the Hive Table

Let's load the data from our input files on the local file system into corresponding partitions that we will create as part of the LOAD DATA command.

1. Enter the following command line by line pressing Enter after each line:

```sql
LOAD DATA LOCAL INPATH '/home/cloudera/Works/Toronto.dat' OVERWRITE INTO TABLE bank PARTITION (city = 'Toronto');
```



You should see the following output:

```console
Loading data to table default.bank partition (city=Toronto) Partition default.bank{city=Toronto} stats: [numFiles=1, numRows=0,

totalSize=203, rawDataSize=0]

OK Time taken: 10.022 seconds
```

The output mentions the partition itself (see the Loading data to table default.bank partition (city=Toronto) line):

You will remember, that default is the default Hive database.

Now let's load data into New York and London partitions as well.

2. Enter the following command line by line pressing Enter after each line:

```sql
LOAD DATA LOCAL INPATH '/home/cloudera/Works/New_York.dat' OVERWRITE INTO TABLE bank PARTITION (city = 'New York');
```

As you can see, spaces in the partition names ('New York') are allowed.

3. Enter the following command line by line pressing Enter after each line:

```sql
LOAD DATA LOCAL INPATH '/home/cloudera/Works/London.dat' OVERWRITE INTO TABLE bank PARTITION (city = 'London');
```

OK, we loaded all our data.

Let's verify the results of data loading. 

##4. Enter the following command:

```console
dfs -ls -R /user/hive;
```

You should see the following output (some parts of the output are aliased with … for space):

```console
drwxrwxrwx - hive hive 0 .../user/hive/warehouse drwxrwxrwx - cloudera hive 0 .../user/hive/warehouse/bank drwxrwxrwx - cloudera hive 0 .../user/hive/warehouse/bank/city=London
-rwxrwxrwx 1 cloudera hive 217 .../user/hive/warehouse/bank/city=London/London.dat drwxrwxrwx - cloudera hive 0 .../user/hive/warehouse/bank/city=New York
-rwxrwxrwx 1 cloudera hive 213 .../user/hive/warehouse/bank/city=New York/New_York.dat drwxrwxrwx - cloudera hive 0 .../user/hive/warehouse/bank/city=Toronto
-rwxrwxrwx 1 cloudera hive 203 .../user/hive/warehouse/bank/city=Toronto/Toronto.dat
```



So, as you can see, partitioning in our case is done by creating the following folder hierarchy:

/user/hive/warehouse/bank/city=<Name of the city>

where the partition folder comes under the table name (bank).

5. Enter the following command:

```sql
SHOW PARTITIONS bank;
```

You should see the following output listing the new partitions that we just created with the LOAD DATA command:

```console
OK city=London city=New York city=Toronto Time taken: 0.228 seconds, Fetched: 3 row(s)
```

Looks like everything is in place; now it's time to run some queries against our partitioned table.

## Part 8 - Running HiveQL Queries

1. Enter the following SQL command:

```sql
SELECT * FROM bank;
```

You should see the following output (no MapReduce job was run as the output is simply a formatted dump of the underlying data contained in our flat files):

``console
OK 15 16 17 18 19 20 21 8 9 10 11 12 13

2015-07-03 16:19:25 2015-07-03 16:19:26 2015-07-03 16:19:27 2015-07-03 16:19:28 2015-07-03 16:19:29 2015-07-03 16:19:30 2015-07-03 16:19:31 2015-07-03 16:19:18 2015-07-03 16:19:19 2015-07-03 16:19:20 2015-07-03 16:19:21 2015-07-03 16:19:22 2015-07-03 16:19:23

1500000

1600000

1700000

1800000

1900000

2000000

2100000

800000

900000

1000000

1100000

1200000

1300000

London London London London London London London New York New York New York New York New York New York



14 2015-07-03 16:19:24 1400000 New York 1 2015-07-03 16:19:11 100000 Toronto 2 2015-07-03 16:19:12 200000 Toronto 3 2015-07-03 16:19:13 300000 Toronto 4 2015-07-03 16:19:14 400000 Toronto 5 2015-07-03 16:19:15 500000 Toronto 6 2015-07-03 16:19:16 600000 Toronto 7 2015-07-03 16:19:17 700000 Toronto Time taken: 1.382 seconds, Fetched: 21 row(s)
```

2. Enter the following command (in one line) which uses the time stamp of the transaction with id 16:

```sql
SELECT * FROM bank WHERE txnDateTime < to_utc_timestamp('2015-07-03 16:19:26', '');
```

You should get the output that includes only transactions belonging to New York and Toronto as only transactions in those cities have time stamps before London's transaction:

```console
OK 15 2015-07-03 16:19:25 1500000 London 8 2015-07-03 16:19:18 800000 New York 9 2015-07-03 16:19:19 900000 New York 10 2015-07-03 16:19:20 1000000 New York 11 2015-07-03 16:19:21 1100000 New York 12 2015-07-03 16:19:22 1200000 New York 13 2015-07-03 16:19:23 1300000 New York 14 2015-07-03 16:19:24 1400000 New York 1 2015-07-03 16:19:11 100000 Toronto 2 2015-07-03 16:19:12 200000 Toronto 3 2015-07-03 16:19:13 300000 Toronto 4 2015-07-03 16:19:14 400000 Toronto 5 2015-07-03 16:19:15 500000 Toronto 6 2015-07-03 16:19:16 600000 Toronto 7 2015-07-03 16:19:17 700000 Toronto Time taken: 0.558 seconds, Fetched: 15 row(s)
```

Note: The `to_utc_timestamp()` Hive function converts the string presentation of the timestamp into the internal UTC format used by Hive.

If your table is large and is with many partitions, Hive offers a nice safety mechanism against running queries that may hog system resources in your Hadoop cluster with a MapReduce job. By default, Hive runs in "nonstrict" mode that allows running any type of supported queries. The safety cap comes with “strict” mode which prohibits querying of partitioned tables without a WHERE clause that filters on partitions.



3. Enter the following command:

```console
set hive.mapred.mode;
```

You should see the following output for default mapred mode:

```console
hive.mapred.mode=nonstrict
```

As you can see, we are running in "permissive/unrestricting" MapReduce mode. Let's install the safety mechanism.

4. Enter the following commands one after another, pressing Enter after each command:

```console
set hive.mapred.mode=strict; 
set hive.mapred.mode;
```

You should see the following output:

```console
hive.mapred.mode=strict
```

Done. Let's see this mode in action. 

5. Enter the following command:

```sql
SELECT * FROM bank;
```

You should see the following output:

```console
FAILED: SemanticException [Error 10041]: No partition predicate found for Alias "bank" Table "bank"
```

The safety mechanism works as expected. 

##6. Enter the following command:

```sql
SELECT * FROM bank WHERE city ='New York';
```



100 You should see the records specific for New York (again it was just a table scan and no MapReduce job was performed as all data was found in the same bucket and we did not apply any other WHERE predicates):

Switch back to "nostrict" mode.

7. Enter the following command:

```console
set hive.mapred.mode=nostrict;
```

Now if you run the `SELECT * FROM bank;` command, you should have an unrestricted run.

## Part 9 - Two-Level Partitioning Design

Let's assume that the board of directors of our bank decided to setup our bank operation in two offshore jurisdictions: Cayman Islands and Luxembourg with branches within each jurisdiction: Cayman Islands will have two branches: #1001 and #1002 and Luxembourg one branch: #2001.

We will re-use the existing source files such that our bank's London operations (actually, just transactions) will go into Cayman Islands' branch #1001; New York's operations will go into Cayman Islands' branch #1002, and Toronto's ops will go into Luxembourg's branch #2001.

Let's see how we can accommodate this requirement (to make things interesting, let's further assume that you are made responsible for doing this system reconfiguration and in case everything works as expected, you will be entitled to receive a tax-free bonus of, say, $999,999).

1. Enter the following command (we starting from scratch):

```sql
DROP TABLE bank;
```

2. Enter the following command:

```sql
CREATE TABLE bank (txnID STRING, txnDateTime TIMESTAMP, txnAmount BIGINT) PARTITIONED BY (offshore STRING, branch STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ','

STORED AS TEXTFILE;
```



Notice that we are adding the branch partition along with the new offshore partition.

3. Enter the following commands to load data into respective partitions:

```sql
LOAD DATA LOCAL INPATH '/home/cloudera/Works/London.dat' OVERWRITE INTO TABLE bank PARTITION (offshore = 'Cayman Islands', branch='1001');

LOAD DATA LOCAL INPATH '/home/cloudera/Works/New_York.dat' OVERWRITE INTO TABLE bank PARTITION (offshore = 'Cayman Islands', branch='1002');

LOAD DATA LOCAL INPATH '/home/cloudera/Works/Toronto.dat' OVERWRITE INTO TABLE bank PARTITION (offshore = 'Luxembourg', branch='2001');
```

4. Enter the following command:

```sql
SHOW PARTITIONS bank;
```

You should see the following output of the folder structure that supports our table partitioning:

```console
OK offshore=Cayman Islands/branch=1001 offshore=Cayman Islands/branch=1002 offshore=Luxembourg/branch=2001 Time taken: 0.073 seconds, Fetched: 3 row(s)
```

5. Enter the following command to enable header (column name) printing:

```console
set hive.cli.print.header=true;
```

6. Enter the following command:

```sql
SELECT * FROM bank WHERE offshore = 'Luxembourg';
```

You should see the following output:

```conosle
OK bank.txnid 1 2 3 4 5 6

bank.txndatetime 2015-07-03 16:19:11 2015-07-03 16:19:12 2015-07-03 16:19:13 2015-07-03 16:19:14 2015-07-03 16:19:15 2015-07-03 16:19:16

bank.txnamount 100000 200000 300000 400000 500000 600000

bank.offshore Luxembourg Luxembourg Luxembourg Luxembourg Luxembourg Luxembourg

bank.branch 2001 2001 2001 2001 2001 2001



102 7 2015-07-03 16:19:17 700000 Time taken: 0.214 seconds, Fetched: 7 row(s)


Luxembourg

2001
```

As you will recall, these are the transactions listed in the Toronto.dat file. Looks like everything works as expected (now it's time to demand the bonus!). We are almost done in this lab.

## Part 10 - Clean-up

1. At the hive> prompt, enter the following command:

```sql
DROP TABLE bank;
```

Note: All partitions will be removed as well with this command.

Confirm deletion of all data related to the bank table from the Hive's warehouse folder. 

2. Enter the following command:

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

3. Enter the following command:

```console
quit;
```

You should be returned to the local system command prompt in the /home/cloudera/Works folder.

4. Enter the following command:

```bash
hadoop fs -rm -r -skipTrash /user/cloudera/*
```

5. Enter the following command:

pwd



You should see the following output:

/home/cloudera/Works

6. Enter the following command:

```bash
rm -f *
```

## Part 11 - Stop the Services

This is the last Hive-related lab; to conserve memory on your Lab Server, you will need to shut down the Hive services.

1. Enter the following commands one after another:

```bash
sudo /etc/init.d/hive-server2 stop 
sudo /etc/init.d/hive-metastore stop
```

## Part 12 - Ending the Working Session

1. Type in exit and press Enter to close your terminal window.

This is the last step in this lab.

## Part 13 - Review

In this lab, you learned how to create a partitioned table in Hive and load it with external data. You also looked at how to query columns of the TIMESTAMP data type and install safety mechanism of enforcing the WHERE clause when querying partitioned tables.
