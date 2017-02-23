package com.rootbeer.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martinmaritsch on 23/02/2017.
 */
public class Datacenter {
  private List<Video> videos;

  public Datacenter() {
    this.videos = new ArrayList<>();
  }

  public void addVideo(Video video) {
    videos.add(video);
  }

  public List<Video> getVideos() {
    return videos;
  }

  public void setVideos(List<Video> videos) {
    this.videos = videos;
  }
}
