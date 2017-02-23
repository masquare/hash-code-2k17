package com.rootbeer.entity;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import java.util.stream.Collectors;

public class Scenario {

  private static List<Integer> decodeInts(String line) {
    return Arrays.stream(line.split(" "))
        .map(Integer::decode)
        .collect(Collectors.toList());
  }

  private int videoCount, endpointCount, requestCount, cacheCount, cacheCapacity;
  private Datacenter datacenter;
  private ArrayList<Endpoint> endpoints;
  private ArrayList<Cache> caches;

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

  private TreeMap<Long, Video> getMostPopularVideos() {
    TreeMap<Long, Video> popularities = new TreeMap<>((o1, o2) -> (int) (o2 - o1));

    datacenter.getVideos().parallelStream().forEach(v -> {
          long sum = endpoints.parallelStream().mapToLong(e -> e.getRequestsForVideo(v.getId())).sum();
          popularities.put(sum, v);
        }
    );

    return popularities;
  }

  private void placePopularVideosInAllCaches(TreeMap<Long, Video> videos, HashMap<Video, Long> counts, Cache cache) throws Exception {
    TreeMap<Long, Video> added = new TreeMap<>((o1, o2) -> (int) (o2 - o1));

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

  public long getLoadTimeForVideo(Endpoint endpoint, int videoID) {
    long cacheDistance = caches.parallelStream()
        .mapToLong(c -> c.containsVideo(videoID) ? endpoint.getDistanceToCache(c.getID()) : Long.MAX_VALUE)
        .min()
        .orElse(Long.MAX_VALUE);

    return Long.min(cacheDistance, endpoint.getDistanceToDataCenter());
  }


}
