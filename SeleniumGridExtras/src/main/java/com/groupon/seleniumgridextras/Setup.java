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

public class Setup extends ExecuteOSTask {

  @Override
  public String getEndpoint() {
    return "/setup";
  }

  @Override
  public String getDescription() {
    return "Calls several pre-defined tasks to act as setup before build";
  }


  @Override
  public String getWindowsCommand() {
    return "";
  }

  @Override
  public String getLinuxCommand() {
    return "ls";
  }

  @Override
  public String execute() {
    String message = "";

//    //OS specific setup
//    if (OSChecker.isWindows()) {
//
//      message = KillAllIE.execute();
//    } else {
//      message = "On non windows box";
//    }
//
//    //Global setup
//    message = message + MoveMouse.execute();

    return message;
  }

  @Override
  public boolean initialize() {
    Boolean initialized = true;
    System.out.println("Setup Tasks");

    for (String module : RuntimeConfig.getSetupModules()) {
      try {
        ExecuteOSTask foo = (ExecuteOSTask) Class.forName(module).newInstance();
        System.out.println("    " + foo.getClass().getSimpleName());
      } catch (ClassNotFoundException error) {
        System.out.println(module + "   " + error);
        initialized = false;
      } catch (InstantiationException error) {
        System.out.println(module + "   " + error);
        initialized = false;
      } catch (IllegalAccessException error) {
        System.out.println(module + "   " + error);
        initialized = false;
      }
    }

    if (initialized.equals(false)) {
      printInitilizedFailure();
      System.exit(1);
    }

    return true;

  }

}
