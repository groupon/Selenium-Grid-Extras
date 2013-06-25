#!/bin/bash

rm -rf current
mkdir current

rm -rf SeleniumGridExtras/target/*

mvn package

cp SeleniumGridExtras/target/*.jar current/