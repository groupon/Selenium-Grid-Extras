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


import com.groupon.seleniumgridextras.tasks.KillAllIE;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KillAllIETest {

    public static final String TASKKILL_F_T_IM_WER_FAULT = "taskkill -F -T -IM WerFault*";
    private KillAllIE task;
    private String expectedIEDriverCommand = "taskkill -F -T -IM iedriver*";
    private String expectedIECommand = "taskkill -F -T -IM iexplore*";
    private String expectedHistoryCommand = "RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 4351";

    @Before
    public void setUp() throws Exception {
        task = new KillAllIE();
    }

    @Test
    public void testGetDescription() throws Exception {

        assertEquals("Executes os level kill command on all instance of Internet Explorer",
                task.getDescription());
    }

    @Test
    public void testGetEndpoint() throws Exception {
        assertEquals("/kill_ie", task.getEndpoint());
    }

    @Test
    public void testGetKillDriverCommand() throws Exception {
        assertEquals(expectedIEDriverCommand, task.getKillDriverCommand());
    }

    @Test
    public void testGetKillIECommand() throws Exception{
        assertEquals(expectedIECommand, task.getKillIECommand());
    }

    @Test
    public void testGetClearHistoryCommand() throws Exception{
        assertEquals(expectedHistoryCommand, task.getClearHistoryCommand());
    }

    @Test
    public void testGetKillCrashReportCommand() throws Exception{
        assertEquals(TASKKILL_F_T_IM_WER_FAULT, task.getKillCrashReportCommand());
    }

}
