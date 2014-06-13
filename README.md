Selenium-Grid-Extras
====================

This project is designed to help you manage your Selenium Grid installation by giving you control over the Grid Hub machine and Grid Node machine. This is very useful in cases when Internet Explorer Driver crashes, and you need to kill the iedriver.exe so that next test can start and not fail.


Setup Instructions
------------------

[Compiled Jars](https://github.com/groupon/Selenium-Grid-Extras/releases)


Compile from source:

```bash
git clone https://github.com/groupon/Selenium-Grid-Extras.git
cd Selenium-Grid-Extras
mvn package
```
After all the tests are finished running and all dependencies are downloaded, you will find 2 JAR files in SeleniumGridExtras/target/
* SeleniumGridExtras-X.X.X-SNAPSHOT-jar-with-dependencies.jar
* SeleniumGridExtras-X.X.X-SNAPSHOT.jar


Auto Restarting Nodes
-------------------
Starting with release 1.3.0 the nodes have an option of automatically restarting after a certain amount of builds have been executed and the node is currently not busy. This helps to keep the nodes in pristine state for longer periods of time, and clears up and browser crashes, which may have occurred. Some setup will be needed to make this feature work as intended.

1. Setup default login user
2. Setup default start up task
3. Give permission to access OS.

### Windows ###

1. [Follow Microsoft's Technical Help](http://technet.microsoft.com/en-us/magazine/ee872306.aspx)
2. ...
3. No need to setup permission as long as current user can run the following command in the Terminal
```bash
shutdown -r -t 1 -f
```

### OS X ###

### Linux ###




Starting Services
-------------------

Note: Make sure to run Grid Extras at least once prior to setting it up as a service, so it can ask you the first run questions.

On Windows:

On Linux:

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



On OSx:

Download the com.groupon.SeleniumGridExtras.plist to your computer, open it in editor of choice.

Update the XML file replacing WORKING_DIRECTORY with the location of the selenium grid extras working directory
Update the XML file replacing SELENIUM_GRID_EXTRAS.jar with the name Selenium Grid Extras was saved as

Move the com.groupon.SeleniumGridExtras.plist to ~/Library/LaunchAgents/

run
```
launchctl load ~/Library/LaunchAgents/com.groupon.SeleniumGridExtras.plist
``



Other Useful Commands
=====================
(This is an incomplete list, please hit /api for more detailed breakdown)

/upgrade_webdriver - Downloads a version of WebDriver jar to node, and upgrades the setting to use new version on restart
/move_mouse - Moves the computers mouse to x and y location. (Default 0,0)
/kill_ie - Executes os level kill command on all instance of Internet Explorer
/ps - Gets a list of currently running processes
/kill_pid - Kills a given process id
/netstat - Returns a system call for all ports. Use /port_info to get parsed details
/port_info - Returns parsed information on a PID occupying a given port
/stop_grid - Stops grid or node process
/screenshot - Take a full OS screen Screen Shot of the node


Web GUI
=======

We are currently working on providing a WEB GUI to allow you to get information about the nodes and control them.


Contributing
============

For This project, add functionality, make sure all tests pass, send pull request.

Note: This product exposes your machine to the whole network, anyone on the network will be able to perform OS level task by simply hitting an HTTP url. There are no security measures at the moment, and at the moment no plans to add any security. You have been warned!


Link Backs
==========
This project uses jWMI.java which was taken from www.henryranch.net