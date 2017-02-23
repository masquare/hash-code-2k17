package com.rootbeer.entity;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import java.util.stream.Collectors;

public class Scenario {

  public static List<Integer> decodeInts(String line) {
    return Arrays.stream(line.split(" "))
        .map(Integer::decode)
        .collect(Collectors.toList());
  }

  int videoCount, endpointCount, requestCount, cacheCount, cacheCapacity;
  Datacenter datacenter;
  ArrayList<Endpoint> endpoints;
  ArrayList<Cache> caches;

  public Scenario(String filename) throws IOException {
    datacenter = new Datacenter();
    caches = new ArrayList<>();
    endpoints = new ArrayList<>();

    // Read from the input file.
    try (FileInputStream inputStream = new FileInputStream(new File(filename))) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

      // Decode and store the global properties of the scenario.
      List<Integer> values = decodeInts(reader.readLine());
      videoCount = values.get(0);
      endpointCount = values.get(1);
      requestCount = values.get(2);
      cacheCount = values.get(3);
      cacheCapacity = values.get(4);

      // Create the caches.
      for (int i = 0; i < cacheCount; i++) {
        caches.add(new Cache(i, cacheCapacity));
      }

      // Decode and store the video sizes.
      int[] v = {0};
      Arrays.stream(reader.readLine().split(" "))
          .map(Integer::decode)
          .forEach(size -> datacenter.addVideo(new Video(v[0]++, size)));

      // Decode and store each endpoint sequentially.
      for (int i = 0; i < endpointCount; i++) {
        values = decodeInts(reader.readLine());
        Endpoint endpoint = new Endpoint(values.get(0));
        int connectedCaches = values.get(1);
        for (int j = 0; j < connectedCaches; j++) {
          values = decodeInts(reader.readLine());
          endpoint.setDistanceToCache(values.get(0), values.get(1));
        }
        endpoints.add(endpoint);
      }

      // Decode and store each request in the relevant endpoint.
      for (int i = 0; i < requestCount; i++) {
        values = decodeInts(reader.readLine());
        endpoints.get(values.get(1)).setRequests(values.get(0), values.get(2));
      }

      // We should now be at the end of the file.
      assert reader.read() == -1;
    }
  }

  public void calculate() {
      /*caches.parallelStream().forEach(c -> {
        try {
            placeVideosInCache(datacenter.getVideos(), c, endpoints);
        } catch (Exception e) {
            e.printStackTrace();
        }
      });*/

      /*caches.parallelStream().forEach(c -> {
        try {
          placeVideosInCacheRound2(datacenter.getVideos(), c, endpoints);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });*/

      /*for(int i = 0; i < 50; i++) {
        System.out.println("i = " + i);
        datacenter.getVideos().stream().forEach(v -> {
          try {
            placeVideoInCache(v);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      }*/

    TreeMap<Long, Video> popular = getMostPopularVideos();

    HashMap<Video, Long> counts = new HashMap<>();

    caches.forEach(c -> {
      try {
        placePopularVideosInAllCaches(popular, counts, c);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public TreeMap<Long, Video> getMostPopularVideos() {
    TreeMap<Long, Video> popularities = new TreeMap<>(new Comparator<Long>() {
      @Override
      public int compare(Long o1, Long o2) {
        return (int) (o2 - o1);
      }
    });

    datacenter.getVideos().parallelStream().forEach(v -> {
          long sum = endpoints.parallelStream().mapToLong(e -> e.getRequestsForVideo(v.getId())).sum();
          popularities.put(sum, v);
        }
    );

    //popularities.forEach((k, v) -> System.out.println(v + "->" + k));

    return popularities;
  }

  public void placePopularVideosInAllCaches(TreeMap<Long, Video> videos, HashMap<Video, Long> counts, Cache cache) throws Exception {
    TreeMap<Long, Video> added = new TreeMap<>(new Comparator<Long>() {
      @Override
      public int compare(Long o1, Long o2) {
        return (int) (o2 - o1);
      }
    });

    System.out.println("placePopularVideosInAllCaches: " + cache.toString());

    for (Map.Entry<Long, Video> entry : videos.entrySet()) {
      if (cache.videoFits(entry.getValue()) && endpoints.parallelStream()
          .anyMatch(e -> e.isConnectedToCache(cache.getID()))) {
        cache.addVideo(entry.getValue());
        added.put(entry.getKey(), entry.getValue());

        if(counts.containsKey(entry.getValue()))
          counts.put(entry.getValue(), counts.get(entry.getValue()) + 1);
        else
          counts.put(entry.getValue(), (long) 1);
      }
    }

    for (Map.Entry<Long, Video> entry : added.entrySet()) {
      videos.remove(entry.getKey(), entry.getValue());
      videos.put((long) ((15.0 + counts.get(entry.getValue())) / (130.0 + counts.get(entry.getValue())) * entry.getKey()), entry.getValue());
    }
  }

  public long calculateFinalScore() {
    return endpoints.parallelStream().mapToLong(endpoint -> {
      Set<Integer> videoIDs = endpoint.getRequests().keySet();
      long score = 0;
      for (Integer videoID : videoIDs) {
        long loadtime = getLoadTimeForVideo(endpoint, videoID);
        score += (endpoint.getDistanceToDataCenter() - loadtime) * endpoint.getRequestsForVideo(videoID);
      }
      return score;
    }).sum();
  }

  public void writeToFile(String fileName) throws IOException {
    PrintStream ps = new PrintStream(new FileOutputStream(fileName));

    // filter unused caches
    //Stream<Cache> usedCaches = caches.stream().filter(c -> !c.getVideos().isEmpty());

    // print used cache count
    ps.println(caches.stream().filter(c -> !c.getVideos().isEmpty()).count());

    caches.stream().filter(c -> !c.getVideos().isEmpty()).forEach(c -> {

      System.out.println("Cache: " + c);

      // format: "<cacheid> <videoid_1> .. <videoid_n>"
      String line = c.getID() + " ";

      for (Video v : c.getVideos()) {
        line += v.getId() + " ";
      }

      line = line.substring(0, line.length() - 1); // remove last space
      ps.println(line);
    });

    ps.flush();
    ps.close();
  }

  public void placeVideoInCache(Video video) throws Exception {
    // scores is a map of score for a cache, score is highest for last element
    TreeMap<Double, Cache> scores = new TreeMap<>();

    caches.parallelStream().filter(c -> c.videoFits(video) && !c.containsVideo(video.getId())).forEach(cache -> {
      double score = endpoints.parallelStream()
          .filter(e -> e.isConnectedToCache(cache.getID()))
          .mapToDouble(e -> e.getRequestsForVideo(video.getId()) / e.getDistanceToCache(cache.getID()))
          .sum();
      scores.put(score, cache);
    });

    if (!scores.isEmpty()) {
      Map.Entry<Double, Cache> entry = scores.pollLastEntry();
      entry.getValue().addVideo(video);
    } else {
      System.out.println("No cache found for video " + video);
    }

    //System.out.println("placeVideoInCache: " + entry.getValue().toString());
  }


  public void placeVideosInCache(List<Video> videos, Cache cache, List<Endpoint> endpoints) throws Exception {

    System.out.println("placeVideosInCache: " + cache.toString());

    // scores is a map of score for a video, score is highest for last element
    TreeMap<Double, Video> scores = new TreeMap<>();

    videos.parallelStream().filter(v -> cache.videoFits(v) && !cache.containsVideo(v.getId())).forEach(video -> {
      // TODO Bernd: useful score function
      double score = endpoints.parallelStream()
          .filter(e -> e.isConnectedToCache(cache.getID()) && e.getRequestsForVideo(video.getId()) > 0)
          .mapToDouble(e ->
              e.getRequestsForVideo(video.getId())
                  * Math.max(e.getDistanceToDataCenter() - e.getDistanceToCache(cache.getID()), 0)
                  / video.getSize())
          .sum();

      scores.put(score, video);
    });


    while (!scores.isEmpty()) {
      Map.Entry<Double, Video> entry = scores.pollLastEntry();
      if (!cache.videoFits(entry.getValue()))
        continue;

      cache.addVideo(entry.getValue());
    }
  }

  public void placeVideosInCacheRound2(List<Video> videos, Cache cache, List<Endpoint> endpoints) throws Exception {

    System.out.println("placeVideosInCache2: " + cache.toString());

    // scores is a map of score for a video, score is highest for last element
    TreeMap<Double, Video> scores = new TreeMap<>();

    videos.parallelStream().filter(v -> cache.videoFits(v) && !cache.containsVideo(v.getId())).forEach(video -> {
      // TODO Bernd: useful score function
      double score = endpoints.parallelStream()
          .filter(e -> e.isConnectedToCache(cache.getID()) && e.getRequestsForVideo(video.getId()) > 0)
          .mapToDouble(e ->
              e.getRequestsForVideo(video.getId())
                  * Math.max(getLoadTimeForVideo(e, video.getId()) - e.getDistanceToCache(cache.getID()), 0)
                  / video.getSize())
          .sum();

      scores.put(score, video);
    });


    while (!scores.isEmpty()) {
      Map.Entry<Double, Video> entry = scores.pollLastEntry();
      if (!cache.videoFits(entry.getValue()))
        continue;

      cache.addVideo(entry.getValue());
    }
  }

  public long getLoadTimeForVideo(Endpoint endpoint, int videoID) {
    long cacheDistance = caches.parallelStream()
        .mapToLong(c -> c.containsVideo(videoID) ? endpoint.getDistanceToCache(c.getID()) : Long.MAX_VALUE)
        .min()
        .orElse(Long.MAX_VALUE);

    return Long.min(cacheDistance, endpoint.getDistanceToDataCenter());
  }


}
