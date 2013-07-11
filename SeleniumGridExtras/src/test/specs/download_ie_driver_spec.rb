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

shared_examples "Driver Download" do
  it "should default to config version if version is not provided" do
    file = "/tmp/webdriver/iedriver/#{@bit}_#{@ie_default_version}.exe"
    zip = "/tmp/webdriver/iedriver/#{@ie_default_version}#{@bit}.zip"
    FileUtils.rm file if File.exist? file
    FileUtils.rm zip if File.exist? zip
    response = get_json "download_iedriver"
    response["file"].first.should == "#{@bit}_#{@ie_default_version}.exe"
  end

  it "should have the downloaded version on file system" do
    File.exist?(@ie_exe_full_path).should == true
  end

  it "should not download file again if it already exists" do
    response = get_json "download_webdriver?version=#{@ie_version}&bit=#{@bit}"
    (response["source_url"] == nil || response["source_url"] == [""]).should == true
    response["out"].should == ["File already downloaded, will not download again"]
    response["file"].should == ["#{@ie_version}.jar"]
  end

  it "should have a full path to the jar file" do
    @response["file_full_path"].should == [@ie_exe_full_path]
  end

  it "should have file param in return" do
    @response["file"].should == ["#{@bit}_#{@ie_version}.exe"]
  end

  it "should have source url in response" do
    @response["source_url"].should == ["https://selenium.googlecode.com/files/IEDriverServer_#{@bit}_#{@ie_version}.zip"]
  end
  
  
end

describe "DownloadIEDriver.java" do
  
  
  context "32 bit" do
    before(:all) do
      @ie_default_version = get_local_config["iedriver"]["version"]  
      @ie_version = "2.30.0"
      @ie_exe = "webdriver/iedriver/Win32_#{@ie_version}.exe"
      @ie_exe_full_path = "/tmp/#{@ie_exe}"
      @ie_zip = "/tmp/webdriver/iedriver/#{@ie_version}Win32.zip"
      @bit = "Win32"
      FileUtils.rm @ie_exe_full_path if File.exist? @ie_exe_full_path
      FileUtils.rm @ie_zip if File.exist? @ie_zip
      @response = get_json "download_iedriver?version=#{@ie_version}"
    end
  
  
    it_behaves_like "No Errors"  
    it_behaves_like "Driver Download"  
  
  end
  
  
  context "64 bit" do
    before(:all) do
      @ie_default_version = get_local_config["iedriver"]["version"]  
      @ie_version = "2.31.0"
      @ie_exe = "webdriver/iedriver/x64_#{@ie_version}.exe"
      @ie_exe_full_path = "/tmp/#{@ie_exe}"
      @ie_zip = "/tmp/webdriver/iedriver/#{@ie_version}x64.zip"
      @bit = "x64"
      FileUtils.rm @ie_exe_full_path if File.exist? @ie_exe_full_path
      FileUtils.rm @ie_zip if File.exist? @ie_zip
      @response = get_json "download_iedriver?version=#{@ie_version}&bit=x64"
    end
    
      it_behaves_like "No Errors"  
      it_behaves_like "Driver Download"  
     
  end
  
end