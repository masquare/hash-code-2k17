package com.rootbeer;

import com.rootbeer.entity.Scenario;

public class MainAll {

    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.out.println("Usage: executable.jar <inputfile> <outputfile>");
            return;
        }

        String[] files = new String[] {
            //"kittens",
            "me_at_the_zoo",
            "trending_today",
            "videos_worth_spreading"
        };

        for(String file : files) {
          System.out.println("Starting " + file);
          Scenario scenario = new Scenario("data/" + file + ".in");
          scenario.calculate();
          scenario.writeToFile("res/" + file + ".txt");
          System.out.println("Finished " + file);
        }




        System.out.println("Done");
    }
}
