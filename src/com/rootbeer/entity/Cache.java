package com.rootbeer.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martinmaritsch on 23/02/2017.
 */
public class Cache {
  private int id;
  private int size;
  private List<Video> videos;

  public Cache(int id, int size) {
    this.id = id;
    this.size = size;
    this.videos = new ArrayList<>();
  }

  public boolean videoFits(Video video) {
    return video.getSize() <= size - videos.stream().mapToInt(Video::getSize).sum();
  }

  public void addVideo(Video video) throws Exception {
    if(!videoFits(video))
      throw new Exception("video does not fit into cache");

    videos.add(video);
  }

  public boolean removeVideo(Video video) {
    return videos.remove(video);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public List<Video> getVideos() {
    return videos;
  }

  public void setVideos(List<Video> videos) throws Exception {
    if(videos.stream().mapToInt(Video::getSize).sum() > this.size)
      throw new Exception("Videos do not fit in");

    this.videos = videos;
  }
}
