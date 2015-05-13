package com.groupon.seleniumgridextras.config;

import java.awt.*;
import java.io.File;
import java.util.HashMap;


public class VideoRecordingOptions extends HashMap<String, String> {


  private static final String FRAME_SECONDS = "frameSeconds";
  private static final String FRAMES = "frames";
  private static final String WIDTH = "width";
  private static final String HEIGHT = "height";
  private static final String DOWNLOAD_VIDEOS_TO_HUB = "download_videos_to_hub";
  private static final String VIDEOS_TO_KEEP = "videos_to_keep";
  private static final String VIDEO_OUTPUT_DIR = "video_output_dir";
  private static final String IDLE_VIDEO_TIMEOUT = "idle_video_timeout";
  private static final String RECORD_TEST_VIDEOS = "record_test_videos";
  private static final String TITLE_FRAME_FONT_COLOR = "title_frame_font_color";
  private static final String LOWER_THIRD_BACKGROUND_COLOR = "lower_third_background_color";
  private static final String LOWER_THIRD_FONT_COLOR = "lower_third_font_color";

  public VideoRecordingOptions() {
  }

  public int getFrames() {
    return Integer.valueOf(this.get(FRAMES));
  }

  public int getSecondsPerFrame() {
    return Integer.valueOf(this.get(FRAME_SECONDS));
  }

  public void setFrameRate(int frames, int perSeconds) {
    this.put(FRAMES, String.valueOf(frames));
    this.put(FRAME_SECONDS, String.valueOf(perSeconds));
  }

  public void setOutputDimensions(int width, int height) {
    this.put(WIDTH, String.valueOf(width));
    this.put(HEIGHT, String.valueOf(height));
  }

  public int getWidth() {
    return Integer.valueOf(this.get(WIDTH));
  }

  public int getHeight() {
    return Integer.valueOf(this.get(HEIGHT));
  }

  public boolean getDownloadVideosToHub() {
    return Boolean.valueOf(this.get(DOWNLOAD_VIDEOS_TO_HUB));
  }

  public void setDownloadVideosToHub(boolean downloadVideosToHub) {
    this.put(DOWNLOAD_VIDEOS_TO_HUB, String.valueOf(downloadVideosToHub));
  }

  public void setVideosToKeep(int count) {
    this.put(VIDEOS_TO_KEEP, String.valueOf(count));
  }

  public int getVideosToKeep() {
    return Integer.valueOf(this.get(VIDEOS_TO_KEEP));
  }

  public void setOutputDir(String outputDir) {
    this.put(VIDEO_OUTPUT_DIR, outputDir);
  }

  public File getOutputDir() {
    return new File(this.get(VIDEO_OUTPUT_DIR));
  }

  public void setIdleTimeout(int idleTimeoutInSeconds) {
    this.put(IDLE_VIDEO_TIMEOUT, String.valueOf(idleTimeoutInSeconds));
  }

  public int getIdleTimeout() {
    return Integer.valueOf(this.get(IDLE_VIDEO_TIMEOUT));
  }

  public void setRecordTestVideos(boolean recordTests) {
    this.put(RECORD_TEST_VIDEOS, String.valueOf(recordTests));
  }

  public boolean getRecordTestVideos() {
    return Boolean.valueOf(this.get(RECORD_TEST_VIDEOS));
  }

  public void setTitleFrameFontColor(int r, int g, int b, int a) {
    this.put(TITLE_FRAME_FONT_COLOR, r + "," + g + "," + b + "," + a);
  }

  public Color getTitleFrameFontColor() {
    return convertStringToColor(this.get(TITLE_FRAME_FONT_COLOR));
  }

  public void setLowerThirdBackgroundColor(int r, int g, int b, int a) {
    this.put(LOWER_THIRD_BACKGROUND_COLOR, r + "," + g + "," + b + "," + a);
  }

  public Color getLowerThirdBackgroundColor() {
    return convertStringToColor(this.get(LOWER_THIRD_BACKGROUND_COLOR));
  }

  public void setLowerThirdFontColor(int r, int g, int b, int a) {
    this.put(LOWER_THIRD_FONT_COLOR, r + "," + g + "," + b + "," + a);
  }

  public Color getLowerThirdFontColor(){
    return convertStringToColor(this.get(LOWER_THIRD_FONT_COLOR));
  }

  private Color convertStringToColor(String input) {
    String[] colorArray = input.split(",");

    return new Color(Integer.valueOf(colorArray[0]), Integer.valueOf(colorArray[1]),
                     Integer.valueOf(colorArray[2]), Integer.valueOf(colorArray[3]));
  }




}
