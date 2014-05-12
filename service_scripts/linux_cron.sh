*/5 * * * * bash -i -c 'cd WORKING_DIRECTORY; export DISPLAY=:1 java -jar SELENIUM_GRID_EXTRAS.jar' >> WORKING_DIRECTORY/log/log.out 2>&1
