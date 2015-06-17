Selenium-Grid-Extras
====================
![](https://github.com/groupon/Selenium-Grid-Extras/blob/master/resources/node_view_screenshot.png)

Selenium Grid Extras is a project that helps you set up and manage your local Selenium Grid. Typical instances of the Grid consists of the HUB and Nodes. The HUB is responsible for managing test sessions and assigning new session idle nodes.

Setup of Grid Extras is made to be simple, just run the packaged JAR file and point the node at the HUB location, Grid Extras will take care of the rest for you.
Here are the features you get by using Selenium Grid Extras vs Selenium Grid alone:
* Ability to control individual grid nodes, following is an incomplete list of OS level tasks:
  * Killing any browser instance by name
  * Stopping any Process by PID
  * Moving mouse to specific location
  * Get Memory usage and disk statistics
* Automatically upgrade WebDriver binaries
* Restart node after a set number of test executions
* Central storage of configurations for all nodes on the HUB server
* Screenshots at the OS level
* And much more.


Running
-------
1. On the initial execution, follow the "Setup Instructions" section.
2. As part of the initial execution, a starter script start_grid_extras.sh or start_grid_extras.bat will be generated.
3. After the initial execution, use the generated start script to start HUB or nodes thereafter.
    * This will help with automatic upgrades and downgrades



Setup Instructions
------------------

Setup is simple, just download the Grid Extras Jar to get started from here: [Download Latest](https://github.com/groupon/Selenium-Grid-Extras/releases)


### Setting up Grid Hub ###

1. In the terminal run following command:
```bash
java -jar Selenium-Grid-Extras-Jar.jar
```

2. You will be prompted with several questions, first one will ask you if you want to set this computer as a HUB, Node, or both. Answer 2 for HUB

3. Leave the Host name for Grid Hub as default 127.0.0.1

4. Set port to be used by Selenium Grid Hub, default is 4444

5. You will be asked if you wish to auto update Selenium. If you answer yes, then every time Selenium Grid Extras is started it will check fo the latest version of Selenium Stand Alone Server, IEDriver, and ChromeDriver. If you choose to not auto update, you will be asked what versions of each driver to lock into.


### Setting up Grid Node ###

1. In the terminal run following command:
```bash
java -jar Selenium-Grid-Extras-Jar.jar
```

2. You will be asked if you wish to use this computer as HUB or Node, select 1 for Node

3. You will be asked for the host name of the HUB computer, type in the IP or hostname of the HUB computer

4. When prompted for the port used by the HUB, enter that value

5. Selenium Grid Extras will attempt to guess the Operating System of the current computer, if it's wrong please enter the correct value

6. You will be asked what Browsers this Node will host, choose the ones that apply

7. You will be asked how often to restart your whole computer. By default after 10 tests Selenium Grid Extras will attempt to restart the Node, provided the node is idle. Choose 0 if you do not wish to have the computer automatically restart.

8. You will be asked if Selenium Grid Extras should automatically check for updates of IEDriver, ChromeDriver and Selenium Stand Alone Server. If you answer no, you will be asked what version to lock into.

9. Finally, you will be asked if you wish to store all of the Node configs on the HUB. If you answer yes, Selenium Grid Extras will attempt to push Node's configs to the HUB. If it is successful, Selenium Grid Extras will attempt to download all of the configs from the HUB before it starts. This way all of the Node configs can be controlled directly from the HUB.


Upgrading Grid Extras
---------------------
There are 2 options available for un-attended upgrades

1. Automatic upgrades can be achieved by selected "auto update" on the first run, or setting "grid_extras_auto_update" key to have value of "1" in selenium_grid_extras_config.json
2. Manual upgrade trigger can be achieved by making an HTTP GET request against http://node_name:3000/upgrade_grid_extras?version=X.X.X

Auto Restarting Nodes
-------------------
Starting with release 1.3.0 the nodes have an option of automatically restarting after a certain amount of builds have been executed and the node is currently not busy. This helps to keep the nodes in pristine state for longer periods of time, and clears up and browser crashes, which may have occurred. Some setup will be needed to make this feature work as intended.

1. Setup default login user
2. Setup default start up task
3. Give permission to access OS.

### Windows ###

1. [Follow Microsoft's Technical Help](http://technet.microsoft.com/en-us/magazine/ee872306.aspx)
2. Add start up batch script to StartUp directory or set up a Scheduled Task to start the batch file automatically
3. No need to setup permission as long as current user can run the following command in the Terminal
```bash
shutdown -r -t 1 -f
```

### OS X ###

1. Turn on Automatic Login option and make the desired user auto login
2. Set up the shell script which starts Grid Extras to run automatically.
3. Modify the /etc/sudoers to contain this line, where the $USER is the current user that will be used for running grid node.
```bash
$USER ALL=(ALL) NOPASSWD: /sbin/shutdown
```


### Linux ###

1. ...
2. Set up the shell script which starts Grid Extras to run automatically.
3. Modify the /etc/sudoers to contain this line, where the $USER is the current user that will be used for running grid node.
```bash
$USER ALL=(ALL) NOPASSWD: /sbin/shutdown
```



Starting Services
-------------------

Note: Make sure to run Grid Extras at least once prior to setting it up as a service, so it can ask you the first run questions.

### Windows ###

There are two major ways to make windows automatically start the Grid Extras binary

* Insert a batch file into StartUp directory
* Using the Task Scheduler to add the java start command, where the executable will be the path to java.exe and the path to Grid Extras passed in as an argument

### Linux ###

There are a lot of security issues with setting up a cron job as a “build user” and letting that user run in the normal display desktop (DISPLAY=:0 aka the one you see when it is connected to the computer monitor). There is a work around to allow the service to run in DISPLAY=:0 but that’s not recommended.

Instead, it is a much better practice to set up a XVNC server on a Linux computer, with a light desktop manager (FluxBox seems to be a good lightweight choice http://fluxbox.org/). Once VNC server and desktop managers are installed, run the following command to start a virtual DISPLAY:

```
vncserver :1 -geometry 1024x768
```

This will start an XVNC server on DISPLAY=:1 with screen resolution of 1024x768. You can tweak these parameters as needed.
Note: You might need to add a cron job to restart vncserver in similar fashion, since vncserver will not automatically start after reboot


After you have the virtual display running, add run this command to edit the cron list for current user (vi is the editor used)

```
crontab -e
```

Add following line to the cron list:

```
*/5 * * * * bash -i -c 'cd WORKING_DIRECTORY; export DISPLAY=:1 java -jar SELENIUM_GRID_EXTRAS.jar' >> WORKING_DIRECTORY/log/log.out 2>&1
```

Where the WORKING_DIRECTORY needs to be replaced with the location where grid extras jar was downloaded, and SELENIUM_GRID_EXTRAS represents the name given to the grid extras jar.
This cron will run every 5 minutes.



### OS X ###

Download the [SeleniumGridExtras.plist](https://github.com/groupon/Selenium-Grid-Extras/blob/master/service_scripts/com.groupon.SeleniumGridExtras.plist) to your computer, open it in editor of choice.

Update the XML file replacing WORKING_DIRECTORY with the location of the selenium grid extras working directory
Update the XML file replacing SELENIUM_GRID_EXTRAS.jar with the name Selenium Grid Extras was saved as

Move the com.groupon.SeleniumGridExtras.plist to ~/Library/LaunchAgents/

run
```
launchctl load ~/Library/LaunchAgents/com.groupon.SeleniumGridExtras.plist
```






Contributing
============

For This project, add functionality, make sure all tests pass, send pull request.

Note: This product exposes your machine to the whole network, anyone on the network will be able to perform OS level task by simply hitting an HTTP url. There are no security measures at the moment, and at the moment no plans to add any security. You have been warned!


Link Backs
==========
This project uses jWMI.java which was taken from www.henryranch.net
