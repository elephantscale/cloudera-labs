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
