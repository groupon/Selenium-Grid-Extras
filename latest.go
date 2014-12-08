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
  Url string
  Assets_url string
  Upload_url string
  Html_url string
  Id int
  Tag_name string
  Target_commitish string
  Name string
  Draft bool
  Authors []Author
  Prelease bool
  Created_at int64
  Published_at int64
  Assets []Asset
  Tarball_url string
  Zipball_url string
  Body string
}

type Author struct {
  Author_info []string
}

type Asset struct {
  Url string
  Id int
  Name string
  Label string
  Uploader []Upload
  Content_type string
  State string
  Size int
  Download_count int
  Created_at int64
  Updated_at int64
  Browser_download_url string
}

type Upload struct {
  Upload_info []string
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
  json.Unmarshal(body, &releases)

  // the first assets is the latest release
  for _, asset := range releases[0].Assets {

    // determine filename
    tokens := strings.Split(asset.Browser_download_url, "/")
    filename := tokens[len(tokens)-1]

    // SeleniumGridExtras-*-jar-with-dependencies.jar
    if asset.Content_type == "application/java-archive" {

      // prevent re-downloading file if already exists 
      if _, err := os.Stat(filename); os.IsNotExist(err) {
        downloadFromUrl(asset.Browser_download_url, filename)

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
