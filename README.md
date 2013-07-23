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


Starting Serivces
-------------------

The JAR file with "-jar-with-dependencies.jar" extension has all of the dependencies pre-packed into itself. You can use it but it's larger in size. If you want to add all dependencies to CLASSPATH yourself, you can use the smaller JAR

Move the JAR file to desired location and run command

```bash
java -jar SeleniumGridExtras-X.X.X-SNAPSHOT-jar-with-dependencies.jar
```

On first run you will be asked the Node's default role (hub|node), what version of WebDriver JAR to use and URL to the Grid Hub node.

After the information is provided, the application will download the desired version of webdriver to "webdriver/(version).jar" and will auto configure version number and url for the grid hub.

Once the service is running, you can hit http://localhost:3000/api for the list of api commands available on a given node.

Starting Grid
=============

To start a Grid Hub on a give computer, hit this URL http://localhost:3000/start_grid?role=hub
To start a Grid Node on a given computer, hit this URL http://localhost:3000/start_grid?role=node
You can check the status of HUB/Node ona given computer by hitting http://localhost:3000/grid_status

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
