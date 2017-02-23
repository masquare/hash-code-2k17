package com.rootbeer.entity;

import java.util.HashMap;

public class Endpoint {
    private int distanceToDatacenter;
    private HashMap<Integer, Integer> cacheDistances;
    private HashMap<Integer, Integer> requests;

    public Endpoint(int distanceToDatacenter) {
        this.distanceToDatacenter = distanceToDatacenter;
        cacheDistances = new HashMap<>();
        requests = new HashMap<>();
    }

    public int getDistanceToDatacenter() {
        return distanceToDatacenter;
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
        return getDistanceToCache(cacheServerID) != -1;
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
}
