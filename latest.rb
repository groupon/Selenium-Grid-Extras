#!/usr/local/bin/ruby
require 'json'
require 'fileutils'

# assuming latest is first tag entry
latest_json = JSON.parse(`curl -s https://api.github.com/repos/chamiz/Selenium-Grid-Extras/releases`)[0]

latest_json["assets"].each do |asset|
  if asset["content_type"] == "application/java-archive"
    system "curl -sL -o #{asset["name"]} #{asset["browser_download_url"]}" unless File.exist?(asset["name"])
    FileUtils.ln_s asset["name"], 'SeleniumGridExtras-jar-with-dependencies.jar', :force => true if asset["name"] =~ /SeleniumGridExtras-(.*)-jar-with-dependencies.jar/
  end
end
