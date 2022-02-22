# Lab 6 - Apache Pig HDFS Command-Line Interface

In this lab, we will learn how to use the HDFS command-line interface in Apache Pig. We will see how to use the FSShell shell commands as well as deprecated (but still available and in use) direct HDFS-related commands.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 2 - Create a Data Set

1. In the terminal window, change directory to Works by typing the following command:

```bash
cd Works
```

Note: The terminal window supports the folder and file name auto-completion feature which allows you to type the initial part of the directory or file name (e.g. W) and complete it by pressing the Tab key.

In this lab, we will work with a file containing the directory listing of the /usr/bin folder.

2. Enter the following command:

```bash
ls -l /usr/bin > dir.dat
```

This command creates the file that we will be using in our lab.

3. Enter the following command:

```bash
head dir.dat
```

You should see the first lines of our data set:

```console
total 116272

-rwxr-xr-x

-rwxr-xr-x

-rwxr-xr-x

-rwxr-xr-x . . .

1 root root 1 root root 1 root root 1 root root

37000 Jun 22 112200 Feb 21 13896 Feb 22 12312 Feb 22

2012 [ 2013 a2p 2013 abrt-action-analyze-backtrace 2013 abrt-action-analyze-c
```



## Part 3 - Run Grunt (Pig Command Shell)

1. Enter the following command to start Grunt:

```console
pig
```

You should get the Grunt shell prompt.

```console
grunt>
```

Note 1: Grunt supports auto-completion of keywords, operator and variable names with the Tab key pressed on the partially entered names.

Note 2: Don't forget the closing ; (semi-colon) after each Pig Latin statement.

All the subsequent commands will be executed at the grunt> prompt.

## Part 4 - Pig HDFS Interface

The command-line interface to HDFS is provided through the fs command which returns a reference to the FSShell shell.

1. Enter the following command:

```console
fs -put dir.dat filesDS
```

This command will copy the contents of the dir.dat file to HDFS under the name filesDS.

## Part 5 - Using Old HDFS Interface Commands

As of Pig version 0.6, the file commands that will be covered below have been deprecated and will be removed in future. You should use the FSShell shell commands instead.

However, the old (deprecated) commands are still available and you can use them. The Apache Pig file commands have a more compact syntax than those used in FSShell and this can aid in your command typing productivity. For this reason, we will briefly touch on some of the most frequently used file commands.

1. Enter the following command at Grunt prompt:

```console
pwd
```



51 You should get the name of the HDFS working directory (this command behaves similar to the pwd Unix command).

hdfs://<HDFS Name Node Server>:8020/user/cloudera

Note: If you ran pwd in Grunt launched in local mode (pig -x local), you would have a different output of this command:

file:/<your working directory>

2. Enter the following command:

```bash
ls
````

This command (functionally similar to the ls Unix command) prints the file and directory listing of the user home directory on HDFS.

hdfs://<HDFS Name Node Server>:8020/user/cloudera/filesDS<r 1>81722

Note: The <r 1> part of the listing item indicates the replication factor of 1 currently configured in HDFS.

By the way, you can get the listing of the user home directory on HDFS using the fs command as follows:

```console
fs -ls
```

Which produces a different from ls output.

Now, we can easily print to console the contents of any file on the HDFS system by using the cat command.

Note: The cat command does not have any output configuration parameters that would limit the number of lines in the system output, so use this command with caution as it would simply dump all the file contents to console.

3. Enter the following command:

```console
cat filesDS
```

This command, as expected, prints the contents of the filesDS file located on HDFS.

The old file commands also allow you to load files from the (conventional) file system onto HDFS as well as get a file from HDFS back to the file system. However, you should use the FSShell shell for these commands.



The other file commands that you can use directly from Grunt's prompt are: cd (change directory), cp (copy files or directory within HDFS), mkdir (create a new directory), etc.

## Part 6 - Using FSShell

While the old Pig HDFS-related commands are still available and you can use them, you are advised to start using the FSShell shell that you can invoke from inside Grunt using the fs command.

1. Enter the following command at Grunt's prompt:

```console
fs -cat filesDS
```

This command invokes the FSShell shell and produces output similar to that of the cat command we used in the previous part: cat filesDS.

As you see, interface to the FSShell shell is quite simple: you just need to type in the fs command which launches FSShell in the background and submits commands passed as parameters to fs. Commands are prefixed with the '-' (dash) sign.

Note: You don't need to end FSShell commands with a ';' (semi-column) as they are not Pig Latin statements.

2. Enter the following command:

```console
fs -help
```

You should get the help on the FSShell shell commands.

Note: Ignore the "Usage: hadoop fs [generic options]" line at the top of the output (you just need to drop hadoop in your commands and use fs directly to get a reference to the FSShell shell).

As you can see, the FSShell shell offers many more commands compared to those available through the deprecated (old) file command list.

Let's get the filesDS file from HDFS back to the conventional file system using the get (or copyToLocal) command of FSShell. In order to prevent the overwriting of the original files.dat file, we will name the file myFileFromHDFS.dat.

3. Enter the following command at Grunt's prompt:

```console
fs -get filesDS myFileFromHDFS.dat
```

We are going to check if the myFileFromHDFS.dat file was indeed created by using the built-in sh command that acts as an interface with the system shell.



4. Enter the following command:

```console
sh ls -l
```

You should see the following output (abridged for space):

```console
-rw-rw-r-- 1 cloudera cloudera 104745 <your time stamp> dir.dat

-rwxr-xr-x 1 cloudera cloudera 104745 <your time stamp> myFileFromHDFS.dat
```

5. Enter the following command:

```console
fs -tail filesDS
```

This command will print the last 1KB of the filesDS file. This command is helpful when you need to get an idea of a file's content and its structure. Unfortunately, there is no matching head command.

Note: When you delete files from HDFS using FSShell, you may want to consider adding the -skipTrash command flag to prevent the file from going into HDFS's "Recycle Bin" and taking up physical disk space:

```console
fs -rm -skipTrash fileToDelete
```

This command option only exists in FSShell (and not in the old file command list).

We are done working with FSShell, and are about to finish working on this lab.

6. Enter the following command (don't forget /* at the end of the command!):

```console
fs -rm -r -skipTrash /user/cloudera/*
```

You should see the following output:

```console
Deleted /user/cloudera/filesDS
```

7. Type in quit and press Enter to close the Grunt command shell.

You should be returned to the system command prompt.

## Part 7 - Working Area Clean-up

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

## Part 8 - Ending the Working Session

1. Type in exit and press Enter to close your terminal window.

This is the last step in this lab.

## Part 9 - Review

In this lab, we familiarized ourselves with some Pig's old HDFS-related commands as well as those supported by the FSShell shell.
