package com.rootbeer.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
    ArrayList<Cache> caches;
    ArrayList<Endpoint> endpoints;

    public Scenario(String filename) throws IOException {
        datacenter = new Datacenter();
        caches = new ArrayList<>();
        endpoints = new ArrayList<>();

        // Read from the input file.
        try (FileInputStream inputStream = new FileInputStream(new File(filename))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Decode and store the global properties of the scenario.
            List<Integer> values = decodeInts(reader.readLine());
            videoCount= values.get(0);
            endpointCount = values.get(1);
            requestCount = values.get(2);
            cacheCount = values.get(3);
            cacheCapacity = values.get(4);

            // Create the caches.
            for (int i = 0; i < cacheCount; i++) {
                caches.add(new Cache(i, cacheCapacity));
            }

            // Decode and store the video sizes.
            int[] v = { 0 };
            Arrays.stream(reader.readLine().split(" "))
                    .map(Integer::decode)
                    .forEach(size -> {
                        datacenter.addVideo(new Video(v[0]++, size));
                    });

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

    }

    public void writeToFile(String fileName) throws IOException {
        PrintStream ps = new PrintStream(new FileOutputStream(fileName));

        // filter unused caches
        Stream<Cache> usedCaches = caches.parallelStream().filter(c -> !c.getVideos().isEmpty());

        // print used cache count
        ps.println(usedCaches.count());

        usedCaches.forEach(c -> {
          // format: "<cacheid> <videoid_1> .. <videoid_n>"
          String line = c.getId() + " ";

          c.getVideos().forEach(v -> ps.print(v.getId() + " "));

          line = line.substring(0, line.length() - 1); // remove last space
          ps.println(line);
        });

        ps.flush();
        ps.close();
    }
}
