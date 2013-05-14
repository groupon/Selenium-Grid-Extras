Selenium-Grid-Extras
====================

 > Copyright (c) 2013, Groupon, Inc.
 > All rights reserved.
 >
 > Redistribution and use in source and binary forms, with or without
 > modification, are permitted provided that the following conditions
 > are met:
 >
 > Redistributions of source code must retain the above copyright notice,
 > this list of conditions and the following disclaimer.
 >
 > Redistributions in binary form must reproduce the above copyright
 > notice, this list of conditions and the following disclaimer in the
 > documentation and/or other materials provided with the distribution.
 >
 > Neither the name of GROUPON nor the names of its contributors may be
 > used to endorse or promote products derived from this software without
 > specific prior written permission.
 >
 > THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 > IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 > TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 > PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 > HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 > SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 > TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 > PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 > LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 > NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 > SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 > Created with IntelliJ IDEA.
 > User: Dima Kovalenko (@dimacus) && Darko Marinov
 > Date: 5/10/13
 > Time: 4:06 PM


This project is to help manage the windows boxes in the Selenium Grid, providing greater test stability.

The problem with running grid nodes on windows boxes is that Internet Explorer does not always close properly at the end of the session.
This leads to instability on the test node, as the computer runs out of memory.

This set of plugins tries to solve this issue by starting a small JAVA server on the node machine, which allows execution of arbitrary code.
We also tie into the Selenium Grid's "PROXY" extension point, to send "setup" and "teardown" commands before each session starts and after session finishes.

We plan to expand this tool to give more choices for the user to turn on and off, such as moving the mouse out of the window, recording videos, and restart selenium node server after each build, and restarting the node only after X many builds.

Getting started

1. Start Selenium Grid with the new proxy attached
`java -cp selenium-server.jar:selenium-grid-extras-0.0.1.jar org.openqa.grid.selenium.GridLauncher -role hub`
> Note: selenium-server.jar refers to the path to the latest selenium stand alone server
>
> Note: This is bash style class path, if you are on Windows computer, you will need to use "selenium-server.jar;selenium-grid-extras-0.0.1.jar"

2. Start the Selenium Node
`java -cp selenium-server.jar:selenium-grid-extras-0.0.1.jar org.openqa.grid.selenium.GridLauncher -proxy com.groupon.SeleniumGridExtrasProxy -role node -hub http://localhost:4444`

3. Start the Selenium-Grid-Extras server
`java -cp selenium-grid-extras-0.0.1.jar com.groupon.SeleniumGridExtras`

The server will start on port 3000 and will have 2 endpoints
* /setup     - Will kill all instances of Internet Explorer
* /teardown  - Will restart the node

> Note: Assumption is that the Selenium node will have a batch file in start up directory that will restart the Selenium Node server and Selenium-Grid-Extras Server


TODO:
* Make the Selenium-Grid-Extras launch the Selenium Node Server so there is only 1 server to start for user
* Split different task into modules
* Make an API for SE-Grid-Extras to allow users to control the node more in detail
* Give ability for the node to send files to some network location (Video recordings, etc...)
* Give ability for the node to receive files from the test to be used in the test, such as uploading
* Create an end point on the Grid Hub that will point to all the current test sessions, and which node the test is running on


