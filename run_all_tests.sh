#!/bin/bash -xl

curl http://localhost:3000/stop_extras?confirm=true

set -e

rm -rf tmp
mkdir tmp

mvn package
java -jar SeleniumGridExtras/target/SeleniumGridExtras-1.1.8-SNAPSHOT-jar-with-dependencies.jar > tmp/grid_log.txt &

rvm gemset use grid_extras --create

gem install rspec json

rspec SeleniumGridExtras/src/test/specs/

curl http://localhost:3000/stop_extras?confirm=true