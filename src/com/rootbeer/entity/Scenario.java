package com.rootbeer.entity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Scenario {

  private List<Cache> caches;

    private Scenario() {}

    public void calculate() throws Exception {

    }

    public static Scenario parseFromFile(String filename) throws IOException {
        Scenario scenario = new Scenario();

        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            // ...
        }

        return scenario;
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
