package com.rootbeer.entity;

/**
 * Created by BenKing on 23/02/2017.
 */
public class Endpoint {
    private int[] cacheDistances;
    private int distanceToDataCenter;
    private int[] requests;


    public Endpoint(int[] cacheDistances, int dataServerDistance, int[] requests) {
        this.cacheDistances = cacheDistances;
        this.distanceToDataCenter = dataServerDistance;
        this.requests = requests;
    }

    public void setDistanceToCache(int[] distances){
        this.cacheDistances = distances;
    }

    public void setDistanceToDataCenter(int distance){
        this.distanceToDataCenter = distance;
    }

    public void setRequests(int[] requests){
        this.requests = requests;
    }

    public int[] getDistancesToCache(){
        return this.cacheDistances;
    }

    public int getDistanceToCache(int cacheServerID){
        return this.cacheDistances[cacheServerID];
    }

    public int getDistanceToDataCenter(){
        return this.distanceToDataCenter;
    }

    public int[] getRequests(){
        return this.requests;
    }

    public boolean isConnectedToCache(int cacheServerID) {
        return cacheDistances[cacheServerID] != -1;
    }
}
