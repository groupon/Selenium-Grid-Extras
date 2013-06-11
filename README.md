Selenium-Grid-Extras
====================

This project is designed to assist with the management of Selenium Grid Node farm. It provides an API end point for tests to programmatically control certain aspects of the node.

Getting started

1. scripts/start_extras_service.sh - Starts the instance of the Grid Extras locally

2. scripts/start_grid.sh - Starts an instance of the selenium Grid locally

3. scripts/start_grid_node.sh - Starts and attaches a node to local grid
Note: scripts/start_grid_node.sh is hard coded to http://localhost:4444, modify the HUB_HOST variable to fit your needs



TODO:
* Video recording of the tests
* Tighter integration with Grid servletes
* VM spin up / spin-down
* Make the Selenium-Grid-Extras launch the Selenium Node Server so there is only 1 server to start for user
* Give ability for the node to send files to some network location (Video recordings, etc...)
* Give ability for the node to receive files from the test to be used in the test, such as uploading



