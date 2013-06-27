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
require "base64"

describe "GetInfoForPort.java" do
  
  
  before(:all) do  
    @response = get_json "screenshot"
  end
  
  
  it "should have PNG as default file_type" do
    @response["file_type"].first.should == "PNG"
  end
  
  it "should return PNG response consistently after many runs" do
    #There was a bug where after first run through file_type would return as nil
    response = get_json "screenshot"
    response["file_type"].first.should == "PNG"
  end
  
  it "should have valid BASE-64 string in response" do
    expect { Base64.decode64(@response["image"].first) }.to_not raise_error
  end
  
  it "should not have an empty file field" do
    @response["file"].first.should_not == nil
    @response["file"].first.should_not == ""    
  end
  
  it "should have created an image locally" do
    @response["file"].first.include?("shared/").should == true
    File.exist?(@response["file"].first).should == true
  end
  
  
  
  it_behaves_like "No Errors"
end