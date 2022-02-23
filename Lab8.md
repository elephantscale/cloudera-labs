# Lab 8 - Using Relational Operators in Apache Pig

In this lab, we will learn how to use the JOIN relational operator. One of the practical applications of this operator is finding duplicate entries in two or more data sets or joining related information for data enrichment and report generation.

## Part 1 - Connect to the Lab Environment

Note: Skip this Lab Part if you are using the Lab Server's desktop environment setup.

Using your SSH client, connect to the Lab Environment. Use cloudera/cloudera credentials when prompted.

## Part 2 - Set up the Workspace

1. In the terminal window, change directory to Works by typing the following command:

```bash
cd Works
```

2. Enter the following commands to copy over the input files:

```bash
cp ~/LabFiles/clients.dat .

cp ~/LabFiles/transactions.dat .
````

Review the contents of both files. We will perform a JOIN operation on these files by the client id listed in both files which is the second field in the transactions.dat file and the first field in clients.dat (the client ids are: 123, 234, 345,456,567,678, and 890).

## Part 3 - Run Grunt (Pig Command Shell)

1. Enter the following command to start Grunt in local mode:

```bash
pig -x local
```

You should get the Grunt shell prompt.

```console
grunt>
```

Note 1: Grunt supports auto-completion of keywords, operator and variable names with the Tab key pressed on the partially entered names.

Note 2: Don't forget the closing ; (semi-colon) after each Pig Latin statement.



 2. Enter the following command at Grunt prompt on one line:

```pig
txns = LOAD 'transactions.dat' USING PigStorage (';') AS (txnid:int, clientid:int,txnamount:long);
```

3. Enter the following command:

```pig
DUMP txns;
```

The DUMP command will print on console the contents of the transactions.log file as aliased by the txns variable.


```console
(1,123,1000) (2,234,15000) (3,345,-90) (4,456,-1200) (5,678,4500) (6,789,-34500) (7,890,9000)
```

4. Enter the following command (the fields in clients.dat are Tab delimited):

```pig
clients = LOAD 'clients.dat' AS (cid:long, fullname: chararray);
```

5. Dump the clients relation:

```pig
Dump clients;
```

You should see the following output:

```console
(123,James Roberts) (234,John McDonald) (345,Fritz Schlecht) (456,Li Wang) (567,Agatha Steeles) (678,Alex Archambault) (789,Chris Rousse) (890,Roy Tanguay)
```

Now we are ready to JOIN the clients and txns relations.

6. Enter the following command:

```pig
client_txn = JOIN txns BY clientid, clients BY cid;
```



7. Perform:

```pig
dump client_txn;
```

You should see the following output

```console
(1,123,1000,123,James Roberts) 
(2,234,15000,234,John McDonald) 
(3,345,-90,345,Fritz Schlecht) 
(4,456,-1200,456,Li Wang) 
(5,678,4500,678,Alex Archambault) 
(6,789,-34500,789,Chris Rousse) 
(7,890,9000,890,Roy Tanguay)
```

As you can see, we have duplication of client ids in fields 2 and 4.

Let's say we need to produce a report that should only list the client name and the amount of their transaction.

Here is how we can do it: we will generate a new relation from client_txn by applying the needed transformations of dropping the unneeded fields and arranging the fields in the proper order of the client name appearing first followed by the amount of their transaction.

8. Enter the following command:

```pig
report = FOREACH client_txn GENERATE $4, $2;
```

9. Enter:

```pig
dump report;
```

The dump of report should look as follows:

```console
(James Roberts,1000) 
(John McDonald,15000) 
(Fritz Schlecht,-90) 
(Li Wang,-1200) 
(Alex Archambault,4500) 
(Chris Rousse,-34500) 
(Roy Tanguay,9000)
```

As a final touch to our award winning report program, we want to order the listing by the amount of transaction showing the largest amounts on top.

No problem.



10. Enter the following command:

```pig
sorted_by_amount = ORDER report BY $1 DESC;
```

11. Enter:

```pig
dump sorted_by_amount;
```

The dump operation will show that the task has been accomplished with flying colors and with minimum effort on our part.

```console
(John McDonald,15000) 
(Roy Tanguay,9000) 
(Alex Archambault,4500)
(James Roberts,1000) 
(Fritz Schlecht,-90) 
(Li Wang,-1200) 
(Chris Rousse,-34500)
```

Note: The JOIN operator supports relational operations on more than two data sets. The syntax is very straightforward, e.g. for triple join of R1, R2 and R3 on their respective joining fields FR1, FR2, and FR3, you would need to write this statement:

```pig
x = JOIN R1 BY FR1, R2 BY FR2, R3 BY FR3;
```

That's all there is to this lab.

12. Type in quit and press Enter to close the Grunt command shell.

You should be returned to the system command prompt.

## Part 4 - Working Area Clean-up

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



64 3. Enter the following command:

```bash
rm -fr *
```

## Part 5 - Ending the Working Session

1. Type in exit and press Enter to close your terminal window. This is the last step in this lab.

## Part 6 - Review

In this lab, we worked with the JOIN relational operator supported by Pig.
