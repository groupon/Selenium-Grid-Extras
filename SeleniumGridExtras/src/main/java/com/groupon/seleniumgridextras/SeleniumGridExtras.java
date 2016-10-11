/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */

package com.groupon.seleniumgridextras;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.grid.SelfHealingGrid;
import com.groupon.seleniumgridextras.homepage.HtmlRenderer;
import com.groupon.seleniumgridextras.tasks.ExecuteOSTask;
import com.groupon.seleniumgridextras.tasks.StartGrid;
import com.groupon.seleniumgridextras.utilities.TempUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import com.groupon.seleniumgridextras.utilities.shutdownhooks.CleanTempShutdownHook;
import com.groupon.seleniumgridextras.utilities.shutdownhooks.VideoShutdownHook;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SeleniumGridExtras {

    public static final String START_UP_COMPLETE = 
            "\nSelenium Grid Extras has been started!\nNavigate to http://%s:%s for more details";
    private static Logger logger = Logger.getLogger(SeleniumGridExtras.class);

    public static void main(String[] args) throws Exception {
        final String filename = "log4j.properties";
        PropertyConfigurator.configure(SeleniumGridExtras.class.getClassLoader().getResource(filename));
        logger.info("Loaded Grid Logger from " + filename);

        RuntimeConfig.load(true);

        SelfHealingGrid.checkStatus(RuntimeConfig.getGridExtrasPort(), RuntimeConfig.getConfig());

        HttpServer
                server =
                HttpServer.create(new InetSocketAddress(RuntimeConfig.getGridExtrasPort()), 0);

        List<ExecuteOSTask> tasks = new LinkedList<ExecuteOSTask>();
        for (String module : RuntimeConfig.getConfig().getActivatedModules()) {
            tasks.add((ExecuteOSTask) Class.forName(module).newInstance());
        }

        logger.debug(RuntimeConfig.getSeleniungGridExtrasHomePath());

        logger.info("Initializing Task Modules");
        for (final ExecuteOSTask task : tasks) {

            if (task.initialize()) {

                HttpContext context = server.createContext(task.getEndpoint(), new HttpExecutor() {
                    @Override
                    String execute(Map params) {

                        logger.debug(
                                "End-point " + task.getEndpoint() + " was called with HTTP params " + params
                                        .toString());
                        String result = JsonParserWrapper.prettyPrintString(task.execute(params));
                        logger.debug(result);
                        return result;
                    }
                });

                context.getFilters().add(new ParameterFilter());
            }


        }

        logger.info("API documentation");
        logger.info("/api - Located here");
        HttpContext context = server.createContext("/api", new HttpExecutor() {
            @Override
            String execute(Map params) {
                String apiDocs = ApiDocumentation.getApiDocumentation();
                logger.debug(apiDocs);
                return apiDocs;
            }
        });

        HttpContext homePageContext = server.createContext("/", new HtmlHttpExecutor() {
            @Override
            String execute(Map params) {
                return new HtmlRenderer(params).toString();
            }
        });

        HttpContext videoContext = server.createContext(VideoHttpExecutor.GET_VIDEO_FILE_ENDPOINT, new VideoHttpExecutor());
        logger.info("Attaching video downloading context at " + VideoHttpExecutor.GET_VIDEO_FILE_ENDPOINT);

        if (RuntimeConfig.getConfig().getAutoStartHub()) {
            logger.info("Grid Hub was set to Autostart");
            ExecuteOSTask grid = new StartGrid();
            logger.info(grid.execute("hub").toString().toString());

        }

        if (RuntimeConfig.getConfig().getAutoStartNode()) {
            logger.info("Grid NodeConfig was set to Autostart");
            ExecuteOSTask grid = new StartGrid();
            logger.info(grid.execute("node").toString().toString());
        }

        context.getFilters().add(new ParameterFilter());
        homePageContext.getFilters().add(new ParameterFilter());
        videoContext.getFilters().add(new ParameterFilter());

        server.setExecutor(null);
        server.start();


        System.out.println(String.format(START_UP_COMPLETE, 
            RuntimeConfig.getOS().getHostIp(),
            RuntimeConfig.getGridExtrasPort()));
        logger.info(String.format(START_UP_COMPLETE, 
            RuntimeConfig.getOS().getHostIp(),
            RuntimeConfig.getGridExtrasPort()));

        new VideoShutdownHook().attachShutDownHook();
        new CleanTempShutdownHook(TempUtility.getWindowsTempForCurrentUser()).attachShutDownHook();
    }
}

