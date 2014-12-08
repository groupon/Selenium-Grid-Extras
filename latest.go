package main

import (
  "io"
  "strings"
  "os"
  "log"
  "encoding/json"
  "net/http"
  "io/ioutil"
)

type Releases []Release

type Release struct {
  URL string
  Assets_URL string
  Upload_URL string
  Html_URL string
  Id int
  Tag_name string
  Target_commitish string
  Name string
  Draft bool
  Authors []Author
  Prelease bool
  Created_at string
  Published_at string
  Assets []Asset
  Tarball_URL string
  Zipball_URL string
  Body string
}

type Author struct {
  Author_info []string
}

type Asset struct {
  URL string
  Id int
  Name string
  Label string
  Uploaders Uploader
  Content_type string
  State string
  Size int
  Download_count int
  Created_at string
  Updated_at string
  Browser_download_URL string
}

type Uploader struct {
  Login string
  Id int
  Avatar_URL string
  Gravatar_id string
  URL string
  Html_URL string
  Followers_URL string
  Following_URL string
  Gists_URL string
  Starred_URL string
  Subscriptions_URL string
  Organizations_URL string
  Repos_URL string
  Events_URL string
  Received_events_URL string
  Type string
  Site_admin bool
}

func get_latest_release() {
  resp, err := http.Get("https://api.github.com/repos/groupon/Selenium-Grid-Extras/releases")
  if err != nil {
    log.Fatal(err)
  }
  defer resp.Body.Close()

  if resp.StatusCode != 200 {
    log.Fatal(resp.Request.URL, " => Response.StatusCode: ", resp.StatusCode)
  }

  body, err := ioutil.ReadAll(resp.Body)
  if err != nil {
    log.Fatal(err)
  }

  var releases Releases
  err = json.Unmarshal(body, &releases)
  if err != nil {
     log.Fatal(err)
  }

  // the first assets is the latest release
  for _, asset := range releases[0].Assets {

    // determine filename
    tokens := strings.Split(asset.Browser_download_URL, "/")
    filename := tokens[len(tokens)-1]

    // SeleniumGridExtras-*-jar-with-dependencies.jar
    if asset.Content_type == "application/java-archive" {

      // prevent re-downloading file if already exists 
      if _, err := os.Stat(filename); os.IsNotExist(err) {
        downloadFromUrl(asset.Browser_download_URL, filename)

        // create symlink to latest release
        os.Symlink(filename, "SeleniumGridExtras-jar-with-dependencies.jar")
      }
      if err != nil {
        log.Fatal(err)
      }
    }
  }
}

func main() {
  get_latest_release()
}

// https://github.com/thbar/golang-playground/blob/master/download-files.go
func downloadFromUrl(url string, fileName string) {
  log.Println("Downloading", url, "to", fileName)

  // TODO: check file existence first with io.IsExist
  output, err := os.Create(fileName)
  if err != nil {
    log.Fatal(err)
  }
  defer output.Close()

  resp, err := http.Get(url)
  if err != nil {
    log.Fatal(err)
  }
  defer resp.Body.Close()

  if resp.StatusCode != 200 {
    log.Fatal(resp.Request.URL, " => Response.StatusCode: ", resp.StatusCode)
  }

  n, err := io.Copy(output, resp.Body)
  if err != nil {
    log.Fatal(err)
  }
  log.Println(n, "bytes downloaded.")
}
