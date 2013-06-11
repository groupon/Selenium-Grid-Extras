Selenium-Grid-Extras
====================

This project is designed to assist with the management of Selenium Grid Node farm. It provides an API end point for tests to programmatically control certain aspects of the node.

Getting started

1. scripts/start_extras_service.sh - Starts the instance of the Grid Extras locally

2. scripts/start_grid.sh - Starts an instance of the selenium Grid locally

3. scripts/start_grid_node.sh - Starts and attaches a node to local grid
Note: scripts/start_grid_node.sh is hard coded to http://localhost:4444, modify the HUB_HOST variable to fit your needs

4. Node's api should be here http://localhost:3000/api

5. Grid integration will be here http://localhost:4444/grid/admin/SeleniumGridExtrasServlet


Configuring
-----------

All of the configurations are stored in selenium_grid_extras_config.json file.

* activated_modules - A list of modules which will be loaded into memory at run time
* deactivated_modules - List of modules that will not be loaded
* setup - List of modules to execute before each test starts to run
* teardown - List of modules to execute after the tests are complete


Adding new Modules
------------------
1. Create class which extends ExecuteOSTask

``` public class GetProcesses extends ExecuteOSTask{}

2. Register a new api endpoint for other people to be able to reach you and a short description of what module does
```
    @Override
    public String getEndpoint() {
        return "/ps";
    }

    @Override
    public String getDescription() {
      return "Gets a list of currently running processes";
    }
```

3. Document what your end point will return on success and failure
```
@Override
  public Map getResponseDescription() {
    Map response = new HashMap();
    response.put("exit_code",
                 "0 for success, 1 for failure");
    response.put("standard_out", "Array of PIDS and descriptions");
    response.put("standard_error", "Error recived on failure");
    return response;
  }
```

4. If you have a CLI command that will execute your task, set them up
```
  @Override
  public String getWindowsCommand() {
    return "tasklist";
  }

  @Override
  public String getLinuxCommand() {
    return "ps x";
  }
```
Note: getMacCommand() by default points to getLinuxCommand(), you should use it if some CLI command is just slightly different on Mac than on Linux
Example: GetProcess.java


5. If you want more freedom than just CLI commands, overwrite the execute() method
```
@Override
  public String execute() {
    return doStuff();
  }
```
Example: MoveMouse.java



TODO:
* Add ability to add custom items to RuntimeConfig from extension points
* Implement Setup/Teardown modules
* Video recording of the tests
* Tighter integration with Grid servletes
* VM spin up / spin-down
* Make the Selenium-Grid-Extras launch the Selenium Node Server so there is only 1 server to start for user
* Give ability for the node to send files to some network location (Video recordings, etc...)
* Give ability for the node to receive files from the test to be used in the test, such as uploading



