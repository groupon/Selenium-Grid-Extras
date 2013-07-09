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

describe "DownloadWebdriver.java" do
  
  before(:all) do
    @wd_default_version = get_local_config["webdriver"]["version"]  
    @wd_version = "2.30.0"
    @wd_jar = "webdriver/#{@wd_version}.jar"
    @wd_jar_full_path = "/tmp/#{@wd_jar}"
    FileUtils.rm @wd_jar_full_path if File.exist? @wd_jar_full_path
    @response = get_json "download_webdriver?version=#{@wd_version}"
  end
  
  it "should default to config version if version is not provided" do
    response = get_json "download_webdriver"
    response["file"].first.should == "#{@wd_default_version}.jar"
  end
  
  it "should have the downloaded version on file system" do
    File.exist?(@wd_jar_full_path).should == true
  end
  
  it "should not download file again if it already exists" do
    response = get_json "download_webdriver?version=#{@wd_version}"
    response["source_url"].should == nil
    response["out"].should == ["File already downloaded, will not download again"]
    response["file"].should == ["#{@wd_version}.jar"]
  end
  
  it "should have a full path to the jar file" do
    @response["file_full_path"].should == [@wd_jar_full_path]
  end
  
  it_behaves_like "No Errors"  
  
  it "should have file param in return" do
    @response["file"].should == ["#{@wd_version}.jar"]
  end
  
  it "should have source url in response" do
    @response["source_url"].should == ["http://selenium.googlecode.com/files/selenium-server-standalone-#{@wd_version}.jar"]
  end
  
end