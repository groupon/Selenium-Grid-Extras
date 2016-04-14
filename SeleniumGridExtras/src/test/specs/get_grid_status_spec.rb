# Copyright (c) 2013, Groupon, Inc.
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# Redistributions of source code must retain the above copyright notice,
# this list of conditions and the following disclaimer.
#
# Redistributions in binary form must reproduce the above copyright
# notice, this list of conditions and the following disclaimer in the
# documentation and/or other materials provided with the distribution.
#
# Neither the name of GROUPON nor the names of its contributors may be
# used to endorse or promote products derived from this software without
# specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
# IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
# TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
# PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
# TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
# LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
# NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

require File.expand_path('spec_helper', File.dirname(__FILE__))

describe "GridStatus.java" do
  
  before(:all) do  
    @response = get_json "grid_status"
  end
  
  it "should have hub AND node running" do
    #expect(@response["hub_running"]).to eq true
    @response["hub_running"].should == true
	@response["node_running"].should == true
	@response.keys.should include("hub_info")
	@response["hub_info"].keys.should include("pid")
	@response.keys.should include("node_info")
	@response["node_info"].keys.should include("pid")
  end
  
  it "should have hub running false after killing hub pid" do
    #expect(@response["node_running"]).to eq true
	hup_pid = @response["hub_info"]["pid"].to_i
	get_json "kill_pid?id=#{hup_pid}"
	@response_after_killing = get_json "grid_status"
	@response_after_killing["hub_running"].should == false
	@response_after_killing["hub_info"].should == {}
	get_json "start_grid?role=hub"
  end
end