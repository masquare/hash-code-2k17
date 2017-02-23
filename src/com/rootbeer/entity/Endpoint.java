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

    public int getDistanceToDataCenter(){
        return distanceToDataCenter;
    }
}
