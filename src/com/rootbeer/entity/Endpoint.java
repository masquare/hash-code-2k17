package com.rootbeer.entity;

import java.util.HashMap;

public class Endpoint {
    private int distanceToDataCenter;
    private HashMap<Integer, Integer> cacheDistances;
    private HashMap<Integer, Integer> requests;

    public Endpoint(int dataServerDistance) {
        distanceToDataCenter = dataServerDistance;
        cacheDistances = new HashMap<>();
        requests = new HashMap<>();
    }

    public void setDistanceToCache(int cache, int distance) {
        cacheDistances.put(cache, distance);
    }

    public int getDistanceToCache(int cache){
        if (cacheDistances.containsKey(cache)) {
            return cacheDistances.get(cache);
        } else {
            return -1;
        }
    }

    public boolean isConnectedToCache(int cacheServerID) {
        return cacheDistances.containsKey(cacheServerID);
    }

    public void setRequests(int video, int quantity) {
        requests.put(video, quantity);
    }

    public int getRequests(int video) {
      if (requests.containsKey(video)) {
        return requests.get(video);
      } else {
        return 0;
      }
    }

    public int getDistanceToDataCenter(){
        return distanceToDataCenter;
    }

    public HashMap<Integer, Integer> getRequests() {
        return requests;
    }

    public void setRequests(HashMap<Integer, Integer> requests) {
        this.requests = requests;
    }

    public int getRequestsForVideo(int id) {
        return requests.getOrDefault(id, 0);
    }
}
