# Lab 2 - The Hadoop Distributed File System

In this lab, we will learn how to work with the Hadoop Distributed File System (HDFS).

The HDFS is the main component of Hadoop which offers developers both Application Programming Interface (API) and the command-line interface (CLI) to interact with it. We will look at the HDFS CLI where commands that we will be using are applicable in the context of a single data node setup or a large Hadoop cluster.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 2 - The Lab Working Directory

All the steps in this lab will be performed in the /home/cloudera/Works directory.

1. In the terminal window, type in the following command:

cd ~/Works

## Part 3 - Getting Started with HDFS

All interactions using the HDFS CLI in this lab will be done via the File System (FS) shell that is invoked by this command: hadoop fs

Note 1: You can also interface with HDFS using the hdfs admin tool using this command: hdfs dfs Note 2: The old (and now deprecated) reference to the FS shell is hadoop dfs

Most of the commands in the FS shell have syntax and functionality similar to those of corresponding Unix File System commands.

1. Enter the following command:

```bash
hadoop fs
```



8 You should see a list of supported commands that you can submit through the HDFS CLI (the list below is abridged for space.)


The critical point to notice here is that all commands must be prefixed with the dash '-', e.g. to get a directory listing, the following command needs to be issued hadoop fs -ls

You can get the bulk help on these commands by entering the following command:

```bash
hadoop fs -help
```

To get help on a specific command, type the name of that command after -help; for example, to get help on the rm (remove file or directory) command, type this command:

```bash
hadoop fs -help rm
```

2. Enter the following command:

```bash
hadoop fs -ls
```



9 You should get no output on the console. By default, the ls command outputs the listing of your current working directory in HDFS and you have not uploaded any files there yet. Let's see how HDFS is structured on your system.

3. Enter the following command:

```bash
hadoop fs -ls /
```

You should see the following output of the top folder of HDFS (the timestamps and folder listing may differ in your case).

Found 5 items drwxr-xr-x drwxr-xr-x drwxrwxrwx drwxr-xr-x drwxr-xr-x

- hbase supergroup

- solr solr

- hdfs supergroup

- hdfs supergroup

- hdfs supergroup

0 2015-06-24 08:25 /hbase 0 2015-04-23 07:49 /solr 0 2015-06-23 18:46 /tmp 0 2015-06-23 18:46 /user 0 2015-04-23 07:47 /var

Note: These folders do not exist on the standard (local) File System even though names may be the same, e.g. /tmp 

4. Enter the following command:

```bash
whoami
```

You should see that you are the cloudera system user (the account you used to log into the system).

User-specific files and directories are stored under their account-specific directories under the /user directory.

5. Enter the following command:

```bash
hadoop fs -ls /user
```



10 You should see the following output (some details in your output may differ):

Found 6 items drwxr-xr-x drwxr-xr-x drwxr-xr-x drwxrwxrwx drwxrwxrwx drwxr-xr-x

- cloudera cloudera

- hdfs supergroup

- mapred hadoop

- hive hive

- oozie oozie

- spark spark

0 2015-06-24 11:41 /user/cloudera 0 2015-06-23 18:46 /user/hdfs 0 2015-04-23 07:48 /user/history 0 2015-04-23 07:48 /user/hive 0 2015-04-23 07:49 /user/oozie 0 2015-04-23 07:49 /user/spark

You are a legitimate tenant at the /user/cloudera location on HDFS, which is your home directory.

You may also change to directories of other users (mapred, hive, etc.) as long as those directories have the x flag set for other users (the last character in the directory permission list), e.g. drwxr-xr-x Note: The HDFS closely follows the Unix File System listing layout.

6. Enter the following command:

```bash
hadoop fs -ls /user/cloudera
```

This command outputs nothing and is functionally equivalent to the hadoop fs -ls command we issued before (and with the same result).

## Part 4 - Putting Files on HDFS

You put files on HDFS by using one of the two commands: copyFromLocal or put. For brevity, we will use put.

You can get help on the put command by issuing this command: hadoop fs -help put Let's create a file that we are going to copy from the local File System over to HDFS. 

1. Enter the following command:

```bash
ls -l /usr/bin > usr_bin_dir.dat
```

This command will capture the listing of the `/usr/bin` folder in the `usr_bin_dir.dat` file in our working directory.

2. Enter the following command:

```bash
ls -lh
```



11 You should get the output similar to the one below:

```console
total 80K -rw-rw-r-- 1 cloudera cloudera 80K Jun 24 11:46 usr_bin_dir.dat
```


The file is there all right, size a bit too small for Hadoop, but it is OK for our purposes.

3. Enter the following command:

```bash
hadoop fs -put usr_bin_dir.dat
```

This is the simplest possible command that copies the usr_bin_dir.dat file from local file system to HDFS where it would be assigned the same usr_bin_dir.dat name. Note, that the file on the local system stays untouched.

4. Enter the following command:

```bash
hadoop fs -ls
```

You should see that your file has been successfully copied over to HDFS.

Found 1 items -rw-r--r-- 1 cloudera cloudera

81722 2015-06-24 11:47 usr_bin_dir.dat

The number 1 in front of the cloudera account name is the number of replicated copies of your file. Normally, in a production setup, you will see the number 3 instead of 1. In our labs, we are running in pseudo-distributed deployment mode which maintains only one replica of every file block on HDFS.

Note: HDFS protects you from accidentally overwriting existing file with the same name and location, so if you run the above command again: hadoop fs -put usr_bin_dir.dat, you would be presented with this message: put: `usr_bin_dir.dat': File exists

OK. Now, what if you want to have a different (e.g. a shorter or a fancier) name of the target file in HDFS. For this, you need to specify this name in the put command.

5. Enter the following command:

```bash
hadoop fs -put usr_bin_dir.dat files
```

Now, if you issue the hadoop fs -ls command, you will see the following output:

```console
Found 2 items

-rw-r--r-- 1 cloudera cloudera

-rw-r--r-- 1 cloudera cloudera

81722 2015-06-24 11:54 files 81722 2015-06-24 11:47 usr_bin_dir.dat
```



The `usr_bin_dir.dat` file was persisted on HDFS as files.

Now let's see how to get file(s) back from HDFS to the local File System. This should be a two-way street, right?

## Part 5 - Getting Files from HDFS

You can get file(s) from HDFS by using either command: copyToLocal or get which are functionally equivalent. We will use the shorter version, get.

1. Enter the following command:

```bash
hadoop fs -get files
```

You should get the files HDFS file (which is the replica of the usr_bin_dir.dat file). 

2. Enter the following command:

```bash
diff files
```

usr_bin_dir.dat

You should get no differences between the files (no output on your console).

## Part 6 - Creating Folders

It is always a good idea to have a way to partition your file system into separate folders. The HDFS design is no exception to this rule with full support for this file organizational task.

1. Enter the following command:

```bash
hadoop fs -mkdir REPORT
```

This command will create the REPORT folder under /user/cloudera/.

Now, let's see how you can put multiple files from the local File System to HDFS. 

2. Enter the following command:

```bash
hadoop fs -put usr_bin_dir.dat files REPORT
```



This command will copy usr_bin_dir.dat and files files to the /user/cloudera/REPORT directory. You can verify the creation of the files using either command:

```bash
hadoop fs -ls /user/cloudera/REPORT
```

or

```bash
hadoop fs -ls REPORT
```

## Part 7 - Moving Files Around

You move files around the HDFS system using the mv command. When you are moving multiple files, the destination argument to the mv command must be a directory.

Note: To get help on this command, run: hadoop fs -help mv

1. Enter the following command:

```bash
hadoop fs -mv REPORT/files files.moved
```

This command will move the files file from the REPORT directory back to the default cloudera's directory with the files.moved name.

Now if you run the hadoop fs -ls REPORT command, you will see that the files file was, indeed, removed from the source location.

2. Enter the following command to see the results of your activities on HDFS so far:


```bash

hadoop fs -ls -R
```

## Part 8 - Deleting (Removing) Files

You delete files / directories with the rm command. The syntax is quite simple:

```bash
hadoop fs -rm <file to delete>
```

You should be aware of one caveat when using the rm command: depending on your HDFS configuration, your file could actually be retained and put in the <user home dir>/.Trash folder that would be conveniently created by HDFS if it was not created before. Having the file around stashed in the Trash bin may be a good idea in case you may still need it in future. On the other hand, it may be a bad thing in case you know you will never need this file again as having the file (particularly a really big one) around consumes resources on the Name Node and disk space on the Data Node(s).

Let's delete a file and see what happens.

1. Enter the following command:

```bash
hadoop fs -rm files.moved
```

You will get a confirmation message to this effect:

```console
15/06/24 12:04:05 INFO fs.TrashPolicyDefault: Namenode trash configuration: Deletion interval = 0 minutes, Emptier interval = 0 minutes.

Deleted files.moved
```

Translation: This message is to the effect that the deleted files retention feature of HDFS is not enabled and the /user/cloudera/.Trash folder is not created; your file is deleted for good (it cannot be restored).

It is a good idea to always to assume that the file trashing feature is enabled. Just get into the habit of using the -skipTrash flag to the rm command which will physically remove the file from HDFS if the trashing feature is enabled and will do nothing if the trashing feature is not enabled (the file will be removed automatically).

Here is how you run this command.

2. Enter the following command:

```bash
hadoop fs -rm -skipTrash usr_bin_dir.dat
```

This command will completely remove the usr_bin_dir.dat file from HDFS whether or not the trashing feature is enabled.

Note: The HDFS's Trash facility is designed with enterprise use cases in mind. You can specify the minimum period (in minutes) that a deleted file will remain in the .Trash folder using the fs.trash.interval configuration property in core-site.xml. By default, fs.trash.interval is zero, which disables trash.

The Trash facility is available through the HDFS FS shell; if files are deleted programmatically (using the HDFS FS API) they are removed immediately.

The HDFS FS shell also lets you expunge deleted files from the .Trash folder that have been there longer than the prescribed retention period; the command for this is:

```bash
$ hadoop fs -expunge
```

## Part 9 - Navigating HDFS with the Web Browser

Note: Depending on your lab environment setup, you may not have a desktop environment enabled on your Lab Server in which case the lab steps in this lab part will not work; if this is the case, proceed to the next lab part.



In the desktop lab environment setup option, you may be given access to the Lab Server's desktop with the Firefox browser installed, in which case you can access the HDFS Name Node web UI served from its embedded web server.

1. Start Firefox (there should be a desktop short-cut or an icon on the toolbar), if it is not yet running, and navigate to:

http://127.0.0.1:50070

2. Click Utilities → Browse the file system link on the Name Node page.

3. Navigate to /user/cloudera You should be able to see the contents of your home directory on HDFS.

Take some time to familiarize yourself with the HDFS file system as reported by Name Node web UI.

4. When you are done, close the browser.

Part 10 - Working Area Clean-up

We are not going to use any of the artifacts we created in this lab in future labs, so it is a good idea to delete those unneeded resources.

1. Switch back to the terminal window.

2. Enter the following command:

```bash
pwd
```

You should see that you are in the `/home/cloudera/Works` directory. If you are not, change to the `/home/cloudera/Works` directory.



3. Enter the following command to delete all files in the working directory:

```bash
rm -f *
```

Note, you may see a folder there, if so, delete the folder using the following command:

```bash
sudo rm -rf NAME_OF_FOLDER
```

4. Enter the following command:

```bash
ls
```

There should be no files left in the working folder.

Now the same work needs to be done on HDFS.

5. Enter the following command (don't forget /* at the end of the command!):

```bash
hadoop fs -rm -r -skipTrash /user/cloudera/*
```

You should see a confirmation message on the deleted resources. __6. Enter the following command:

```bash
hadoop fs -ls /user/cloudera
```

The command should return no results.

## Part 11 - Ending the Working Session

1. Type in exit and press Enter to close the terminal window. This is the last step in this lab.

## Part 12 - Review

In this lab, we learned how to work with Hadoop Distributed File System (HDFS) using its command-line interface represented by the HDFS File System shell. We also looked at how to navigate through HDFS using your local browser.
