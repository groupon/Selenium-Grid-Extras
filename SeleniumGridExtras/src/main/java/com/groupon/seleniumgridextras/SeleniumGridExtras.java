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

import com.google.gson.GsonBuilder;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.ExecuteOSTask;
import com.groupon.seleniumgridextras.tasks.StartGrid;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SeleniumGridExtras {

  public static void main(String[] args) throws Exception {

    RuntimeConfig.load();

    HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);

    List<ExecuteOSTask> tasks = new LinkedList<ExecuteOSTask>();
    for (String module : RuntimeConfig.getConfig().getActivatedModules()) {
      tasks.add((ExecuteOSTask) Class.forName(module).newInstance());
    }

    System.out.println(RuntimeConfig.getSeleniungGridExtrasHomePath());

    System.out.println("=== Initializing Task Modules ===");
    for (final ExecuteOSTask task : tasks) {

      if (task.initialize()) {

        HttpContext context = server.createContext(task.getEndpoint(), new HttpExecutor() {
          @Override
          String execute(Map params) {
            System.out.println(
                "End-point " + task.getEndpoint() + " was called with HTTP params " + params
                    .toString());
            String result = new GsonBuilder().setPrettyPrinting().create().toJson(task.execute(params));
            return result;
          }
        });

        context.getFilters().add(new ParameterFilter());
      }


    }

    System.out.println("=== API documentation ===");
    System.out.println("/api - Located here");
    HttpContext context = server.createContext("/api", new HttpExecutor() {
      @Override
      String execute(Map params) {
        String foo = ApiDocumentation.getApiDocumentation();
        System.out.println(foo);
        return foo;
      }
    });

    if (RuntimeConfig.getConfig().getGrid().getAutoStartHub()) {
      System.out.println("=== Grid Hub was set to Autostart ===");
      ExecuteOSTask grid = new StartGrid();
      System.out.println(grid.execute("hub").toString().toString());

    }

    if (RuntimeConfig.getConfig().getGrid().getAutoStartNode()) {
      System.out.println("=== Grid Node was set to Autostart ===");
      ExecuteOSTask grid = new StartGrid();
      System.out.println(grid.execute("node").toString().toString());
    }

    context.getFilters().add(new ParameterFilter());

    server.setExecutor(null);
    server.start();
    System.out.println("Server has been started");
  }
}

