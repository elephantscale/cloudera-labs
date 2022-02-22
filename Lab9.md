# Lab 9 - The Hive and Beeline Shells

Hive comes with its own command-line interface (CLI) represented by the hive command line tool which allows users to execute HiveQL commands in either interactive or unattended (batch) mode.

 * In interactive mode, the user enters HiveQL-based queries manually, submitting commands sequentially

 * In unattended (batch) mode, the Hive shell takes command parameters on the command-line

Beeline is the latest command-line interface (CLI) to work with the Hive server. While under the hood Beeline is quite different from the hive command-line tool, from a single user perspective, it offers the same functionality for executing HiveQL commands in either interactive or unattended (batch) mode.

The Beeline shell is launched by running the beeline command.

In this lab, you will learn how to work with Hive and the Beeline shells.

We will do a side-by-side demonstration of using the hive and beeline command line tools so that you will be able to see the similarities and differences between these two Hive tools that you may encounter in Hadoop environments.

You will be shown the commands as issued using the hive tool. If the command syntax of a functionally equivalent beeline command is different, this beeline command will be given as a note.

Note: You can run Beeline in either embedded or remote mode of operation. In our lab we will use the embedded option as our Beeline client and the Hive Server are collocated on the same host machine.

In this lab, we use the Hive client and the Beeline shell version 1.1.0.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab ## Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 2 - The Lab Working Directory

All the steps in this lab will be performed in the /home/cloudera/Works directory.

1. In the terminal window, type in the following command:

```bash
cd ~/Works
```



## Part 3 - Getting Started with the Hive Shell in Interactive Mode

Before we begin, we need to make sure that Hive-related services are up and running. 1. Enter the following commands one after another:

```bash
sudo /etc/init.d/hive-metastore status 
sudo /etc/init.d/hive-server2 status
```

If you see their status as not running, start the stopped service(s) using the following commands:

```bash
sudo /etc/init.d/hive-metastore start 
sudo /etc/init.d/hive-server2 start
```

You start the interactive Hive shell by running the hive command from the system prompt.

2. Enter the following command:

```bash
hive
```

You should be placed in the Hive shell and presented with hive> prompt. Note: You start the Beeline shell in embedded mode by running this command:

```bash
beeline -u jdbc:hive2://
```

Ignore any warnings that you may see on the console, e.g. with regard to Beeline.

Note: When you enter Hive commands, make sure you end them with the semi-colon (;) All hive commands, when repeated in beeline, would produce similar outputs which are only slightly different in their format.

3. Enter the following command:

```SQL
SHOW TABLES;
```

You should be presented with the following output (your command execution time may be different):

```console
OK Time taken: 0.107 seconds
```

The Hive shell contacted the Hive metastore and it responded back that there is no tables or views created so far in Hive.



Note: Beeline will come back with a differently formatted output:

```console
OK 
+-----------+--+ tab_name | +-----------+--+ +-----------+--+ 
No rows selected (0.524 seconds)
```

It may also print a warning message "… MetaStoreClient lost connection ..." which is automatically dealt with by a successful reconnect attempt.

If you see this message, you can safely ignore it.

4. Enter the following command:

```sql
set;
```

A long list of system and configuration variables should be displayed.

Later in the lab, you will learn how to capture this list in a text file for browsing.

Let's find the version of Java installed on the system; this value is cached in the system namespace.

5. Enter the following command:

```sql
set system:java.version;
```

You should get the following output:

```consolej
system:java.version=1.7.0_67
```

Let's check the location of Hive's metastore on HDFS. 6. Enter the following command:

```sql
set hive.metastore.warehouse.dir;
```

You should see the following output:

```console
hive.metastore.warehouse.dir=/user/hive/warehouse
```

The Hive shell offers command interface to HDFS via the dfs command. Let's see what is currently in the Hive's metastore.



7. Enter the following command at Hive shell's prompt hive>:

```console
dfs -ls /user/hive/warehouse;
```

The shell should return nothing as you have not created anything yet in Hive.

Let's quit the Hive shell for a moment.

8. Enter the following command:

```console
quit;
```

You should be placed back at the system prompt.

Note: Beeline internal command for ending the current shell session is:

```console
!quit
```

Let's change the Hive shell's prompt from the standard hive> to our own and creative hi! >

9. Enter the following command:

```bash
hive --hiveconf hive.cli.prompt=hi!
```

A new Hive shell session will start, and you will get a new and a bit more welcoming prompt:

```console
hi!>
```

Note: The functionally equivalent command in Beeline is:

```console
beeline -u jdbc:hive2:// --hiveconf hive.cli.prompt=hi!
```

Depending on the Hadoop platform you are running Beeline on, it may (or may not!) throw a run time exception when executing the above command using the beeline tool:

```console
java.lang.IllegalArgumentException: Cannot modify hive.cli.prompt at runtime. It is not in list of params that are allowed to be modified at runtime
```

The Hive shell supports execution of host OS (operation system) commands. OS commands must be prefixed with '\!' (a backslash followed by a bang) and terminated with ';'.

10. Enter the following command:

```console
\!pwd;
```



The output should list your working folder on the local system:

```console
/home/cloudera/Works
```

Note: The functionally equivalent beeline command is

```console
!sh pwd
```

Now, let's create a simple table using the Hive Data Definition Language (DDL). 11. Enter the following command:

```sql
CREATE TABLE FOO (id INT, name STRING);
```

You should get an 'OK' as a confirmation message back. 12. Enter the following command:

```sql
SHOW TABLES;
```

You should see the following output:

```console
OK 
foo 
Time taken: <your time> seconds
```

13. Enter the following command:

```sql
DESCRIBE foo;
```

You should see the following output:

```console
OK 
id int name string 
Time taken: 0.2 seconds, Fetched: 2 row(s)
```

14. Enter the following command:

```console
dfs -ls /user/hive/warehouse;
```



Now that you have created a table in Hive, you should be able to see the related information about its definition in the metastore:

```console
Found 1 items drwxrwxrwx

- cloudera hive

0 <TIME STAMP> /user/hive/warehouse/foo
```

Let's now switch gears towards using Hive in batch mode. 15. Enter the following command to quit the Hive shell:

```console
quit;
```

You should be placed back at the system prompt.

Note: If you want to minimize Hive system output to console, start the Hive shell with the -S flag:

```bash
hive -S
```

## Part 4 - Getting Started with the Hive Shell in Batch Mode

You can also execute Hive commands in non-interactive (or batch / unattended) mode. Let's first get a basic idea about hive tool's setup.

1. Enter the following command (the '`' is the backtick character normally located under the Esc key and next to the 1 key):

```bash
cat `which hive`
```

You should see the following output (abridged for space):

```console
. . . 
export HIVE_HOME=/usr/lib/hive exec /usr/lib/hive/bin/hive "$@"
```

Commands that will be accepted by the Hive shell are passed to it via the "$@" bash variable.

To get help on commands that can be passed on to the Hive shell can be obtained by running the hive -help command.

2. Enter the following command:

```bash
hive -H
```



You should get a help output

```bash
beeline --help
```

What is interesting here is that in order to print help, you are placed in a newly created Beeline shell session and is left there (which is, probably, not what you intention was!)

3. Enter the following command:

```bash
hive -S -e "SHOW TABLES;"
```

Note: The beeline command for that is

```bash
beeline -u jdbc:hive2:// -e "SHOW TABLES;"
```

The -S option is not supported.

You should get back the list of existing tables in Hive:

foo

If you issue this command without the -S flag (silent mode), you will get some additional information.

4. Enter the following command:

```bash
hive -S -e "SELECT * FROM foo;"
```



72 Note: The beeline command for that is

```bash
beeline -u jdbc:hive2:// -e "SELECT * FROM foo;"
```

Nothing should be returned back as you don't have any records in the foo table.

Let's see how to use a Hive script.

5. Using a text editor of your choice, create a text file with the following content; name the file myHiveScript.q

``sql
DROP TABLE foo;
```

Note: You can also use the following command for that:

```bash
echo "DROP TABLE foo;" > myHiveScript.q
```

6. Save the file and exit the editor. 7. Enter the following command:

```bash
hive -f myHiveScript.q
```

Note: The beeline command for that is

```bash
beeline -u jdbc:hive2:// -f myHiveScript.q
```

In case of successful command execution, you should see the following output in your console:

```console
<output skipped for space> 
OK 
Time taken: <your script time> seconds
```

8. Enter the following command:

```bash
hive -S -e "DESCRIBE foo;"
```

Note: The beeline command for that is

```bash
beeline -u jdbc:hive2:// -e "DESCRIBE foo;"
```

In the output of the command, you should see this line:

```console

FAILED: SemanticException [Error 10001]: Table not found foo
```



73 You can also confirm deletion of the table by running this command (which should return nothing):

```bash
hive -S -e "SHOW TABLES;"
```

9. Enter the following command at the system command prompt:

```bash
hadoop fs -ls -R /user/hive/warehouse

The command should return nothing as, after dropping the table, Hive also removed any meta information about it.

When you get a list of system and Hive configuration variables while in the Hive shell, you may want to capture this list in a text file. That's how you can do it.

10. Enter the following command:

```bash
hive -S -e "set;" > hiveVars.dat
```


Now you can use the hiveVars.dat file created in the working folder for your reference. Take a moment to browse through its content (you can use the less hiveVars.dat command; quit by typing in q).

Note: The functionally equivalent to the above beeline command is:

```bash
beeline -u jdbc:hive2:// -e "set;" > hiveVars.dat
```

which creates the hiveVars.dat file with poorly formatted contents that is very difficult to read.

We are almost done in this lab.

## Part 5 - Working Area Clean-up

1. Enter the following command (don't forget /* at the end of the command!):

```bash
hadoop fs -rm -r -skipTrash /user/cloudera/*
```

You should see a confirmation message on the deleted resources, if any.

2. Enter the following command:

```console
cd /home/cloudera/Works
```



3. Enter the following command:

```bash
pwd
```

You should see the following output:

```console
/home/cloudera/Works
```

4. Enter the following command:

```bash
rm -fr *
```

## Part 6 - Ending the Working Session

1. Type in exit and press Enter to close your terminal window.

This is the last step in this lab.

## Part 7 - Review

In this lab, you learned how to work with the Hive shell in both interactive and batch mode.
