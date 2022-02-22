# Lab 1 - Learning the Lab Environment

The steps below will help you understand the Lab Environment for this class as well as list some of the repeated tasks that you will be required to do in subsequent labs. You will also, if necessary, refresh your memory on some of the commands and tools commonly used in Unix-like environments.

## Part 1 - Connecting to the Lab Environment

There are a number of ways your class lab environment can be set up. Below is an overview of steps you need to take to be able to start doing your class labs. Those steps are in addition to the steps you are required to perform in order to connect to the cloudbased lab environment. Those steps are sent to you in a separate communication from our System Administration Group and would normally contain an RDP connection file as an attachment as shown below (the file may have a different name or extension).

After connecting to the cloud-based lab environment (using the RDP connection file as per communication from our Sys Admin Group), you may be presented with the following options:

Option 1. You may be directly placed in the Lab Server's Unix desktop environment. In this case, you are done and can start doing class labs inside that environment by starting a Unix terminal window or the browser. You can find short-cuts / icons to start a terminal window and the browser on the desktop. If needed, ask the instructor to show you how to navigate the Unix Desktop.

Note: Skip the steps named "Connect to the Lab Environment" in subsequent labs as those only apply to Option 2 below.

Option 2. If you are placed on the Windows Desktop (instead of direct placement on the Unix Lab Server's desktop), you will find a Putty short-cut there that you need to use to connect to the Unix Lab Server.

Note: Steps below only apply to Option 2 (Windows Desktop initial connection placement) for the Putty SSH client.

These steps will be referred to in subsequent labs as "Connect to the Lab Environment".

1. Double click the Terminal shortcut on your machine's Desktop...

2. The cloudera connection will open.


3 In the subsequent labs, we will be referring to this window as either an SSH window or a terminal window.

After you have successfully logged in, you will be in the cloudera account's home directory. The current directory name (where you are on the system) is printed as part of the system prompt (currently it is /home/cloudera).

## Part 2 - Using the vi Editor

Throughout subsequent labs, you may be required to create new files or edit existing ones. You can use any editor that is installed on the Lab Server and you are familiar with. Below we provide a quick overview of the vi editor.

To editing or create a new file, pass on the file name as an argument to vi.

1. Enter the following command to create a new file foobar:

```bash
vi foobar
```

The vi editor opens displaying the contents of the existing file (that you want to edit) or an empty window for the new file as is the case with the foobar file.

The vi editor supports two modes: edit mode and command mode.

By default, you are in command mode. To switch to edit (insert) mode, you need to press i on the keyboard.

2. Press i on the keyboard.

You should see the INSERT line appearing at the bottom of the editor window.

At this point you can start typing your text.

To switch back to command mode, you need to press Esc followed by :

The colon (:) that appears at the bottom of the screen is the command prompt ready to accept your commands. We will use just the save and exit commands.

Whenever you want to save your file without leaving the editor, you need to switch to command mode and type w at the : command prompt.

Make sure you are in the insert (editing) mode (you should see the -- INSERT -- line at the editor's bottom).

3. Type in some text in the editor window.

4. Press Esc followed by :

You should see the command prompt (:) appearing at the bottom of the screen:

5. Press w at the : command prompt and press Enter.

This command will save changes to your file and will create it if it is a new file.

If you want to save and exit the editor, you need to enter wq at the command prompt (:). You will be referred to this command in subsequent labs as "Save the file and exit the editor".

If you want to exit the editor without saving, you need to enter q! at the command prompt (:).

6. Switch back to the edit (insert) mode by pressing i and type in some more text.

7. Switch to command mode by pressing Esc followed by :

8. Enter wq at command prompt.

9. Press Enter to submit the command.

You should exit the vi editor window.

Note: Opening your file in vi in read-only mode, supply the -R flag, e.g

```bash
vi -R your_file.dat
```

10. Enter the following command:

```bash
rm -f foobar
```

This command will remove the foobar file from the file system without prompting for confirmation of your operation (the -f flag).


## Part 3 - Sundry Commands and Techniques

The big thing to remember is that Unix / Linux commands, file and folder names are case-sensitive, such that directories foo and Foo are different and can peacefully coexist in the same parent folder.

To view the contents of a file use the cat command, e.g. cat myfile.dat

Before you do this, first check the size of the file using this command: ls -lh which can take the name of the file as an argument or issued without any arguments, in which case it will list details of all files and directories in the current directory.

If the file is big, or you would just like browse through it, use the less command that takes the name of the file as an argument , e.g. less your_filename

The less command help you navigate through the file back and forth by using the arrow and PgUp and PgDown keys; when you are done and wish to exist the browsing window, just enter q.

The Linux terminal window supports the folder and file name auto-completion feature which allows you to type the initial part of the directory or file name and complete it by pressing the Tab key.

Use the following short-cuts to quickly navigate along the single command line:


 * Control-a: Moves the cursor to the start of the line.

 * Cntrl-e:Moves the cursor to the end of the line.

 * Cntrl-k: Deletes (kills) the line contents to the right of the cursor.

 * Ctrl-u: Deletes the line contents to the left of cursor.

# Part 4 - Troubleshooting Tips

In some Lab Server deployment scenarios, you may not have certain functionality available preventing you from completing some lab steps.

Here are some tips and workarounds to help you do your classwork.

 * To start the Firefox browser from command line (as the cloudera user) in case the desktop short-cut does not work:

```bash
/usr/local/firefox/firefox &
```

• If you get a message that the Firefox browser is already running, but you do not have a way to bring it to the forefront (to activate it), use these commands to


6 search for the firefox process running and then kill it :

```bash
ps -ef | grep -i firefox
```

The first number after the username (cloudera) is the process id of the dormant firefox process you need to kill, e.g.

cloudera 6328 1 58 07:29 ?  00:00:02 firefox

```bash
kill -9 <firefox pid from ps command above, e.g. 6328>
```

Sometimes, after a long period of inactivity, certain services may lose inter-connectivity with their dependency services. You may see different types of error or warning messages that, essentially, indicate that your environment no longer functions properly. If this is the case, shut down all the services and terminals that you started and issue the reboot command:

```bash
reboot
```
