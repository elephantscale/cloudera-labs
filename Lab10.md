# Lab 10 - Hive Data Definition Language

The Hive Data Definition Language (DDL) supports a variety of system operations for creating, altering and dropping databases, tables, views, columns and partitions.

In this lab, you will learn how to work with most important Hive DDL commands you will also learn about Hive's warehouse folder structure in HDFS.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab  Part if you are using the Lab Server's desktop environment setup.

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

## Part 4 - The Input File

We are going to use a comma-delimited text file that contains a portion of the /usr/bin directory listing on the local file system.



1. Enter the following command:

```bash
cp ~/LabFiles/files.dat .
```

The files.dat has the following comma-delimited fields:

1. File name

2. File size

3. Month of creation

4. Day of creation

Now that we have the input file on the local file system, we need to upload it to HDFS. With Hive, however, this step is not required as the upload will be done transparently for the user as part of the `LOAD DATA LOCAL INPATH <local path to the input file> INTO TABLE <hive table name>` command issued from inside the Hive shell. This step will be done later in the lab.

## Part 5 - Creating a Table in HiveQL

In this lab, we are going to use the default database that is created by Hive.

You start the interactive Hive shell by running the hive command from the system prompt.

1. Enter the following command to start the Hive shell:

```bash
hive
```

You should be placed in the Hive shell and presented with the hive> prompt.

Note 1: When you enter Hive commands, make sure you end them with the semi-colon (;) Note 2: Although HiveQL commands and statements are case-insensitive, we will adhere to using the upper case for them, e.g. SHOW TABLES;



2. Enter the following command:

```sql
SHOW DATABASES;
```

You should be presented with the following output:


```console
OK default Time taken: 1.607 seconds
```

The default database is automatically supplied by Hive. To start using it, you don't need to issue any commands. For any other database you create in Hive, you would need to use the `USE YOUR_DB_NAME` command.

Let's create a table called tblFiles with the structure that will allow us import the contents of the files.dat file we created earlier.

3. Enter the following commands at the hive> prompt (press Enter after each line):

```sql
CREATE TABLE tblFiles (fileName STRING, fileSize INT, month STRING, day INT) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' 
STORED AS TEXTFILE;
```

As you are entering commands, you will be prompted with the interim '>' prompt.:

```console
hive> CREATE TABLE tblFiles (fileName STRING, fileSize INT, month STRING, day INT)

> ROW FORMAT DELIMITED FIELDS TERMINATED BY ','

> STORED AS TEXTFILE;
```

After the last line, all three lines will be submitted to Hive for execution (which is triggered by the closing semi-column ';')

4. Enter the following command:

```sql
SHOW TABLES;
```

You should see the newly created table tblfiles (in lower case) listed in the command output.

5. Enter the following command:

```sql
DESCRIBE tblFiles;
```



You should get the following output:

filename filesize month day

string int string int

Where does Hive store the metadata about its tables and other artifacts?

6. Enter the following command:

```console
set hive.metastore.warehouse.dir;
```

You should see the following output:

```console
hive.metastore.warehouse.dir=/user/hive/warehouse
```

So, the tblFiles table definition is stored in the `/user/hive/warehouse` HDFS folder.

Note: Location of the tables that you create in Hive is specified in the `hive.metastore.warehouse.dir` property of the `hive-site.xml` configuration file:

```xml
<property>

<name>hive.metastore.warehouse.dir</name>

<value>/user/hive/warehouse</value>

</property>
```

Location of the hive-site.xml depends on the deployment bundle.

7. Enter the following command:

```console
dfs -ls /user/hive/warehouse;
```

You should see the following output:


```console
Found 1 items
drwxrwxrwx - cloudera hive 0 2015-07-03 13:46 /user/hive/warehouse/tblfiles
```

Notice in the preceding line that the user name under which the folder for tblFiles was created is cloudera and the group stays as hive.

Note: When you load data in the table, the group will be further changed to supergroup.



## Part 6 - Loading Data into a Hive Table

To populate the tblFiles Hive table with the content of the files.dat local file system file, we need to issue this LOAD DATA LOCAL INPATH … command.

1. Enter the following command (in one line):

```sql
LOAD DATA LOCAL INPATH '/home/cloudera/Works/files.dat' OVERWRITE INTO TABLE tblFiles;
```


On receiving this command, Hive will go ahead and upload the files.dat file from the local file system to the /user/hive/warehouse/tblfiles HDFS folder.

You should see the following output of this command (some details will differ in your output):

```console
Loading data to table default.tblfiles 
Table default.tblfiles stats: [numFiles=1, numRows=0, totalSize=28798, rawDataSize=0] 
OK 
Time taken: 1.249 seconds
```

The system reports the size of the uploaded file (28798). 2. Enter the following command to confirm file creation:

```bash
dfs -ls -R /user/hive;
```

You should see the following output:

```console
drwxrwxrwx - hive hive drwxrwxrwx - cloudera hive -rwxrwxrwx 1 cloudera hive
0 2015-07-03 13:46 /user/hive/warehouse 0 … /user/hive/warehouse/tblfiles 28798 … /user/hive/warehouse/tblfiles/files.dat
```

As you can see, the files.dat file is there all right in the tblfiles folder which logically represents a table in Hive.

## Part 7 - Changing the Table Name

Hive supports standard SQL syntax for changing table names:

```sql
ALTER TABLE old_table_name RENAME TO new_table_name;
```

Let's see rename our table to something more descriptive.



1. Enter the following command:

```sql
ALTER TABLE tblFiles RENAME TO tblFilesInfo;
```

You should see the following output:

```console
OK 
Time taken: 0.434 seconds
```

Let's see what was changed on HDFS. 

2. Enter the following command:

```console
dfs -ls -R /user/hive;
```


You should see the following output reflecting the results of our operation:

```console
drwxrwxrwx - hive hive drwxrwxrwx - cloudera hive -rwxrwxrwx 1 cloudera hive
0 ... /user/hive/warehouse ... /user/hive/warehouse/tblFilesInfo 28798 ... /user/hive/warehouse/tblFilesInfo/files.dat
```

As you can see, only the table directory name has changed to tblFilesInfo.

## Part 8 - Working with a View

Views can help you restrict the amount of viewable data or hide away complex queries. Let's see how we can apply our knowledge of views in practice.

Suppose, you want to get a list of files that were created on the first of each month. Since you know that the day of month of each rows to be returned is 1, then you don't want to display it as part of the output.

1. Enter the following command (as one line):

```sql
CREATE VIEW vFirstDayOfMonth AS SELECT fileSize, month FROM tblFilesInfo WHERE day = 22;
```

You should see the following output:

```console
OK 
Time taken: 0.147 seconds
```



2. Enter the following command:

```sql
SHOW TABLES;
```

You should see the following output:

```console
OK 
tblfilesinfo vfirstdayofmonth 
Time taken: 0.098 seconds
```

The output lists our new view as a table.

But if you run the dfs -ls -R /user/hive; command, you won't see a separate folder created for our view under the warehouse directory.

3. Enter the following command:

```sql
SELECT * FROM vFirstDayOfMonth;
```

You should see the following output (the tail of the output is shown below):


```console
...
2167 Aug 
78296 Jun 
25664 Jun 
801 Feb 
9056 Jun 
8007 Jun 
7603 Jun
7931 Jun 
13580 Jun 
11030 Jun 
Time taken: 0.122 seconds, Fetched: 217 row(s)
```

We are done working with our view. 4. Enter the following command:

```sql
DROP VIEW vFirstDayOfMonth;
```

This command should run successfully. We are almost done in this lab.



## Part 9 - Clean-up

1. At the hive> prompt, enter the following command:

```sql
DROP TABLE tblFilesInfo;
```

You should get the message about successful deletion of our table:

```console
OK Time taken: 4.152 seconds

Confirm deletion of all data related to the tblFilesInfo table from the Hive's warehouse folder.

2. Enter the following command:

```console
dfs -ls -R /user/hive/;
```

You should see the following output:


```console

drwxrwxrwx - hive hive

0 <your time stamp> /user/hive/warehouse
```

We are done working in the Hive shell. 

3. Enter the following command:

```
quit;
```

You should be returned to the local system command prompt in the /home/cloudera/Works folder.

4. Enter the following command (don't forget /* at the end of the command!):

```bash
hadoop fs -rm -r -skipTrash /user/cloudera/*
```

5. Enter the following command:

```bash
rm -f files.dat
```

## Part 10 - Ending the Working Session

1. Type in exit and press Enter to close your terminal window. This is the last step in this lab.

